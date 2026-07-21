package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamPhanHoi {
    private UUID id;
    private String tenSanPham;
    private BigDecimal gia;
    private String hinhAnh;
    private String moTa;
    private String loai;
    private boolean conBan;
}