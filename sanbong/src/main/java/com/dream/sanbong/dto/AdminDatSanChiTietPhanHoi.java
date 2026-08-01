package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Chi tiết đầy đủ 1 đơn đặt sân: thông tin đơn + dịch vụ đã gọi
 * + lịch sử gia hạn giờ + các giao dịch thanh toán liên quan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDatSanChiTietPhanHoi {
    private AdminDatSanPhanHoi thongTinDon;
    private List<ChiTietDichVuPhanHoi> danhSachDichVu;
    private BigDecimal tongTienDichVu;
    private List<LichSuGiaHanPhanHoi> danhSachGiaHan;
    private List<AdminThanhToanPhanHoi> danhSachThanhToan;
    private BigDecimal tongPhaiThu;   // tiền sân + dịch vụ
    private BigDecimal daThanhToan;
    private BigDecimal conLai;
}
