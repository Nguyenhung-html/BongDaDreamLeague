package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GuiMaOtpSmsYeuCau {
    @NotBlank(message = "Số điện thoại không được để trống!")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không đúng định dạng (gồm 10 số, VD: 0987654321)!")
    private String soDienThoai;
}