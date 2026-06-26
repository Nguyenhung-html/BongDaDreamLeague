package com.dream.sanbong.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class StaffDatSanPhanHoi {

    private UUID id;
    private String maGanDo;       // 8 ký tự đầu UUID để hiển thị
    private String tenSan;
    private Integer loaiSan;
    private String hoTenDat;
    private String soDienThoai;
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal tongTien;
    private BigDecimal tienCoc;
    private String trangThai;
    private String ngayDat;

    public StaffDatSanPhanHoi() {}

    public StaffDatSanPhanHoi(UUID id, String maGanDo, String tenSan, Integer loaiSan,
                               String hoTenDat, String soDienThoai,
                               String ngayDa, String gioBatDau, String gioKetThuc,
                               BigDecimal tongTien, BigDecimal tienCoc,
                               String trangThai, String ngayDat) {
        this.id = id;
        this.maGanDo = maGanDo;
        this.tenSan = tenSan;
        this.loaiSan = loaiSan;
        this.hoTenDat = hoTenDat;
        this.soDienThoai = soDienThoai;
        this.ngayDa = ngayDa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.tongTien = tongTien;
        this.tienCoc = tienCoc;
        this.trangThai = trangThai;
        this.ngayDat = ngayDat;
    }

    // Getters
    public UUID getId() { return id; }
    public String getMaGanDo() { return maGanDo; }
    public String getTenSan() { return tenSan; }
    public Integer getLoaiSan() { return loaiSan; }
    public String getHoTenDat() { return hoTenDat; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getNgayDa() { return ngayDa; }
    public String getGioBatDau() { return gioBatDau; }
    public String getGioKetThuc() { return gioKetThuc; }
    public BigDecimal getTongTien() { return tongTien; }
    public BigDecimal getTienCoc() { return tienCoc; }
    public String getTrangThai() { return trangThai; }
    public String getNgayDat() { return ngayDat; }
}
