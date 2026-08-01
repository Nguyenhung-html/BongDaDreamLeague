package com.dream.sanbong.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DatSanYeuCau {
    private UUID sanBongId;
    private String hoTenDat;
    private String soDienThoai;
    private String ngayDa;       // "2026-06-28"
    private String gioBatDau;    // "18:00"
    private String gioKetThuc;   // "19:00"
    private String phuongThuc;   // "QR" | "TIEN_MAT"
}
