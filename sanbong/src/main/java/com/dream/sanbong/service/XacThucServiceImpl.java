package com.dream.sanbong.service;

import com.dream.sanbong.config.DatabaseSchemaUpdater;
import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.*;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class XacThucServiceImpl implements XacThucService {

    private final NguoiDungRepository nguoiDungRepo;
    private final BCryptPasswordEncoder maHoaMatKhau;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final SmsService smsService;
    private final DatabaseSchemaUpdater databaseSchemaUpdater;

    @Value("${google.client-id:}")
    private String configuredGoogleClientId;

    // Lưu trữ mã OTP trong bộ nhớ tạm (Key -> OtpEntry: "email:xyz" hoặc "sms:098...")
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    private static class OtpEntry {
        private final String code;
        private final LocalDateTime expiryTime;
        private final LocalDateTime createdAt;

        public OtpEntry(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
            this.createdAt = LocalDateTime.now();
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        public boolean isRateLimited() {
            // Giới hạn 60 giây giữa các lần yêu cầu gửi lại mã
            return LocalDateTime.now().isBefore(createdAt.plusSeconds(60));
        }
    }

    public XacThucServiceImpl(NguoiDungRepository nguoiDungRepo, JwtUtils jwtUtils, EmailService emailService, SmsService smsService, DatabaseSchemaUpdater databaseSchemaUpdater) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.maHoaMatKhau = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.smsService = smsService;
        this.databaseSchemaUpdater = databaseSchemaUpdater;
    }

    @Override
    public String xuLyDangKy(DangKyYeuCau yeuCau) {
        if (nguoiDungRepo.existsByEmail(yeuCau.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được đăng ký!");
        }
        if (nguoiDungRepo.existsBySoDienThoai(yeuCau.getSoDienThoai())) {
            throw new RuntimeException("Lỗi: Số điện thoại này đã được đăng ký!");
        }

        String matKhauDaMaHoa = maHoaMatKhau.encode(yeuCau.getMatKhau());

        NguoiDung nguoiDungMoi = NguoiDung.builder()
                .hoTen(yeuCau.getHoTen())
                .soDienThoai(yeuCau.getSoDienThoai())
                .email(yeuCau.getEmail())
                .matKhau(matKhauDaMaHoa)
                .diaChi(yeuCau.getDiaChi())
                .vaiTro(VaiTro.USER)
                .build();

        nguoiDungRepo.save(nguoiDungMoi);
        return "Đăng ký tài khoản thành công!";
    }

    @Override
    public XacThucPhanHoi xuLyDangNhap(DangNhapYeuCau yeuCau) {
        NguoiDung nguoiDung = nguoiDungRepo.findByEmailOrSoDienThoai(yeuCau.getEmail(), yeuCau.getEmail())
                .orElseThrow(() -> new RuntimeException("Lỗi: Tài khoản không tồn tại!"));

        if (!nguoiDung.isTrangThai()) {
            throw new RuntimeException("Lỗi: Tài khoản của bạn đã bị khóa!");
        }

        if (!maHoaMatKhau.matches(yeuCau.getMatKhau(), nguoiDung.getMatKhau())) {
            throw new RuntimeException("Lỗi: Sai mật khẩu!");
        }

        String tokenThiet = jwtUtils.generateToken(nguoiDung.getEmail(), nguoiDung.getVaiTro().name());

        return new XacThucPhanHoi(
                "Đăng nhập thành công!",
                tokenThiet,
                nguoiDung.getId(),
                nguoiDung.getHoTen(),
                nguoiDung.getVaiTro().name(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getAvatar()
        );
    }

    @Override
    public String guiMaOtpQuenMatKhau(GuiMaOtpYeuCau yeuCau) {
        if (yeuCau.getEmail() == null || yeuCau.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập email đã đăng ký tài khoản!");
        }

        String email = yeuCau.getEmail().trim().toLowerCase();

        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Email này chưa được đăng ký trong hệ thống!"));

        if (!nguoiDung.isTrangThai()) {
            throw new RuntimeException("Lỗi: Tài khoản này hiện đang bị khóa, vui lòng liên hệ ban quản trị!");
        }

        // Kiểm tra xem có đang bị giới hạn thời gian gửi lại (60s cooldown)
        OtpEntry existing = otpStorage.get(email);
        if (existing != null && existing.isRateLimited()) {
            throw new RuntimeException("Vui lòng đợi 60 giây trước khi yêu cầu gửi lại mã mới!");
        }

        // Tạo mã ngẫu nhiên 6 chữ số (100000 - 999999)
        String maOtp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Lưu OTP với thời hạn 5 phút
        otpStorage.put(email, new OtpEntry(maOtp, LocalDateTime.now().plusMinutes(5)));

        // Gửi email xác thực với tên thương hiệu DreamLeague
        emailService.guiMaOtpQuenMatKhau(nguoiDung.getEmail(), nguoiDung.getHoTen(), maOtp);

        return "Mã xác thực đã được gửi tới email của bạn. Vui lòng kiểm tra hộp thư (kể cả mục Thư rác/Spam)!";
    }

    @Override
    public String kiemTraMaOtp(XacThucOtpYeuCau yeuCau) {
        if (yeuCau.getEmail() == null || yeuCau.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Email không được để trống!");
        }
        if (yeuCau.getMaXacThuc() == null || yeuCau.getMaXacThuc().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập mã xác thực 6 số!");
        }

        String email = yeuCau.getEmail().trim().toLowerCase();
        OtpEntry entry = otpStorage.get(email);

        if (entry == null) {
            throw new RuntimeException("Lỗi: Không tìm thấy yêu cầu xác thực hoặc mã đã hết hạn. Vui lòng gửi lại mã!");
        }

        if (entry.isExpired()) {
            otpStorage.remove(email);
            throw new RuntimeException("Lỗi: Mã xác thực đã hết hạn (chỉ có hiệu lực trong 5 phút). Vui lòng lấy mã mới!");
        }

        if (!entry.getCode().equals(yeuCau.getMaXacThuc().trim())) {
            throw new RuntimeException("Lỗi: Mã xác thực không chính xác!");
        }

        return "Mã xác thực chính xác! Vui lòng nhập mật khẩu mới.";
    }

    @Override
    public String datLaiMatKhau(DatLaiMatKhauYeuCau yeuCau) {
        if (yeuCau.getEmail() == null || yeuCau.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Email không được để trống!");
        }
        if (yeuCau.getMaXacThuc() == null || yeuCau.getMaXacThuc().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Mã xác thực không được để trống!");
        }
        if (yeuCau.getMatKhauMoi() == null || yeuCau.getMatKhauMoi().trim().length() < 6) {
            throw new RuntimeException("Lỗi: Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        String email = yeuCau.getEmail().trim().toLowerCase();
        OtpEntry entry = otpStorage.get(email);

        if (entry == null || entry.isExpired() || !entry.getCode().equals(yeuCau.getMaXacThuc().trim())) {
            throw new RuntimeException("Lỗi: Mã xác thực không hợp lệ hoặc đã hết hạn!");
        }

        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Tài khoản người dùng không tồn tại!"));

        // Mã hóa mật khẩu mới và lưu vào CSDL
        String matKhauMaHoa = maHoaMatKhau.encode(yeuCau.getMatKhauMoi().trim());
        nguoiDung.setMatKhau(matKhauMaHoa);
        nguoiDungRepo.save(nguoiDung);

        // Xóa OTP khỏi bộ nhớ sau khi sử dụng thành công
        otpStorage.remove(email);

        return "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay với mật khẩu mới.";
    }

    // ── CÁC API QUÊN MẬT KHẨU QUA SMS ──────────────────────────────────────────

    @Override
    public String guiMaOtpSmsQuenMatKhau(GuiMaOtpSmsYeuCau yeuCau) {
        if (yeuCau.getSoDienThoai() == null || yeuCau.getSoDienThoai().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập số điện thoại đã đăng ký tài khoản!");
        }

        String soDienThoai = yeuCau.getSoDienThoai().trim();

        NguoiDung nguoiDung = nguoiDungRepo.findBySoDienThoai(soDienThoai)
                .orElseThrow(() -> new RuntimeException("Lỗi: Số điện thoại này chưa được đăng ký trong hệ thống!"));

        if (!nguoiDung.isTrangThai()) {
            throw new RuntimeException("Lỗi: Tài khoản này hiện đang bị khóa, vui lòng liên hệ ban quản trị!");
        }

        String storageKey = "sms:" + soDienThoai;

        // Kiểm tra cooldown 60s
        OtpEntry existing = otpStorage.get(storageKey);
        if (existing != null && existing.isRateLimited()) {
            throw new RuntimeException("Vui lòng đợi 60 giây trước khi yêu cầu gửi lại mã mới!");
        }

        // Tạo mã ngẫu nhiên 6 chữ số (100000 - 999999)
        String maOtp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Lưu OTP với thời hạn 5 phút
        otpStorage.put(storageKey, new OtpEntry(maOtp, LocalDateTime.now().plusMinutes(5)));

        // Gửi SMS OTP qua SmsService (kết nối trực tiếp SMS Gateway)
        smsService.guiMaOtpQuenMatKhau(soDienThoai, nguoiDung.getHoTen(), maOtp);

        return "Mã xác thực OTP đã được gửi tới số điện thoại của bạn qua SMS!";
    }

    @Override
    public String kiemTraMaOtpSms(XacThucOtpSmsYeuCau yeuCau) {
        if (yeuCau.getSoDienThoai() == null || yeuCau.getSoDienThoai().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Số điện thoại không được để trống!");
        }
        if (yeuCau.getMaXacThuc() == null || yeuCau.getMaXacThuc().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Vui lòng nhập mã xác thực 6 số!");
        }

        String soDienThoai = yeuCau.getSoDienThoai().trim();
        String storageKey = "sms:" + soDienThoai;
        OtpEntry entry = otpStorage.get(storageKey);

        if (entry == null) {
            throw new RuntimeException("Lỗi: Không tìm thấy yêu cầu xác thực hoặc mã đã hết hạn. Vui lòng gửi lại mã!");
        }

        if (entry.isExpired()) {
            otpStorage.remove(storageKey);
            throw new RuntimeException("Lỗi: Mã xác thực đã hết hạn (chỉ có hiệu lực trong 5 phút). Vui lòng lấy mã mới!");
        }

        if (!entry.getCode().equals(yeuCau.getMaXacThuc().trim())) {
            throw new RuntimeException("Lỗi: Mã xác thực không chính xác!");
        }

        return "Mã xác thực chính xác! Vui lòng nhập mật khẩu mới.";
    }

    @Override
    public String datLaiMatKhauSms(DatLaiMatKhauSmsYeuCau yeuCau) {
        if (yeuCau.getSoDienThoai() == null || yeuCau.getSoDienThoai().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Số điện thoại không được để trống!");
        }
        if (yeuCau.getMaXacThuc() == null || yeuCau.getMaXacThuc().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Mã xác thực không được để trống!");
        }
        if (yeuCau.getMatKhauMoi() == null || yeuCau.getMatKhauMoi().trim().length() < 6) {
            throw new RuntimeException("Lỗi: Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        String soDienThoai = yeuCau.getSoDienThoai().trim();
        String storageKey = "sms:" + soDienThoai;
        OtpEntry entry = otpStorage.get(storageKey);

        if (entry == null || entry.isExpired() || !entry.getCode().equals(yeuCau.getMaXacThuc().trim())) {
            throw new RuntimeException("Lỗi: Mã xác thực không hợp lệ hoặc đã hết hạn!");
        }

        NguoiDung nguoiDung = nguoiDungRepo.findBySoDienThoai(soDienThoai)
                .orElseThrow(() -> new RuntimeException("Lỗi: Tài khoản người dùng không tồn tại!"));

        // Mã hóa mật khẩu mới và lưu vào CSDL
        String matKhauMaHoa = maHoaMatKhau.encode(yeuCau.getMatKhauMoi().trim());
        nguoiDung.setMatKhau(matKhauMaHoa);
        nguoiDungRepo.save(nguoiDung);

        // Xóa OTP khỏi bộ nhớ sau khi sử dụng thành công
        otpStorage.remove(storageKey);

        return "Đặt lại mật khẩu thành công! Bạn có thể đăng nhập ngay với mật khẩu mới.";
    }

    @Override
    public XacThucPhanHoi xuLyDangNhapGoogle(GoogleLoginYeuCau yeuCau) {
        if (yeuCau.getIdToken() == null || yeuCau.getIdToken().trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Mã xác thực Google ID Token không được để trống!");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String verifyUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + yeuCau.getIdToken().trim();

            @SuppressWarnings("unchecked")
            Map<String, Object> googlePayload = restTemplate.getForObject(verifyUrl, Map.class);

            if (googlePayload == null || !googlePayload.containsKey("email")) {
                throw new RuntimeException("Lỗi: Không thể xác thực tài khoản Google hoặc token không hợp lệ!");
            }

            String email = ((String) googlePayload.get("email")).trim().toLowerCase();
            String name = (String) googlePayload.get("name");
            if (name == null || name.trim().isEmpty()) {
                name = email.split("@")[0];
            }

            // Kiểm tra email_verified
            Object emailVerified = googlePayload.get("email_verified");
            boolean isEmailVerified = emailVerified != null &&
                    ("true".equalsIgnoreCase(String.valueOf(emailVerified)) || Boolean.TRUE.equals(emailVerified));

            if (!isEmailVerified) {
                throw new RuntimeException("Lỗi: Email Google này chưa được xác thực bởi Google!");
            }

            // Kiểm tra aud / azp nếu có
            String aud = (String) googlePayload.get("aud");
            String azp = (String) googlePayload.get("azp");
            if (configuredGoogleClientId != null && !configuredGoogleClientId.trim().isEmpty()) {
                boolean matchAud = configuredGoogleClientId.trim().equals(aud);
                boolean matchAzp = configuredGoogleClientId.trim().equals(azp);
                if (!matchAud && !matchAzp) {
                    throw new RuntimeException("Lỗi: Google Client ID không khớp với ứng dụng!");
                }
            }

            String picture = (String) googlePayload.get("picture");

            // Tìm người dùng trong cơ sở dữ liệu
            NguoiDung nguoiDung = nguoiDungRepo.findByEmail(email).orElse(null);

            if (nguoiDung != null) {
                if (!nguoiDung.isTrangThai()) {
                    throw new RuntimeException("Lỗi: Tài khoản của bạn hiện đang bị khóa!");
                }
                // Nếu tài khoản cũ chưa có avatar và Google có avatar thì cập nhật
                if ((nguoiDung.getAvatar() == null || nguoiDung.getAvatar().trim().isEmpty()) && picture != null) {
                    nguoiDung.setAvatar(picture);
                    nguoiDung = nguoiDungRepo.save(nguoiDung);
                }
            } else {
                // Tự động tạo người dùng mới từ Google
                NguoiDung nguoiDungMoi = NguoiDung.builder()
                        .hoTen(name)
                        .email(email)
                        .soDienThoai(null)
                        .matKhau(maHoaMatKhau.encode(UUID.randomUUID().toString()))
                        .avatar(picture)
                        .vaiTro(VaiTro.USER)
                        .trangThai(true)
                        .build();

                try {
                    nguoiDung = nguoiDungRepo.save(nguoiDungMoi);
                } catch (Exception e) {
                    // Nếu bảng vừa được tạo lại thủ công trong SQL Server khiến cột so_dien_thoai bị NOT NULL
                    databaseSchemaUpdater.capNhatCauTrucDatabase();
                    nguoiDung = nguoiDungRepo.save(nguoiDungMoi);
                }
            }

            String tokenThiet = jwtUtils.generateToken(nguoiDung.getEmail(), nguoiDung.getVaiTro().name());

            return new XacThucPhanHoi(
                    "Đăng nhập bằng Google thành công!",
                    tokenThiet,
                    nguoiDung.getId(),
                    nguoiDung.getHoTen(),
                    nguoiDung.getVaiTro().name(),
                    nguoiDung.getSoDienThoai(),
                    nguoiDung.getAvatar()
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi xác thực Google: " + e.getMessage());
        }
    }
}