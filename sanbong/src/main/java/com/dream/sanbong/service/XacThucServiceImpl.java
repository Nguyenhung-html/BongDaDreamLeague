package com.dream.sanbong.service;

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

    public XacThucServiceImpl(NguoiDungRepository nguoiDungRepo) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.maHoaMatKhau = new BCryptPasswordEncoder();
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
        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(yeuCau.getEmail())
                .orElseThrow(() -> new RuntimeException("Lỗi: Email không tồn tại!"));

        if (!nguoiDung.isTrangThai()) {
            throw new RuntimeException("Lỗi: Tài khoản của bạn đã bị khóa!");
        }

        if (!maHoaMatKhau.matches(yeuCau.getMatKhau(), nguoiDung.getMatKhau())) {
            throw new RuntimeException("Lỗi: Sai mật khẩu!");
        }

        // Tạm thời trả về token rỗng, sau này ghép JWT thực tế vào đây
        String tokenGiaLap = "chu-gi-do-rat-dai-cua-jwt";

        return new XacThucPhanHoi(
                "Đăng nhập thành công!",
                tokenGiaLap,
                nguoiDung.getId(),
                nguoiDung.getHoTen(),
                nguoiDung.getVaiTro().name()
        );
    }
}