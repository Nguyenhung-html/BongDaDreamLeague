package com.dream.sanbong.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoanhThuChiTietDTO {
    private BigDecimal tongDoanhThu;
    private long donHoanThanh;
    private long donDaHuy;
    private double tyLeHuy;
    private List<ThongKeDTO> doanhThuTheoNgay;    // Line chart (ngày trong tháng)
    private List<ChiTietSanDTO> chiTietTheoSan;   // Bảng chi tiết
}
