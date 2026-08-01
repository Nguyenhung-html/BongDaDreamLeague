package com.dream.sanbong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Body dùng chung cho các thao tác đổi trạng thái có kèm lý do.
 * Ví dụ: Staff khoá sân ({"trangThai":"BAO_TRI","lyDo":"Thay cỏ"})
 * hoặc Staff huỷ đơn ({"lyDo":"Khách báo bận"}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapNhatTrangThaiYeuCau {
    private String trangThai;
    private String lyDo;
}
