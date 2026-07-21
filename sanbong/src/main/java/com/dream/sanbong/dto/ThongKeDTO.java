package com.dream.sanbong.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThongKeDTO {
    private String nhan;        // Label: "Tháng 1", "01", "2026-07-15"...
    private BigDecimal giaTri;  // Doanh thu hoặc số lượng
}
