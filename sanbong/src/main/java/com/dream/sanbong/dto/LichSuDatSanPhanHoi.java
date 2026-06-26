package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LichSuDatSanPhanHoi {
    private UUID id;
    private String tenSan;
    private Integer loaiSan;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal tongTien;
    private BigDecimal tienCoc;
    private String trangThai;
    private String ngayDat;
    private boolean coTheHuy; // còn > 20 phút trước giờ đá
}
