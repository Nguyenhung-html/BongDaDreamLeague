package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin sửa thông tin nhân viên. Trường nào null thì giữ nguyên giá trị cũ.
 * Mật khẩu đổi bằng endpoint riêng, không đi qua đây.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatNhanVienYeuCau {
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro; // STAFF | ADMIN
}
