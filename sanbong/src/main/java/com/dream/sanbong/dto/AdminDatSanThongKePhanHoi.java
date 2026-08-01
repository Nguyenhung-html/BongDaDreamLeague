package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Số liệu tổng hợp cho màn hình quản lý đơn đặt sân của Admin */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDatSanThongKePhanHoi {
    private long tongDon;
    private long choXacNhan;
    private long daCoc;
    private long hoanThanh;
    private long daHuy;

    private BigDecimal doanhThuHoanThanh;  // tổng tiền sân của các đơn HOAN_THANH
    private BigDecimal tienCocDangGiu;     // tiền cọc của các đơn DA_COC
}
