package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Số liệu tổng hợp cho màn hình đối soát giao dịch thanh toán của Admin */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminThanhToanTongHopPhanHoi {
    private long tongGiaoDich;
    private long soThanhCong;
    private long soDangCho;
    private long soThatBai;
    private long soHoanTien;

    private BigDecimal tienThanhCong;   // tổng tiền đã thực thu
    private BigDecimal tienDangCho;     // tổng tiền đang chờ xác nhận
    private BigDecimal tienHoanTien;    // tổng tiền đã hoàn lại cho khách

    private BigDecimal tienMat;         // thực thu bằng TIEN_MAT
    private BigDecimal tienChuyenKhoan; // thực thu bằng QR
}
