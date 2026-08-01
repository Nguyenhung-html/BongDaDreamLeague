package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "GIA_SAN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaSan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "san_bong_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_GIA_SAN_SAN_BONG"))
    private SanBong sanBong;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "gia_tien", nullable = false, precision = 10, scale = 0)
    private BigDecimal giaTien;
}
