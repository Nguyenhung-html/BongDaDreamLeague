package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DatSanPhanHoi {
    private UUID datSanId;
    private UUID thanhToanId;
    private String tenSan;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal tongTien;
    private BigDecimal tienCoc;
    private String trangThai;
    private String thongBao;
}
