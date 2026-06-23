package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users") // Tên bảng giữ nguyên theo ERD để DB hiểu
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "so_dien_thoai", nullable = false, length = 15, unique = true)
    private String soDienThoai;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "mat_khau", nullable = false, length = 225)
    private String matKhau;

    @Column(name = "dia_chi", length = 256)
    private String diaChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private VaiTro vaiTro;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai")
    private boolean trangThai;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayTao = LocalDateTime.now();
        this.trangThai = true; // Mặc định mở khóa khi vừa đăng ký
    }
}