package com.dream.sanbong.service;

import com.dream.sanbong.dto.ThongBaoPhanHoi;
import com.dream.sanbong.entity.DatSan;

import java.util.List;
import java.util.UUID;

public interface ThongBaoService {

    List<ThongBaoPhanHoi> layDanhSach(UUID nguoiDungId);

    long demChuaDoc(UUID nguoiDungId);

    void danhDauDaDoc(UUID thongBaoId);

    void danhDauTatCaDaDoc(UUID nguoiDungId);

    // Gọi khi 1 giao dịch đặt cọc được xác nhận thành công (qua webhook SePay)
    // Tạo 1 thông báo cho khách hàng + 1 thông báo cho từng Staff/Admin
    void taoThongBaoThanhToanThanhCong(DatSan datSan);
}