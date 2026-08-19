package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Một dòng khách hàng trong bảng quản lý của Admin.
 * Kèm luôn vài số liệu tổng hợp để trang danh sách không phải gọi thêm API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminKhachHangPhanHoi {
    private UUID id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private boolean trangThai;          // true = đang hoạt động, false = đã khoá
    private String ngayTao;             // dd/MM/yyyy HH:mm
    private long soDonDaDat;            // tổng số đơn từng đặt
    private long soDonDangHoatDong;     // đơn chưa huỷ & chưa hoàn thành
    private BigDecimal tongChiTieu;     // tổng tiền các đơn đã hoàn thành
}
