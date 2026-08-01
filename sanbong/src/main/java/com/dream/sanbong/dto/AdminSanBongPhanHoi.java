package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Thông tin sân bóng đầy đủ cho màn hình quản lý của Admin.
 * Khác SanBongPhanHoi (dành cho khách) ở chỗ có thêm toạ độ,
 * số khung giờ đã cấu hình và số đơn đã phát sinh.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSanBongPhanHoi {
    private UUID id;
    private String tenSan;
    private Integer loaiSan;
    private String moTa;
    private String diaChi;
    private BigDecimal viDo;
    private BigDecimal kinhDo;
    private String hinhAnh;
    private String trangThai;               // HOAT_DONG | BAO_TRI | DONG_CUA
    private int soKhungGio;                 // số dòng trong bảng giá
    private long soDonDaDat;                // số đơn đặt sân đã phát sinh
    private List<GiaSanPhanHoi> danhSachGia;
}
