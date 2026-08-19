package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANH_GIA_HE_THONG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGiaHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nguoi_dung_id")
    private UUID nguoiDungId;

    @Column(name = "ten_nguoi_dung", nullable = false)
    private String tenNguoiDung;

    @Column(name = "vai_tro", nullable = false)
    private String vaiTro;

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Builder.Default
    @Column(name = "hien_thi")
    private Boolean hienThi = true;

    @Builder.Default
    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia = LocalDateTime.now();

    // --- BỔ SUNG DÀNH CHO PHẢN HỒI ---
    @Column(name = "phan_hoi", columnDefinition = "NVARCHAR(MAX)")
    private String phanHoi;

    @Column(name = "ngay_phan_hoi")
    private LocalDateTime ngayPhanHoi;

    @Column(name = "nguoi_phan_hoi_id")
    private UUID nguoiPhanHoiId;
}