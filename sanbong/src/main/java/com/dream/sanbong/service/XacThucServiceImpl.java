package com.dream.sanbong.service;

import com.dream.sanbong.config.JwtUtils; 
import com.dream.sanbong.dto.DangKyYeuCau;
import com.dream.sanbong.dto.DangNhapYeuCau;
import com.dream.sanbong.dto.XacThucPhanHoi;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class XacThucServiceImpl implements XacThucService {

    private final NguoiDungRepository nguoiDungRepo;
    private final BCryptPasswordEncoder maHoaMatKhau;
    private final JwtUtils jwtUtils; 

    public XacThucServiceImpl(NguoiDungRepository nguoiDungRepo, JwtUtils jwtUtils) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.maHoaMatKhau = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
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
        
        // ĐÃ SỬA CHỖ NÀY: Gọi hàm tìm bằng Email HOẶC Số điện thoại.
        // Vì Vue truyền chuỗi nhập (có thể là email hoặc sđt) vào biến "email",
        // nên ta ném biến đó vào cả 2 tham số để Spring tự lục tìm trong database.
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
                nguoiDung.getVaiTro().name()
        );
    }
}