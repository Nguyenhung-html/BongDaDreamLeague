package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "SAN_BONG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanBong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten_san", nullable = false, length = 100)
    private String tenSan;

    @Column(name = "loai_san", nullable = false)
    private Integer loaiSan; // 5 hoặc 7

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "dia_chi", nullable = false, length = 255)
    private String diaChi;

    @Column(name = "vi_do", precision = 10, scale = 8)
    private java.math.BigDecimal viDo;

    @Column(name = "kinh_do", precision = 11, scale = 8)
    private java.math.BigDecimal kinhDo;

    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;

    @Column(name = "trang_thai", length = 20)
    private String trangThai; // HOAT_DONG | BAO_TRI | DONG_CUA
}
