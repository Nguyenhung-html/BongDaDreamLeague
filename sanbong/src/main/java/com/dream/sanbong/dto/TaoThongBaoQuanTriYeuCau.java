package com.dream.sanbong.dto;

import com.dream.sanbong.entity.DoiTuongNhan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaoThongBaoQuanTriYeuCau {
    private String tieuDe;
    private String noiDung;
    private DoiTuongNhan doiTuongNhan;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean hienThi;
}