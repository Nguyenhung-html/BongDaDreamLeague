package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class XacThucOtpSmsYeuCau {
    @NotBlank(message = "Số điện thoại không được để trống!")
    private String soDienThoai;

    @NotBlank(message = "Mã xác thực không được để trống!")
    @Pattern(regexp = "^[0-9]{6}$", message = "Mã xác thực phải gồm đúng 6 chữ số!")
    private String maXacThuc;
}