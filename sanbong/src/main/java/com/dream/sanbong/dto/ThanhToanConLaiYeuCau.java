package com.dream.sanbong.dto;

import java.util.UUID;

public class ThanhToanConLaiYeuCau {
    private UUID datSanId;
    private String phuongThuc; // TIEN_MAT | QR

    public ThanhToanConLaiYeuCau() {}

    public UUID getDatSanId() { return datSanId; }
    public void setDatSanId(UUID datSanId) { this.datSanId = datSanId; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }
}
