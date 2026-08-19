package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKePhanHoi {
    private String nhan;            // nhãn hiển thị (ví dụ: "Tháng 1", "Sân 1",...)
    private BigDecimal giaTri;      // giá trị thống kê (doanh thu hoặc số lượt đặt)
}