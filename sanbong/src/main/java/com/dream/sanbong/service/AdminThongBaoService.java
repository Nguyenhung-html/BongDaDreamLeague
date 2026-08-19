package com.dream.sanbong.service;

import com.dream.sanbong.dto.CapNhatThongBaoQuanTriYeuCau;
import com.dream.sanbong.dto.TaoThongBaoQuanTriYeuCau;
import com.dream.sanbong.dto.ThongBaoQuanTriPhanHoi;
import com.dream.sanbong.entity.DoiTuongNhan;

import java.util.List;
import java.util.UUID;

public interface AdminThongBaoService {

    /** Lọc theo đối tượng nhận; truyền null để lấy tất cả không phân biệt nhóm. */
    List<ThongBaoQuanTriPhanHoi> layDanhSach(DoiTuongNhan loc);

    ThongBaoQuanTriPhanHoi layChiTiet(UUID id);

    ThongBaoQuanTriPhanHoi them(TaoThongBaoQuanTriYeuCau yeuCau);

    ThongBaoQuanTriPhanHoi capNhat(UUID id, CapNhatThongBaoQuanTriYeuCau yeuCau);

    void xoa(UUID id);

    ThongBaoQuanTriPhanHoi doiTrangThaiHienThi(UUID id, boolean hienThi);
}