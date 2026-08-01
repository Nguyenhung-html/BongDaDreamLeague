package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Hoá đơn đầy đủ để in / xuất file: phần đầu là thông tin tổng,
 * phần thân là các dòng chi tiết, phần cuối là các lần thanh toán.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietPhanHoi {
    private HoaDonPhanHoi thongTinChung;
    private List<DongHoaDonPhanHoi> cacDong;
    private List<AdminThanhToanPhanHoi> danhSachThanhToan;
}
