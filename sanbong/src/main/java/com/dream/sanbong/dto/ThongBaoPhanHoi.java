package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoPhanHoi {
    private UUID id;
    private String tieuDe;
    private String noiDung;
    private String loai;
    private boolean daDoc;
    private LocalDateTime ngayTao;
}