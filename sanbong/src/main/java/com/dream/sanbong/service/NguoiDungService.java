package com.dream.sanbong.service;

import com.dream.sanbong.dto.CapNhatProfileYeuCau;
import com.dream.sanbong.dto.DoiMatKhauYeuCau;
import com.dream.sanbong.dto.ProfilePhanHoi;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Xem thông tin cá nhân
    public ProfilePhanHoi getProfile(UUID nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        return ProfilePhanHoi.fromEntity(nguoiDung);
    }

    // Cập nhật thông tin cá nhân
    @Transactional
    public ProfilePhanHoi updateProfile(UUID nguoiDungId, CapNhatProfileYeuCau yeuCau) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (yeuCau.getHoTen() != null) {
            String hoTen = yeuCau.getHoTen().trim();
            if (hoTen.length() < 2) {
                throw new RuntimeException("Họ và tên phải có ít nhất 2 ký tự!");
            }
            nguoiDung.setHoTen(hoTen);
        }

        if (yeuCau.getSoDienThoai() != null && !yeuCau.getSoDienThoai().trim().isEmpty()) {
            String sdt = yeuCau.getSoDienThoai().trim();
            if (!sdt.matches("^0\\d{9}$")) {
                throw new RuntimeException("Số điện thoại không hợp lệ (phải gồm 10 số và bắt đầu bằng số 0)!");
            }
            // Nếu đổi sang SĐT khác, kiểm tra xem đã có người khác dùng chưa
            if (!sdt.equals(nguoiDung.getSoDienThoai()) && nguoiDungRepository.existsBySoDienThoai(sdt)) {
                throw new RuntimeException("Lỗi: Số điện thoại này đã được sử dụng bởi tài khoản khác!");
            }
            nguoiDung.setSoDienThoai(sdt);
        }

        if (yeuCau.getEmail() != null && !yeuCau.getEmail().trim().isEmpty()) {
            String email = yeuCau.getEmail().trim().toLowerCase();
            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new RuntimeException("Email không đúng định dạng!");
            }
            // Nếu đổi sang Email khác, kiểm tra xem đã có người khác dùng chưa
            if (!email.equals(nguoiDung.getEmail()) && nguoiDungRepository.existsByEmail(email)) {
                throw new RuntimeException("Lỗi: Email này đã được sử dụng bởi tài khoản khác!");
            }
            nguoiDung.setEmail(email);
        }

        if (yeuCau.getDiaChi() != null) {
            nguoiDung.setDiaChi(yeuCau.getDiaChi().trim());
        }

        if (yeuCau.getAvatar() != null) {
            nguoiDung.setAvatar(yeuCau.getAvatar().trim());
        }

        NguoiDung updated = nguoiDungRepository.save(nguoiDung);
        return ProfilePhanHoi.fromEntity(updated);
    }

    // Đổi mật khẩu
    @Transactional
    public String doiMatKhau(UUID nguoiDungId, DoiMatKhauYeuCau yeuCau) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (yeuCau.getMatKhauHienTai() == null || yeuCau.getMatKhauHienTai().trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mật khẩu hiện tại!");
        }

        if (yeuCau.getMatKhauMoi() == null || yeuCau.getMatKhauMoi().trim().length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        if (!passwordEncoder.matches(yeuCau.getMatKhauHienTai(), nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác!");
        }

        String matKhauMoiMaHoa = passwordEncoder.encode(yeuCau.getMatKhauMoi().trim());
        nguoiDung.setMatKhau(matKhauMoiMaHoa);
        nguoiDungRepository.save(nguoiDung);

        return "Đổi mật khẩu thành công!";
    }
}