package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Một dòng trong hoá đơn: tiền sân, một lần gia hạn giờ,
 * hoặc một món đồ uống / dịch vụ khách đã gọi.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DongHoaDonPhanHoi {
    private String loai;        // TIEN_SAN | GIA_HAN | DICH_VU
    private String moTa;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
