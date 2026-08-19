package com.dream.sanbong.dto;

import com.dream.sanbong.entity.NguoiDung;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Một dòng nhân viên trong bảng quản lý tài khoản của Admin.
 * Không bao giờ trả về mật khẩu (kể cả bản đã hash).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNhanVienPhanHoi {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private UUID id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro;      // STAFF | ADMIN
    private boolean trangThai;  // true = đang hoạt động, false = đã khoá
    private String ngayTao;

    public static AdminNhanVienPhanHoi fromEntity(NguoiDung nd) {
        return AdminNhanVienPhanHoi.builder()
                .id(nd.getId())
                .hoTen(nd.getHoTen())
                .email(nd.getEmail())
                .soDienThoai(nd.getSoDienThoai())
                .diaChi(nd.getDiaChi())
                .vaiTro(nd.getVaiTro().name())
                .trangThai(nd.isTrangThai())
                .ngayTao(nd.getNgayTao() != null ? nd.getNgayTao().format(FMT) : "")
                .build();
    }
}
