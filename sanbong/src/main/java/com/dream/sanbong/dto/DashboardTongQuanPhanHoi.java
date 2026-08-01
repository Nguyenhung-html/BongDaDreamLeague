package com.dream.sanbong.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardTongQuanPhanHoi {
    private long soLuotDat;
    private BigDecimal tongDoanhThu;
    private long soKhachHangMoi;
}