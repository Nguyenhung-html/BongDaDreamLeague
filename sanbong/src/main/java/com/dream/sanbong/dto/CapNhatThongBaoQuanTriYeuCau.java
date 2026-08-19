package com.dream.sanbong.dto;

import com.dream.sanbong.entity.DoiTuongNhan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Trạng thái hiển thị/ẩn KHÔNG nằm trong DTO này — đổi qua 2 endpoint riêng
 * PUT /{id}/hien và PUT /{id}/an, cùng kiểu với khoá/mở khoá tài khoản khách hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatThongBaoQuanTriYeuCau {
    private String tieuDe;
    private String noiDung;
    private DoiTuongNhan doiTuongNhan;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}