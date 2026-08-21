package com.dream.sanbong.dto;

import com.dream.sanbong.entity.Message.LoaiNguoiGui;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {
    private UUID id;
    private UUID ticketId;
    private UUID nguoiGuiId;
    private LoaiNguoiGui loaiNguoiGui;
    private String noiDung;
    private LocalDateTime thoiGianGui;
    private boolean daDoc;
    private LocalDateTime thoiGianDoc;
}