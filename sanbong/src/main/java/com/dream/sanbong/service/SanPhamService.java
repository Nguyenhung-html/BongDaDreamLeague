package com.dream.sanbong.service;

import com.dream.sanbong.dto.SanPhamPhanHoi;
import com.dream.sanbong.dto.SanPhamYeuCau;

import java.util.List;
import java.util.UUID;

public interface SanPhamService {

    // Cho khách xem menu (chỉ sản phẩm còn bán)
    List<SanPhamPhanHoi> layDanhSachConBan();

    // Cho Admin xem toàn bộ (kể cả ngừng bán)
    List<SanPhamPhanHoi> layTatCa();

    SanPhamPhanHoi them(SanPhamYeuCau yeuCau);

    SanPhamPhanHoi sua(UUID id, SanPhamYeuCau yeuCau);

    void xoa(UUID id);
}