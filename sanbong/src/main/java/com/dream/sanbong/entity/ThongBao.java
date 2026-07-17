package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "THONG_BAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false,
                foreignKey = @ForeignKey(name = "FK_THONG_BAO_USER"))
    private NguoiDung nguoiDung;

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "noi_dung", length = 500)
    private String noiDung;

    // "booking" | "system" | "promotion"
    @Column(name = "loai", length = 30)
    private String loai;

    @Column(name = "da_doc", nullable = false)
    private boolean daDoc;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    protected void khiTaoMoi() {
        this.ngayTao = LocalDateTime.now();
        this.daDoc = false;
    }
}