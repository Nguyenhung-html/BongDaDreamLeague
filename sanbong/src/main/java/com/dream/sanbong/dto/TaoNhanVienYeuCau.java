package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin tạo tài khoản nhân viên mới.
 * vaiTro để trống sẽ mặc định là STAFF.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaoNhanVienYeuCau {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhau;
    private String diaChi;
    private String vaiTro; // STAFF | ADMIN
}
