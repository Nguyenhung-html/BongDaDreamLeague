package com.dream.sanbong.service;

import com.dream.sanbong.dto.AdminNhanVienPhanHoi;
import com.dream.sanbong.dto.CapNhatNhanVienYeuCau;
import com.dream.sanbong.dto.TaoNhanVienYeuCau;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.ThongBaoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ Admin quản lý tài khoản nội bộ (STAFF và ADMIN).
 * Có vài chốt an toàn để Admin không tự khoá / tự xoá mình,
 * và để hệ thống không bao giờ rơi vào trạng thái không còn Admin nào.
 */
@Service
public class AdminNhanVienService {

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 6;

    private final NguoiDungRepository nguoiDungRepo;
    private final DatSanRepository datSanRepo;
    private final ThongBaoRepository thongBaoRepo;
    private final PasswordEncoder maHoaMatKhau;

    public AdminNhanVienService(NguoiDungRepository nguoiDungRepo,
                                DatSanRepository datSanRepo,
                                ThongBaoRepository thongBaoRepo,
                                PasswordEncoder maHoaMatKhau) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.datSanRepo = datSanRepo;
        this.thongBaoRepo = thongBaoRepo;
        this.maHoaMatKhau = maHoaMatKhau;
    }

    /**
     * Danh sách nhân viên (STAFF + ADMIN), lọc tuỳ chọn theo từ khoá và vai trò.
     *
     * @param tuKhoa chuỗi tìm theo tên / email / SĐT, có thể null
     * @param vaiTro "STAFF" | "ADMIN" | null (lấy cả hai)
     */
    public List<AdminNhanVienPhanHoi> layDanhSach(String tuKhoa, String vaiTro) {
        List<NguoiDung> danhSach = nguoiDungRepo.findByVaiTroInOrderByNgayTaoDesc(
                List.of(VaiTro.STAFF, VaiTro.ADMIN));

        if (vaiTro != null && !vaiTro.isBlank()) {
            VaiTro loc = doiSangVaiTroNoiBo(vaiTro);
            danhSach = danhSach.stream().filter(nd -> nd.getVaiTro() == loc).toList();
        }

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(nd -> chuaChuoi(nd.getHoTen(), tim)
                            || chuaChuoi(nd.getEmail(), tim)
                            || chuaChuoi(nd.getSoDienThoai(), tim))
                    .toList();
        }

        return danhSach.stream().map(AdminNhanVienPhanHoi::fromEntity).toList();
    }

    public AdminNhanVienPhanHoi layChiTiet(UUID id) {
        return AdminNhanVienPhanHoi.fromEntity(timNhanVien(id));
    }

    /** Tạo tài khoản nhân viên mới, mật khẩu được hash BCrypt trước khi lưu */
    @Transactional
    public AdminNhanVienPhanHoi them(TaoNhanVienYeuCau yeuCau) {
        if (!coGiaTri(yeuCau.getHoTen())) {
            throw new RuntimeException("Lỗi: Vui lòng nhập họ tên nhân viên!");
        }
        if (!coGiaTri(yeuCau.getEmail())) {
            throw new RuntimeException("Lỗi: Vui lòng nhập email!");
        }
        if (!coGiaTri(yeuCau.getSoDienThoai())) {
            throw new RuntimeException("Lỗi: Vui lòng nhập số điện thoại!");
        }
        kiemTraMatKhau(yeuCau.getMatKhau());

        if (nguoiDungRepo.existsByEmail(yeuCau.getEmail())) {
            throw new RuntimeException("Lỗi: Email này đã được đăng ký!");
        }
        if (nguoiDungRepo.existsBySoDienThoai(yeuCau.getSoDienThoai())) {
            throw new RuntimeException("Lỗi: Số điện thoại này đã được đăng ký!");
        }

        VaiTro vaiTro = coGiaTri(yeuCau.getVaiTro())
                ? doiSangVaiTroNoiBo(yeuCau.getVaiTro())
                : VaiTro.STAFF;

        NguoiDung nhanVien = NguoiDung.builder()
                .hoTen(yeuCau.getHoTen())
                .email(yeuCau.getEmail())
                .soDienThoai(yeuCau.getSoDienThoai())
                .matKhau(maHoaMatKhau.encode(yeuCau.getMatKhau()))
                .diaChi(yeuCau.getDiaChi())
                .vaiTro(vaiTro)
                .build();

        return AdminNhanVienPhanHoi.fromEntity(nguoiDungRepo.save(nhanVien));
    }

    /** Sửa thông tin nhân viên, có kiểm tra trùng email / SĐT */
    @Transactional
    public AdminNhanVienPhanHoi capNhat(UUID id, CapNhatNhanVienYeuCau yeuCau, String emailAdminHienTai) {
        NguoiDung nhanVien = timNhanVien(id);

        if (coGiaTri(yeuCau.getEmail()) && !yeuCau.getEmail().equals(nhanVien.getEmail())) {
            if (nguoiDungRepo.existsByEmail(yeuCau.getEmail())) {
                throw new RuntimeException("Lỗi: Email này đã được tài khoản khác sử dụng!");
            }
            nhanVien.setEmail(yeuCau.getEmail());
        }

        if (coGiaTri(yeuCau.getSoDienThoai()) && !yeuCau.getSoDienThoai().equals(nhanVien.getSoDienThoai())) {
            if (nguoiDungRepo.existsBySoDienThoai(yeuCau.getSoDienThoai())) {
                throw new RuntimeException("Lỗi: Số điện thoại này đã được tài khoản khác sử dụng!");
            }
            nhanVien.setSoDienThoai(yeuCau.getSoDienThoai());
        }

        if (coGiaTri(yeuCau.getHoTen())) {
            nhanVien.setHoTen(yeuCau.getHoTen());
        }
        if (yeuCau.getDiaChi() != null) {
            nhanVien.setDiaChi(yeuCau.getDiaChi());
        }

        if (coGiaTri(yeuCau.getVaiTro())) {
            VaiTro vaiTroMoi = doiSangVaiTroNoiBo(yeuCau.getVaiTro());
            if (vaiTroMoi != nhanVien.getVaiTro()) {
                // Hạ quyền chính mình hoặc hạ quyền Admin cuối cùng đều làm hệ thống mất Admin
                if (nhanVien.getVaiTro() == VaiTro.ADMIN) {
                    chanTuThaoTacChinhMinh(nhanVien, emailAdminHienTai,
                            "Lỗi: Bạn không thể tự hạ quyền tài khoản đang đăng nhập!");
                    chanKhiLaAdminCuoiCung("Lỗi: Đây là tài khoản Admin duy nhất còn hoạt động, không thể hạ quyền!");
                }
                nhanVien.setVaiTro(vaiTroMoi);
            }
        }

        return AdminNhanVienPhanHoi.fromEntity(nguoiDungRepo.save(nhanVien));
    }

    /** Khoá / mở khoá tài khoản nhân viên */
    @Transactional
    public AdminNhanVienPhanHoi doiTrangThai(UUID id, boolean kichHoat, String emailAdminHienTai) {
        NguoiDung nhanVien = timNhanVien(id);

        if (nhanVien.isTrangThai() == kichHoat) {
            throw new RuntimeException(kichHoat
                    ? "Lỗi: Tài khoản này đang hoạt động rồi!"
                    : "Lỗi: Tài khoản này đã bị khoá trước đó rồi!");
        }

        if (!kichHoat) {
            chanTuThaoTacChinhMinh(nhanVien, emailAdminHienTai,
                    "Lỗi: Bạn không thể tự khoá tài khoản đang đăng nhập!");
            if (nhanVien.getVaiTro() == VaiTro.ADMIN) {
                chanKhiLaAdminCuoiCung("Lỗi: Đây là tài khoản Admin duy nhất còn hoạt động, không thể khoá!");
            }
        }

        nhanVien.setTrangThai(kichHoat);
        return AdminNhanVienPhanHoi.fromEntity(nguoiDungRepo.save(nhanVien));
    }

    /** Admin đặt lại mật khẩu cho nhân viên */
    @Transactional
    public String datLaiMatKhau(UUID id, String matKhauMoi) {
        NguoiDung nhanVien = timNhanVien(id);
        kiemTraMatKhau(matKhauMoi);

        nhanVien.setMatKhau(maHoaMatKhau.encode(matKhauMoi));
        nguoiDungRepo.save(nhanVien);
        return "Đã đặt lại mật khẩu cho nhân viên " + nhanVien.getHoTen() + "!";
    }

    /**
     * Xoá hẳn tài khoản nhân viên. Chỉ cho phép khi tài khoản chưa từng
     * đứng tên đơn đặt sân nào — vì DAT_SAN có khoá ngoại trỏ tới USERS,
     * xoá đi sẽ mất dấu vết đơn cũ. Trường hợp đó nên khoá thay vì xoá.
     */
    @Transactional
    public String xoa(UUID id, String emailAdminHienTai) {
        NguoiDung nhanVien = timNhanVien(id);

        chanTuThaoTacChinhMinh(nhanVien, emailAdminHienTai,
                "Lỗi: Bạn không thể tự xoá tài khoản đang đăng nhập!");

        if (nhanVien.getVaiTro() == VaiTro.ADMIN) {
            chanKhiLaAdminCuoiCung("Lỗi: Đây là tài khoản Admin duy nhất còn hoạt động, không thể xoá!");
        }

        long soDon = datSanRepo.countByNguoiDungId(nhanVien.getId());
        if (soDon > 0) {
            throw new RuntimeException("Lỗi: Nhân viên này đang đứng tên " + soDon
                    + " đơn đặt sân nên không thể xoá. Hãy khoá tài khoản để ngừng truy cập!");
        }

        // Dọn thông báo trước vì THONG_BAO có khoá ngoại trỏ tới USERS
        thongBaoRepo.deleteByNguoiDungId(nhanVien.getId());
        nguoiDungRepo.delete(nhanVien);
        return "Đã xoá tài khoản nhân viên " + nhanVien.getHoTen() + "!";
    }

    // ── Helpers ───────────────────────────────────────────────────

    private NguoiDung timNhanVien(UUID id) {
        NguoiDung nd = nguoiDungRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy nhân viên!"));
        if (nd.getVaiTro() == VaiTro.USER) {
            throw new RuntimeException("Lỗi: Tài khoản này là khách hàng, không quản lý ở mục nhân viên!");
        }
        return nd;
    }

    private VaiTro doiSangVaiTroNoiBo(String vaiTro) {
        try {
            VaiTro kq = VaiTro.valueOf(vaiTro.trim().toUpperCase());
            if (kq == VaiTro.USER) {
                throw new RuntimeException("Lỗi: Vai trò nhân viên chỉ có thể là STAFF hoặc ADMIN!");
            }
            return kq;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Lỗi: Vai trò không hợp lệ (chỉ chấp nhận STAFF hoặc ADMIN)!");
        }
    }

    private void kiemTraMatKhau(String matKhau) {
        if (matKhau == null || matKhau.length() < DO_DAI_MAT_KHAU_TOI_THIEU) {
            throw new RuntimeException("Lỗi: Mật khẩu phải có ít nhất "
                    + DO_DAI_MAT_KHAU_TOI_THIEU + " ký tự!");
        }
    }

    private void chanTuThaoTacChinhMinh(NguoiDung nhanVien, String emailAdminHienTai, String thongBao) {
        if (emailAdminHienTai != null && emailAdminHienTai.equals(nhanVien.getEmail())) {
            throw new RuntimeException(thongBao);
        }
    }

    private void chanKhiLaAdminCuoiCung(String thongBao) {
        long soAdminConHoatDong = nguoiDungRepo.findByVaiTroIn(List.of(VaiTro.ADMIN))
                .stream()
                .filter(NguoiDung::isTrangThai)
                .count();
        if (soAdminConHoatDong <= 1) {
            throw new RuntimeException(thongBao);
        }
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
