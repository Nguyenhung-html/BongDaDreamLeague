package com.dream.sanbong.dto;

import com.dream.sanbong.entity.DoiTuongNhan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThongBaoQuanTriPhanHoi {
    private UUID id;
    private String tieuDe;
    private String noiDung;
    private DoiTuongNhan doiTuongNhan;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean hienThi;
    private LocalDateTime ngayTao;
}