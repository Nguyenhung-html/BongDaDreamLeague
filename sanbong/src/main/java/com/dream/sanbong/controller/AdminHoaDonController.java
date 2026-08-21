package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.service.AdminHoaDonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin + Staff] Quản lý dữ liệu hoá đơn
 * Đường dẫn gốc: /api/admin/hoa-don — cho phép cả ADMIN và STAFF đọc dữ liệu
 * (Staff cần xem/in hoá đơn tại quầy, không chỉ Admin mới xem được).
 * Mỗi đơn đặt sân tương ứng một hoá đơn, dựng từ tiền sân + gia hạn + dịch vụ.
 */
@RestController
@RequestMapping("/api/admin/hoa-don")
@CrossOrigin(origins = "*")
public class AdminHoaDonController {

    private final AdminHoaDonService hoaDonService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminHoaDonController(AdminHoaDonService hoaDonService, QuyenTruyCap quyenTruyCap) {
        this.hoaDonService = hoaDonService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /**
     * GET /api/admin/hoa-don?tuNgay=&denNgay=&trangThaiThanhToan=&baoGomDaHuy=&tuKhoa=
     * Danh sách hoá đơn. Mặc định bỏ qua hoá đơn của các đơn đã huỷ.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String trangThaiThanhToan,
            @RequestParam(required = false, defaultValue = "false") boolean baoGomDaHuy,
            @RequestParam(required = false) String tuKhoa,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(request);
            return ResponseEntity.ok(hoaDonService.layDanhSach(
                    tuNgay, denNgay, trangThaiThanhToan, baoGomDaHuy, tuKhoa));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/hoa-don/thong-ke?tuNgay=&denNgay=&baoGomDaHuy=
     * Tổng số hoá đơn, tổng giá trị, đã thu và còn lại.
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<?> layThongKe(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false, defaultValue = "false") boolean baoGomDaHuy,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(request);
            return ResponseEntity.ok(hoaDonService.layThongKe(tuNgay, denNgay, baoGomDaHuy));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/hoa-don/{datSanId}
     * Hoá đơn đầy đủ: thông tin chung + các dòng chi tiết + các lần thanh toán.
     */
    @GetMapping("/{datSanId}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID datSanId, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(request);
            return ResponseEntity.ok(hoaDonService.layChiTiet(datSanId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}