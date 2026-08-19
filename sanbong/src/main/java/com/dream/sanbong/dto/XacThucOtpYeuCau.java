package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class XacThucOtpYeuCau {
    @NotBlank(message = "Email không được để trống!")
    private String email;

    @NotBlank(message = "Mã xác thực không được để trống!")
    private String maXacThuc;
}