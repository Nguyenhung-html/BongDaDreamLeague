package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Admin thêm mới hoặc sửa thông tin sân bóng.
 * Khi sửa, trường nào null thì giữ nguyên giá trị cũ.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanBongYeuCau {
    private String tenSan;
    private Integer loaiSan;   // 5 hoặc 7
    private String moTa;
    private String diaChi;
    private BigDecimal viDo;
    private BigDecimal kinhDo;
    private String hinhAnh;
    private String trangThai;  // HOAT_DONG | BAO_TRI | DONG_CUA
}
