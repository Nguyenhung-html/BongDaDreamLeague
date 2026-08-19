package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="SU_DUNG_VOUCHER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuDungVoucher {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="voucher_id", nullable=false) private Voucher voucher;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="nguoi_dung_id", nullable=false) private NguoiDung nguoiDung;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="dat_san_id") private DatSan datSan;
    @Column(name="so_tien_giam", nullable=false, precision=18, scale=2) private BigDecimal soTienGiam;
    @Column(name="ngay_su_dung", nullable=false) private LocalDateTime ngaySuDung;
    @PrePersist void prePersist(){ if(ngaySuDung==null) ngaySuDung=LocalDateTime.now(); }
}
