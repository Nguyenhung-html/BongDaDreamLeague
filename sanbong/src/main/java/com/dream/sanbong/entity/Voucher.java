package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "VOUCHER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voucher {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name="ma", nullable=false, unique=true, length=50) private String ma;
    @Column(name="ten", nullable=false, length=200) private String ten;
    @Column(name="mo_ta", length=500) private String moTa;
    @Column(name="loai_giam", nullable=false, length=20) private String loaiGiam;
    @Column(name="gia_tri_giam", nullable=false, precision=18, scale=2) private BigDecimal giaTriGiam;
    @Column(name="giam_toi_da", precision=18, scale=2) private BigDecimal giamToiDa;
    @Column(name="don_toi_thieu", precision=18, scale=2) private BigDecimal donToiThieu;
    @Column(name="doi_tuong_khach_hang", nullable=false, length=20) private String doiTuongKhachHang;
    @Column(name="so_luong", nullable=false) private Integer soLuong;
    @Column(name="da_su_dung", nullable=false) private Integer daSuDung;
    @Column(name="gioi_han_moi_khach", nullable=false) private Integer gioiHanMoiKhach;
    @Column(name="ngay_bat_dau", nullable=false) private LocalDateTime ngayBatDau;
    @Column(name="ngay_ket_thuc", nullable=false) private LocalDateTime ngayKetThuc;
    @Column(name="trang_thai", nullable=false, length=20) private String trangThai;
}
