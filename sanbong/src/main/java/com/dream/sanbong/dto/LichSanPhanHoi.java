package com.dream.sanbong.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Thông tin một sân + danh sách đơn đặt trong ngày → dùng cho Calendar View
 */
public class LichSanPhanHoi {

    private UUID sanBongId;
    private String tenSan;
    private Integer loaiSan;
    private String trangThaiSan; // HOAT_DONG | BAO_TRI
    private List<SlotDatSan> danhSachDat;

    public LichSanPhanHoi() {}

    public LichSanPhanHoi(UUID sanBongId, String tenSan, Integer loaiSan,
                           String trangThaiSan, List<SlotDatSan> danhSachDat) {
        this.sanBongId = sanBongId;
        this.tenSan = tenSan;
        this.loaiSan = loaiSan;
        this.trangThaiSan = trangThaiSan;
        this.danhSachDat = danhSachDat;
    }

    public UUID getSanBongId() { return sanBongId; }
    public String getTenSan() { return tenSan; }
    public Integer getLoaiSan() { return loaiSan; }
    public String getTrangThaiSan() { return trangThaiSan; }
    public List<SlotDatSan> getDanhSachDat() { return danhSachDat; }

    // ─── Inner class: mỗi khung giờ đã được đặt ──────────────────
    public static class SlotDatSan {
        private UUID datSanId;
        private String hoTenDat;
        private String soDienThoai;
        private String gioBatDau;
        private String gioKetThuc;
        private String trangThai;
        private BigDecimal tongTien;

        public SlotDatSan() {}

        public SlotDatSan(UUID datSanId, String hoTenDat, String soDienThoai,
                           String gioBatDau, String gioKetThuc,
                           String trangThai, BigDecimal tongTien) {
            this.datSanId = datSanId;
            this.hoTenDat = hoTenDat;
            this.soDienThoai = soDienThoai;
            this.gioBatDau = gioBatDau;
            this.gioKetThuc = gioKetThuc;
            this.trangThai = trangThai;
            this.tongTien = tongTien;
        }

        public UUID getDatSanId() { return datSanId; }
        public String getHoTenDat() { return hoTenDat; }
        public String getSoDienThoai() { return soDienThoai; }
        public String getGioBatDau() { return gioBatDau; }
        public String getGioKetThuc() { return gioKetThuc; }
        public String getTrangThai() { return trangThai; }
        public BigDecimal getTongTien() { return tongTien; }
    }
}
