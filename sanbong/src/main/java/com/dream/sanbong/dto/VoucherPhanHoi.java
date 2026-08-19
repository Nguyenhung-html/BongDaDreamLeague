package com.dream.sanbong.dto;
import lombok.*;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.UUID;
@Getter @AllArgsConstructor @Builder public class VoucherPhanHoi {
    private UUID id; private String ma, ten, moTa, loaiGiam; private BigDecimal giaTriGiam, giamToiDa, donToiThieu;
    private String doiTuongKhachHang; private Integer soLuong, daSuDung, gioiHanMoiKhach; private LocalDateTime ngayBatDau, ngayKetThuc; private String trangThai;
}
