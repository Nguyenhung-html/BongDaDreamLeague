package com.dream.sanbong.service;

import com.dream.sanbong.dto.LichSanPhanHoi;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StaffSanBongService {
    /** Lấy lịch sân theo ngày — trả về tất cả sân + danh sách đặt trong ngày */
    List<LichSanPhanHoi> layLichSanTheoNgay(LocalDate ngay);

    /** Khóa/mở sân: trangThai = HOAT_DONG | BAO_TRI */
    String capNhatTrangThaiSan(UUID sanBongId, String trangThai, String lyDo, String emailStaff);
}
