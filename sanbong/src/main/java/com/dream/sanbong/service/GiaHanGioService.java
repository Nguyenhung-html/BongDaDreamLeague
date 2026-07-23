package com.dream.sanbong.service;

import com.dream.sanbong.dto.GiaHanGioPhanHoi;

import java.util.UUID;

public interface GiaHanGioService {
    // Gia hạn thêm 30 phút cho 1 đơn đặt sân (Staff làm - CỘNG TIỀN THẬT). Trả về giờ mới + tiền cộng thêm.
    GiaHanGioPhanHoi giaHanThemGio(UUID datSanId, String email);

    // MỚI THÊM: khách bấm yêu cầu gia hạn - CHỈ gửi thông báo cho Staff, không đổi giờ/tiền
    void yeuCauGiaHan(UUID datSanId, String email);
}