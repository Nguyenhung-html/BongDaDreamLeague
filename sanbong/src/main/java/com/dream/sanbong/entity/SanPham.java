package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SAN_PHAM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten_san_pham", nullable = false, length = 150)
    private String tenSanPham;

    @Column(name = "gia", nullable = false)
    private BigDecimal gia;

    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    // "DO_UONG" | "DICH_VU"
    @Column(name = "loai", nullable = false, length = 30)
    private String loai;

    @Column(name = "con_ban", nullable = false)
    private boolean conBan;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayTao = LocalDateTime.now();
    }
}