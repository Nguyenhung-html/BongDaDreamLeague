package com.dream.sanbong.dto;
import lombok.*; import java.math.BigDecimal;
@Getter @AllArgsConstructor @Builder public class VoucherKiemTraPhanHoi {
    private boolean hopLe; private String ma, ten, thongBao, hangKhachHang; private long soLuotDat; private Integer nguongHang; private BigDecimal soTienGiam, donHangSauGiam;
}
