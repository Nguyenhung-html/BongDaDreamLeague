package com.dream.sanbong.service;

import com.dream.sanbong.dto.StaffThanhToanPhanHoi;
import com.dream.sanbong.dto.ThanhToanConLaiYeuCau;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StaffThanhToanService {
    /** Lấy danh sách tất cả giao dịch thanh toán (có thể lọc theo ngày) */
    List<StaffThanhToanPhanHoi> layDanhSachThanhToan(LocalDate tuNgay, LocalDate denNgay);

    /** Xác nhận đã nhận tiền cọc → DANG_CHO thành THANH_CONG */
    StaffThanhToanPhanHoi xacNhanCoc(UUID thanhToanId, String emailStaff);

    /** Thu phần tiền còn lại tại quầy, tự động hoàn thành đơn */
    StaffThanhToanPhanHoi thanhToanConLai(ThanhToanConLaiYeuCau yeuCau, String emailStaff);
}
