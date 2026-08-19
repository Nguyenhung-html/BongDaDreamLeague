package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XacThucPhanHoi {
    private String thongBao;
    private String token; 
    private UUID idNguoiDung;
    private String hoTen;
    private String vaiTro;
    private String soDienThoai;
    private String avatar;
}