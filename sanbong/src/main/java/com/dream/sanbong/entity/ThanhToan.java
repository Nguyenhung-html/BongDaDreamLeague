package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "THANH_TOAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_san_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_THANH_TOAN_DAT_SAN"))
    private DatSan datSan;

    @Column(name = "so_tien", nullable = false, precision = 10, scale = 0)
    private BigDecimal soTien;

    // COC | THANH_TOAN_CON_LAI
    @Column(name = "loai", nullable = false, length = 20)
    private String loai;

    // QR | TIEN_MAT
    @Column(name = "phuong_thuc", nullable = false, length = 20)
    private String phuongThuc;

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    // THANH_CONG | DANG_CHO | THAT_BAI | HOAN_
    @Column(name = "ngay_hoan_tien")
private LocalDateTime ngayHoanTien;

    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayThanhToan = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = "DANG_CHO";
        }
    }
}
