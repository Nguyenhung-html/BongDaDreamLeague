package com.dream.sanbong.service;

import com.dream.sanbong.dto.GioHangPhanHoi;
import com.dream.sanbong.dto.ThemDichVuYeuCau;

import java.util.UUID;

public interface DichVuDatSanService {

    // Thêm 1 sản phẩm vào giỏ hàng dịch vụ của 1 đơn đặt sân (email = người đang đăng nhập)
    GioHangPhanHoi themVaoGioHang(UUID datSanId, String email, ThemDichVuYeuCau yeuCau);

    // Lấy toàn bộ giỏ hàng dịch vụ + tổng tiền cần thanh toán của 1 đơn
    GioHangPhanHoi layGioHang(UUID datSanId, String email);

    // Xoá 1 dòng khỏi giỏ hàng (theo id của DichVuDatSan)
    GioHangPhanHoi xoaKhoiGioHang(UUID chiTietId, String email);
}