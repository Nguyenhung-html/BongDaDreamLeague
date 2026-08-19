package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANH_GIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_bong_id", nullable = false)
    private SanBong sanBong;

    @Column(name = "so_sao", nullable = false)
    private Integer soSao;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "hien_thi")
    private boolean hienThi;

    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayDanhGia = LocalDateTime.now();
        this.hienThi = true; // Mặc định hiển thị công khai
    }
}