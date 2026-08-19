package com.dream.sanbong.service;

import com.dream.sanbong.dto.*;

public interface XacThucService {
    String xuLyDangKy(DangKyYeuCau yeuCau);
    XacThucPhanHoi xuLyDangNhap(DangNhapYeuCau yeuCau);

    String guiMaOtpQuenMatKhau(GuiMaOtpYeuCau yeuCau);
    String kiemTraMaOtp(XacThucOtpYeuCau yeuCau);
    String datLaiMatKhau(DatLaiMatKhauYeuCau yeuCau);

    String guiMaOtpSmsQuenMatKhau(GuiMaOtpSmsYeuCau yeuCau);
    String kiemTraMaOtpSms(XacThucOtpSmsYeuCau yeuCau);
    String datLaiMatKhauSms(DatLaiMatKhauSmsYeuCau yeuCau);

    XacThucPhanHoi xuLyDangNhapGoogle(GoogleLoginYeuCau yeuCau);
}
