package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DatLaiMatKhauSmsYeuCau {
    @NotBlank(message = "Số điện thoại không được để trống!")
    private String soDienThoai;

    @NotBlank(message = "Mã xác thực không được để trống!")
    private String maXacThuc;

    @NotBlank(message = "Mật khẩu mới không được để trống!")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự!")
    private String matKhauMoi;
}