package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Số liệu tổng hợp cho màn hình dữ liệu hoá đơn của Admin */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonThongKePhanHoi {
    private long tongHoaDon;
    private long soDaThanhToanDu;
    private long soThanhToanMotPhan;
    private long soChuaThanhToan;

    private BigDecimal tongGiaTri;   // tổng giá trị tất cả hoá đơn
    private BigDecimal tongDaThu;
    private BigDecimal tongConLai;
}
