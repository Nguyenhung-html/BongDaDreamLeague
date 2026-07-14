package com.dream.sanbong.service;

import com.dream.sanbong.dto.SePayReturnPhanHoi;
import com.dream.sanbong.dto.SePayTaoGiaoDichPhanHoi;
import java.util.UUID;

public interface SePayService {
    SePayTaoGiaoDichPhanHoi taoGiaoDich(UUID thanhToanId);
    SePayReturnPhanHoi xuLyReturn(String maGiaoDich, String vnpResponseCode);
    boolean hoanTien(UUID thanhToanId);
}