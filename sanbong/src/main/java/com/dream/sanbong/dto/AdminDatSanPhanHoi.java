package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Một dòng đơn đặt sân trong bảng quản lý toàn hệ thống của Admin.
 * So với StaffDatSanPhanHoi thì có thêm thông tin tài khoản đặt và
 * số tiền đã thu / còn lại để Admin đối soát nhanh.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDatSanPhanHoi {
    private UUID id;
    private String maDon;            // 8 ký tự đầu UUID, viết hoa
    private UUID sanBongId;
    private String tenSan;
    private Integer loaiSan;
    private String hoTenDat;
    private String soDienThoai;
    private String emailTaiKhoan;    // email tài khoản đặt (rỗng nếu khách vãng lai)
    private String ngayDa;
    private String gioBatDau;
    private String gioKetThuc;
    private BigDecimal tongTien;     // tiền sân (đã gồm gia hạn giờ)
    private BigDecimal tienCoc;
    private BigDecimal daThanhToan;  // tổng các giao dịch THANH_CONG
    private BigDecimal conLai;       // tổng phải thu - đã thu (gồm cả dịch vụ)
    private String trangThai;        // CHO_XAC_NHAN | DA_COC | HOAN_THANH | DA_HUY
    private String ngayDat;
    private String ngayHuy;
}
