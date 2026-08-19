package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thông báo do Admin chủ động tạo để quảng bá / thông tin chung tới một nhóm
 * đối tượng (Tất cả / Người dùng / Nhân viên), có khoảng thời gian hiển thị
 * và có thể ẩn/hiện thủ công.
 *
 * KHÁC với entity ThongBao hiện có (thông báo hệ thống tự sinh riêng cho từng
 * người dùng, ví dụ thanh toán thành công, yêu cầu gia hạn, có trạng thái
 * "đã đọc" riêng của từng người) — hai entity phục vụ 2 mục đích khác nhau
 * nên tách bảng riêng, không gộp chung.
 */
@Entity
@Table(name = "thong_bao_quan_tri")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBaoQuanTri {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String tieuDe;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoiTuongNhan doiTuongNhan;

    private LocalDate ngayBatDau;

    private LocalDate ngayKetThuc;

    @Column(nullable = false)
    private boolean hienThi;

    @Column(updatable = false)
    private LocalDateTime ngayTao;

    private LocalDateTime ngayCapNhat;

    @PrePersist
    void truocKhiTao() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = ngayTao;
    }

    @PreUpdate
    void truocKhiCapNhat() {
        ngayCapNhat = LocalDateTime.now();
    }
}