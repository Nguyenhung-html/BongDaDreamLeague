package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "DAT_SAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatSan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_DAT_SAN_USER"))
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_bong_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_DAT_SAN_SAN_BONG"))
    private SanBong sanBong;

    @Column(name = "ho_ten_dat", nullable = false, length = 100)
    private String hoTenDat;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "ngay_da", nullable = false)
    private LocalDate ngayDa;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "tong_tien", nullable = false, precision = 10, scale = 0)
    private BigDecimal tongTien;

    @Column(name = "tien_coc", precision = 10, scale = 0)
    private BigDecimal tienCoc;

    // CHO_XAC_NHAN | DA_XAC_NHAN | DA_HUY | HOAN_THANH
    @Column(name = "trang_thai", length = 30)
    private String trangThai;

    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    @Column(name = "ngay_huy")
    private LocalDateTime ngayHuy;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayDat = LocalDateTime.now();
        if (this.trangThai == null) {
            this.trangThai = "CHO_XAC_NHAN";
        }
        if (this.tienCoc == null) {
            this.tienCoc = BigDecimal.ZERO;
        }
    }
}
