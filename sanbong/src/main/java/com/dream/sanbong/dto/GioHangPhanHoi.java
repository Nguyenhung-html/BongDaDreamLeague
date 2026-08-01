package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GioHangPhanHoi {
    private List<ChiTietDichVuPhanHoi> danhSach;
    private BigDecimal tongTienDichVu;      // tổng riêng phần đồ uống/dịch vụ
    private BigDecimal tienConLaiSan;       // 50% tiền sân còn lại (tổng tiền - tiền cọc)
    private BigDecimal tongCanThanhToan;    // tienConLaiSan + tongTienDichVu = số tiền cần trả tại sân
}