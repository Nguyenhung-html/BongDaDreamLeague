package com.dream.sanbong.service;

import com.dream.sanbong.dto.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DatSanService {
    // ── Giữ chỗ & Hủy giữ chỗ mới ───────────────────────────────────
    DatSanPhanHoi holdSlot(DatSanYeuCau yeuCau, String emailNguoiDung, String clientIp);
    String cancelHold(UUID datSanId, String emailNguoiDung);

    // ── Code cũ giữ nguyên ──────────────────────────────────────────
    DatSanPhanHoi datSan(DatSanYeuCau yeuCau, String emailNguoiDung);
    List<LichSuDatSanPhanHoi> layLichSu(String emailNguoiDung);
    String huySan(UUID datSanId, String emailNguoiDung);
    List<KhungGioDaDatPhanHoi> layKhungGioDaDat(UUID sanBongId, LocalDate ngay);
    
    List<StaffDatSanPhanHoi> layTatCaDon(String trangThai, String ngay);
    String xacNhanDon(UUID datSanId, String emailStaff);
    String staffHuyDon(UUID datSanId, String lyDo, String emailStaff);
    String hoanThanhDon(UUID datSanId, String emailStaff);
}