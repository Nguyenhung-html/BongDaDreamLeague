package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuiMaOtpYeuCau {
    @NotBlank(message = "Email không được để trống!")
    private String email;
}