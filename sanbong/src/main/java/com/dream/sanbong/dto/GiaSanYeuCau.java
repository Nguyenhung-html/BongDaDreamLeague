package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Admin thêm / sửa một khung giờ trong bảng giá của sân */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiaSanYeuCau {
    private String gioBatDau;   // "06:00"
    private String gioKetThuc;  // "07:00"
    private BigDecimal giaTien;
}
