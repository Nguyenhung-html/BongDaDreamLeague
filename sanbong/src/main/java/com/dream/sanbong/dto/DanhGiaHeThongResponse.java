package com.dream.sanbong.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGiaHeThongResponse {
    private UUID id;
    private String name;
    private String role;
    private Integer rating;
    private String quote;
    private Boolean hienThi;
    private LocalDateTime ngayDanhGia;
    private String phanHoi;
    private LocalDateTime ngayPhanHoi;
    private UUID nguoiPhanHoiId;
    private String tenNguoiPhanHoi;
    private String vaiTroNguoiPhanHoi;
}