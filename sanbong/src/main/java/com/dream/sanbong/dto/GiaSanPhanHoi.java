package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GiaSanPhanHoi {
    private UUID id;
    private String gioBatDau;   // "06:00"
    private String gioKetThuc;  // "07:00"
    private BigDecimal giaTien;
}
