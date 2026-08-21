package com.dream.sanbong.dto;


import com.dream.sanbong.entity.Ticket.TrangThaiTicket;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketDTO {
    private UUID id;
    private String maTicket;
    private UUID khachHangId;
    private String tenKhachHang;
    private String soDienThoai;
    private UUID nhanVienId;
    private String tenNhanVien;
    private String chuDe;
    private TrangThaiTicket trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private MessageDTO tinNhanMoiNhat;
}