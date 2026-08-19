package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor // Trả về Constructor đầy đủ tham số ban đầu
public class DanhGiaResponse {
    private UUID id;
    private String tenNguoiDung;
    private int soSao;
    private String noiDung;
    private LocalDateTime ngayDanhGia;
}