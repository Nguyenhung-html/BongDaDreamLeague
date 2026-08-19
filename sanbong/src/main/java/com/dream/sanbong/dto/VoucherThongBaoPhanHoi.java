package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thông tin voucher công khai cho trang Thông báo.
 * Chỉ phục vụ hiển thị, không chứa dữ liệu sử dụng nội bộ.
 */
@Getter
@Builder
@AllArgsConstructor
public class VoucherThongBaoPhanHoi {
    private UUID id;
    private String ma;
    private String ten;
    private String moTa;
    private String loaiGiam;
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private BigDecimal donToiThieu;
    private String doiTuongKhachHang;
    private LocalDateTime ngayKetThuc;
}
