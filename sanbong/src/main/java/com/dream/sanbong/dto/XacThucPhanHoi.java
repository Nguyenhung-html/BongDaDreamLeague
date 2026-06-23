package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class XacThucPhanHoi {
    private String thongBao;
    private String token; 
    private UUID idNguoiDung;
    private String hoTen;
    private String vaiTro;
}
