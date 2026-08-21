package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Message {

    // --- ENUM KHAI BÁO TRỰC TIẾP TRONG ENTITY ---
    public enum LoaiNguoiGui {
        KHACH_HANG, STAFF, ADMIN, BOT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "nguoi_gui_id")
    private UUID nguoiGuiId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_nguoi_gui", nullable = false)
    private LoaiNguoiGui loaiNguoiGui;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String noiDung;

    @Column(name = "thoi_gian_gui")
    private LocalDateTime thoiGianGui = LocalDateTime.now();

    @Column(name = "da_doc")
    private boolean daDoc = false;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;
}