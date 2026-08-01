package com.dream.sanbong.service;

import com.dream.sanbong.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DatSanService {
    // ── User features ─────────────────────────────────────────────
    DatSanPhanHoi datSan(DatSanYeuCau yeuCau, String emailNguoiDung);
    List<LichSuDatSanPhanHoi> layLichSu(String emailNguoiDung);
    String huySan(UUID datSanId, String emailNguoiDung);

    /** Lấy danh sách khung giờ đã bị đặt của 1 sân trong 1 ngày (để FE disable/làm mờ) */
    List<KhungGioDaDatPhanHoi> layKhungGioDaDat(UUID sanBongId, LocalDate ngay);

    // ── Staff features ────────────────────────────────────────────
    /** Lấy tất cả đơn đặt sân, có thể lọc theo trạng thái và ngày (nullable = không lọc) */
    List<StaffDatSanPhanHoi> layTatCaDon(String trangThai, String ngay);

    /** Staff xác nhận đơn → CHO_XAC_NHAN thành DA_COC, cập nhật thanh toán cọc THANH_CONG */
    String xacNhanDon(UUID datSanId, String emailStaff);

    /** Staff huỷ đơn bất kỳ (có quyền cao hơn user) */
    String staffHuyDon(UUID datSanId, String lyDo, String emailStaff);

    /** Staff đánh dấu hoàn thành (DA_COC → HOAN_THANH) */
    String hoanThanhDon(UUID datSanId, String emailStaff);
}