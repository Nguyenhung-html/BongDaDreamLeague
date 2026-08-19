package com.dream.sanbong.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SanPhamYeuCau {
    private String tenSanPham;
    private BigDecimal gia;
    private String hinhAnh;
    private String moTa;
    private String loai;      // "DO_UONG" | "DICH_VU"
    private boolean conBan;
}