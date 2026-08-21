package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ticket {

    // --- ENUM KHAI BÁO TRỰC TIẾP TRONG ENTITY ---
    public enum TrangThaiTicket {
        CHUA_TIEP_NHAN, DANG_XU_LY, DA_DONG
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma_ticket", unique = true, nullable = false)
    private String maTicket;

    @Column(name = "khach_hang_id")
    private UUID khachHangId;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "nhan_vien_id")
    private UUID nhanVienId;

    @Column(name = "chu_de")
    private String chuDe = "Hỗ trợ đặt sân";

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TrangThaiTicket trangThai = TrangThaiTicket.CHUA_TIEP_NHAN;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat = LocalDateTime.now();
}