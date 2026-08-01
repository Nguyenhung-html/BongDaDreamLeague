package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiaHanGioPhanHoi {
    private LocalTime gioKetThucMoi;
    private BigDecimal soTienThem;
    private BigDecimal tongTienMoi;
    private String thongBao;
}   