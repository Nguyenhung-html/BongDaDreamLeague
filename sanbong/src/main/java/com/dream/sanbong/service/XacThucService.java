package com.dream.sanbong.service;

import com.dream.sanbong.dto.DangKyYeuCau;
import com.dream.sanbong.dto.DangNhapYeuCau;
import com.dream.sanbong.dto.XacThucPhanHoi;

public interface XacThucService {
    String xuLyDangKy(DangKyYeuCau yeuCau);
    XacThucPhanHoi xuLyDangNhap(DangNhapYeuCau yeuCau);
}
