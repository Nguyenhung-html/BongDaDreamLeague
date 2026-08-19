package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Một lần gia hạn thêm giờ của đơn đặt sân */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuGiaHanPhanHoi {
    private UUID id;
    private String gioKetThucCu;
    private String gioKetThucMoi;
    private BigDecimal soTienThem;
    private String ngayGiaHan;
}
