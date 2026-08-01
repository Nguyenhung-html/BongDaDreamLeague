package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Một dòng hoá đơn trong bảng dữ liệu hoá đơn của Admin.
 * Mỗi đơn đặt sân tương ứng đúng một hoá đơn.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonPhanHoi {
    private UUID datSanId;
    private String maHoaDon;            // "HD-" + 8 ký tự đầu UUID đơn
    private String tenKhach;
    private String soDienThoai;
    private String emailTaiKhoan;
    private String tenSan;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;

    private BigDecimal tienSan;         // tiền sân gốc (chưa gồm gia hạn)
    private BigDecimal tienGiaHan;      // tổng tiền các lần gia hạn giờ
    private BigDecimal tienDichVu;      // tổng tiền đồ uống / dịch vụ
    private BigDecimal tongCong;        // tienSan + tienGiaHan + tienDichVu
    private BigDecimal daThanhToan;
    private BigDecimal conLai;

    private String trangThaiDon;        // CHO_XAC_NHAN | DA_COC | HOAN_THANH | DA_HUY
    private String trangThaiThanhToan;  // CHUA_THANH_TOAN | THANH_TOAN_MOT_PHAN | DA_THANH_TOAN_DU
    private String ngayXuat;            // lần thu tiền thành công gần nhất, không có thì lấy ngày đặt
}
