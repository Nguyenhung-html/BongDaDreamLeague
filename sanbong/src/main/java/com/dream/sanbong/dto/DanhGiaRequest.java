package com.dream.sanbong.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class DanhGiaRequest {

    @NotNull(message = "ID sân bóng không được để trống")
    private UUID sanBongId;

    @NotNull(message = "Số sao không được để trống")
    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    private int soSao;

    @Size(max = 500, message = "Nội dung bình luận không quá 500 ký tự")
    private String noiDung;
}