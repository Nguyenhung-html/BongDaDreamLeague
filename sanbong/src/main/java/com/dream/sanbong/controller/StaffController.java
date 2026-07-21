package com.dream.sanbong.controller;

import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.*;
import com.dream.sanbong.service.DatSanService;
import com.dream.sanbong.service.StaffSanBongService;
import com.dream.sanbong.service.StaffThanhToanService;
import com.dream.sanbong.service.ThongKeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Staff Controller — toàn bộ API dành cho nhân viên (STAFF / ADMIN)
 * Đường dẫn gốc: /api/staff
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    private final DatSanService datSanService;
    private final StaffSanBongService staffSanBongService;
    private final StaffThanhToanService staffThanhToanService;
    private final ThongKeService thongKeService;
    private final JwtUtils jwtUtils;

    public StaffController(DatSanService datSanService,
                           StaffSanBongService staffSanBongService,
                           StaffThanhToanService staffThanhToanService,
                           ThongKeService thongKeService,
                           JwtUtils jwtUtils) {
        this.datSanService = datSanService;
        this.staffSanBongService = staffSanBongService;
        this.staffThanhToanService = staffThanhToanService;
        this.thongKeService = thongKeService;
        this.jwtUtils = jwtUtils;
    }

    // ══════════════════════════════════════════════════════════════
    // QUẢN LÝ ĐẶT SÂN
    // ══════════════════════════════════════════════════════════════

    /**
     * GET /api/staff/dat-san?trangThai=&ngay=
     * Xem danh sách tất cả đơn đặt sân (có filter tuỳ chọn)
     */
    @GetMapping("/dat-san")
    public ResponseEntity<?> layDanhSachDatSan(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String ngay,
            HttpServletRequest request) {
        try {
            kiemTraQuyenStaff(request);
            List<StaffDatSanPhanHoi> danhSach = datSanService.layTatCaDon(trangThai, ngay);
            return ResponseEntity.ok(danhSach);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/staff/dat-san
     * Đặt sân walk-in tại quầy (dùng tài khoản Staff, lưu thông tin khách vãng lai)
     */
    @PostMapping("/dat-san")
    public ResponseEntity<?> datSanWalkIn(
            @RequestBody DatSanYeuCau yeuCau,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            DatSanPhanHoi ketQua = datSanService.datSan(yeuCau, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/dat-san/{id}/xac-nhan
     * Xác nhận đơn đặt sân → CHO_XAC_NHAN thành DA_COC
     */
    @PutMapping("/dat-san/{id}/xac-nhan")
    public ResponseEntity<?> xacNhanDon(
            @PathVariable UUID id,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            String ketQua = datSanService.xacNhanDon(id, email);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/dat-san/{id}/huy
     * Staff huỷ đơn bất kỳ (quyền cao hơn user)
     */
    @PutMapping("/dat-san/{id}/huy")
    public ResponseEntity<?> staffHuyDon(
            @PathVariable UUID id,
            @RequestBody(required = false) CapNhatTrangThaiYeuCau body,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            String lyDo = (body != null) ? body.getLyDo() : null;
            String ketQua = datSanService.staffHuyDon(id, lyDo, email);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/dat-san/{id}/hoan-thanh
     * Đánh dấu đơn đã hoàn thành
     */
    @PutMapping("/dat-san/{id}/hoan-thanh")
    public ResponseEntity<?> hoanThanhDon(
            @PathVariable UUID id,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            String ketQua = datSanService.hoanThanhDon(id, email);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // QUẢN LÝ LỊCH SÂN
    // ══════════════════════════════════════════════════════════════

    /**
     * GET /api/staff/lich-san?ngay=YYYY-MM-DD
     * Lấy lịch sân theo ngày — trả về calendar view
     */
    @GetMapping("/lich-san")
    public ResponseEntity<?> layLichSan(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            HttpServletRequest request) {
        try {
            kiemTraQuyenStaff(request);
            if (ngay == null) ngay = LocalDate.now();
            List<LichSanPhanHoi> lichSan = staffSanBongService.layLichSanTheoNgay(ngay);
            return ResponseEntity.ok(lichSan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/san-bong/{id}/trang-thai
     * Khóa/mở sân: body { "trangThai": "BAO_TRI", "lyDo": "..." }
     */
    @PutMapping("/san-bong/{id}/trang-thai")
    public ResponseEntity<?> capNhatTrangThaiSan(
            @PathVariable UUID id,
            @RequestBody CapNhatTrangThaiYeuCau body,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            String ketQua = staffSanBongService.capNhatTrangThaiSan(id, body.getTrangThai(), body.getLyDo(), email);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // QUẢN LÝ THANH TOÁN
    // ══════════════════════════════════════════════════════════════

    /**
     * GET /api/staff/thanh-toan?tuNgay=&denNgay=
     * Danh sách tất cả giao dịch thanh toán
     */
    @GetMapping("/thanh-toan")
    public ResponseEntity<?> layDanhSachThanhToan(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            HttpServletRequest request) {
        try {
            kiemTraQuyenStaff(request);
            List<StaffThanhToanPhanHoi> danhSach = staffThanhToanService.layDanhSachThanhToan(tuNgay, denNgay);
            return ResponseEntity.ok(danhSach);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/thanh-toan/{id}/xac-nhan-coc
     * Xác nhận đã nhận tiền cọc thành công
     */
    @PutMapping("/thanh-toan/{id}/xac-nhan-coc")
    public ResponseEntity<?> xacNhanCoc(
            @PathVariable UUID id,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            StaffThanhToanPhanHoi ketQua = staffThanhToanService.xacNhanCoc(id, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/staff/thanh-toan/thanh-toan-cuoi
     * Thu phần tiền còn lại tại quầy
     */
    @PostMapping("/thanh-toan/thanh-toan-cuoi")
    public ResponseEntity<?> thanhToanConLai(
            @RequestBody ThanhToanConLaiYeuCau yeuCau,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            kiemTraQuyenStaff(request);
            StaffThanhToanPhanHoi ketQua = staffThanhToanService.thanhToanConLai(yeuCau, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // THỐNG KÊ
    // ══════════════════════════════════════════════════════════════

    /**
     * GET /api/staff/thong-ke/dashboard?nam=2026
     * Data tổng quan cho Dashboard: cards + 2 line charts
     */
    @GetMapping("/thong-ke/dashboard")
    public ResponseEntity<?> layDashboard(
            @RequestParam(required = false) Integer nam,
            HttpServletRequest request) {
        try {
            kiemTraQuyenStaff(request);
            if (nam == null) nam = java.time.LocalDate.now().getYear();
            DashboardTongQuanDTO data = thongKeService.layTongQuanDashboard(nam);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/staff/thong-ke/doanh-thu?nam=2026&thang=7
     * Data chi tiết doanh thu: cards + line chart ngày + bảng sân
     */
    @GetMapping("/thong-ke/doanh-thu")
    public ResponseEntity<?> layDoanhThuChiTiet(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) Integer thang,
            HttpServletRequest request) {
        try {
            kiemTraQuyenStaff(request);
            if (nam == null) nam = java.time.LocalDate.now().getYear();
            DoanhThuChiTietDTO data = thongKeService.layDoanhThuChiTiet(nam, thang);
            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════

    private String layEmailTuToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        return jwtUtils.getEmailFromToken(authHeader.substring(7));
    }

    private void kiemTraQuyenStaff(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không có quyền truy cập!");
        }
        String vaiTro = jwtUtils.getVaiTroFromToken(authHeader.substring(7));
        if (!"STAFF".equals(vaiTro) && !"ADMIN".equals(vaiTro)) {
            throw new RuntimeException("Lỗi: Bạn không có quyền Staff để thực hiện thao tác này!");
        }
    }
}
