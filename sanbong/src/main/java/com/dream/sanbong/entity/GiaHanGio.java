package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "GIA_HAN_GIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaHanGio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_san_id", nullable = false)
    private DatSan datSan;

    @Column(name = "gio_ket_thuc_cu", nullable = false)
    private LocalTime gioKetThucCu;

    @Column(name = "gio_ket_thuc_moi", nullable = false)
    private LocalTime gioKetThucMoi;

    @Column(name = "so_tien_them", nullable = false)
    private java.math.BigDecimal soTienThem;

    @Column(name = "ngay_gia_han")
    private LocalDateTime ngayGiaHan;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayGiaHan = LocalDateTime.now();
    }
}