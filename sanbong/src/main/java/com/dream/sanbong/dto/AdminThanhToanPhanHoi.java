package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Một giao dịch thanh toán trong bảng đối soát của Admin.
 * Kèm sẵn thông tin đơn đặt sân gốc để Admin không phải mở thêm màn hình khác.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminThanhToanPhanHoi {
    private UUID id;
    private UUID datSanId;
    private String maDon;
    private String tenKhach;
    private String soDienThoai;
    private String tenSan;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal soTien;
    private String loai;            // COC | THANH_TOAN_CON_LAI
    private String phuongThuc;      // QR | TIEN_MAT
    private String maGiaoDich;
    private String trangThai;       // DANG_CHO | THANH_CONG | THAT_BAI | DA_HOAN_TIEN
    private String ngayThanhToan;
    private String ngayHoanTien;
    private String trangThaiDon;    // trạng thái của đơn đặt sân tương ứng
}
