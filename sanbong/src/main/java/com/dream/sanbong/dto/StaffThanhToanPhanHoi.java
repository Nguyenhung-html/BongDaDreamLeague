package com.dream.sanbong.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class StaffThanhToanPhanHoi {

    private UUID id;
    private UUID datSanId;
    private String maGanDo;
    private String tenKhach;
    private String soDienThoai;
    private String tenSan;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal soTien;
    private String loai;          // COC | THANH_TOAN_CON_LAI
    private String phuongThuc;    // QR | TIEN_MAT
    private String maGiaoDich;
    private String trangThai;     // DANG_CHO | THANH_CONG | THAT_BAI
    private String ngayThanhToan;

    public StaffThanhToanPhanHoi() {}

    public StaffThanhToanPhanHoi(UUID id, UUID datSanId, String maGanDo, String tenKhach,
                                  String soDienThoai, String tenSan, String ngayDa,
                                  String gioBatDau, String gioKetThuc, BigDecimal soTien,
                                  String loai, String phuongThuc, String maGiaoDich,
                                  String trangThai, String ngayThanhToan) {
        this.id = id;
        this.datSanId = datSanId;
        this.maGanDo = maGanDo;
        this.tenKhach = tenKhach;
        this.soDienThoai = soDienThoai;
        this.tenSan = tenSan;
        this.ngayDa = ngayDa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.soTien = soTien;
        this.loai = loai;
        this.phuongThuc = phuongThuc;
        this.maGiaoDich = maGiaoDich;
        this.trangThai = trangThai;
        this.ngayThanhToan = ngayThanhToan;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getDatSanId() { return datSanId; }
    public String getMaGanDo() { return maGanDo; }
    public String getTenKhach() { return tenKhach; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getTenSan() { return tenSan; }
    public String getNgayDa() { return ngayDa; }
    public String getGioBatDau() { return gioBatDau; }
    public String getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getSoTien() { return soTien; }
    public String getLoai() { return loai; }
    public String getPhuongThuc() { return phuongThuc; }
    public String getMaGiaoDich() { return maGiaoDich; }
    public String getTrangThai() { return trangThai; }
    public String getNgayThanhToan() { return ngayThanhToan; }
}
