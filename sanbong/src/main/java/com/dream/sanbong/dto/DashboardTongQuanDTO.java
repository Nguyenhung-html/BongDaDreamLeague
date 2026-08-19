package com.dream.sanbong.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardTongQuanDTO {
    private long tongKhachHang;
    private long datSanHomNay;
    private BigDecimal doanhThuHomNay;
    private List<ThongKeDTO> doanhThuTheoThang;  // 12 điểm — line chart 1
    private List<ThongKeDTO> datSanTheoThang;    // 12 điểm — line chart 2
}
