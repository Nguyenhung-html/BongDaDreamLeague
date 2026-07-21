package com.dream.sanbong.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanDTO {
    private String tenSan;
    private long soLuotDat;
    private double soGio;
    private BigDecimal doanhThu;
}
