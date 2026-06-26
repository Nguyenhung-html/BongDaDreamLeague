package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SanBongPhanHoi {
    private UUID id;
    private String tenSan;
    private Integer loaiSan;
    private String moTa;
    private String diaChi;
    private String hinhAnh;
    private String trangThai;
    private List<GiaSanPhanHoi> danhSachGia;
}
