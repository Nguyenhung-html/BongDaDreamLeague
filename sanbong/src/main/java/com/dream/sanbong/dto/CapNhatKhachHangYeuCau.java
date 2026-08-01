package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin sửa thông tin khách hàng. Trường nào null thì giữ nguyên giá trị cũ.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatKhachHangYeuCau {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
}
