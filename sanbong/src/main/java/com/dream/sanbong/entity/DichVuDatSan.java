package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DICH_VU_DAT_SAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DichVuDatSan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_san_id", nullable = false)
    private DatSan datSan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_pham_id", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    // Snapshot giá tại thời điểm gọi món, phòng khi Admin đổi giá sau này
    @Column(name = "gia", nullable = false)
    private BigDecimal gia;

    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayDat = LocalDateTime.now();
    }
}