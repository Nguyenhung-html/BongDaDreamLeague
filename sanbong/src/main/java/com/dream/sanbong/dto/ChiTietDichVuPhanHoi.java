package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDichVuPhanHoi {
    private UUID id;            // id của dòng DichVuDatSan (dùng để xoá)
    private UUID sanPhamId;
    private String tenSanPham;
    private String hinhAnh;
    private BigDecimal gia;     // giá 1 đơn vị (snapshot lúc gọi)
    private Integer soLuong;
    private BigDecimal thanhTien; // gia * soLuong
}