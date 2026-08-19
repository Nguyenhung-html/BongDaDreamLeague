package com.dream.sanbong.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data public class VoucherYeuCau {
    private String ma; private String ten; private String moTa; private String loaiGiam;
    private BigDecimal giaTriGiam, giamToiDa, donToiThieu;
    private String doiTuongKhachHang; private Integer soLuong, gioiHanMoiKhach;
    private LocalDateTime ngayBatDau, ngayKetThuc; private String trangThai;
}
