package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.service.AdminThanhToanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý giao dịch thanh toán
 * Đường dẫn gốc: /api/admin/thanh-toan — toàn bộ yêu cầu quyền ADMIN.
 */
@RestController
@RequestMapping("/api/admin/thanh-toan")
@CrossOrigin(origins = "*")
public class AdminThanhToanController {

    private final AdminThanhToanService thanhToanService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminThanhToanController(AdminThanhToanService thanhToanService, QuyenTruyCap quyenTruyCap) {
        this.thanhToanService = thanhToanService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /**
     * GET /api/admin/thanh-toan?tuNgay=&denNgay=&trangThai=&loai=&phuongThuc=&tuKhoa=
     * Danh sách giao dịch toàn hệ thống, kể cả giao dịch thất bại / đã hoàn tiền.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) String phuongThuc,
            @RequestParam(required = false) String tuKhoa,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.layDanhSach(
                    tuNgay, denNgay, trangThai, loai, phuongThuc, tuKhoa));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/thanh-toan/tong-hop?tuNgay=&denNgay=
     * Số liệu đối soát: số giao dịch và tổng tiền theo trạng thái / phương thức.
     */
    @GetMapping("/tong-hop")
    public ResponseEntity<?> layTongHop(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.layTongHop(tuNgay, denNgay));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/thanh-toan/theo-don/{datSanId} — tất cả giao dịch của 1 đơn */
    @GetMapping("/theo-don/{datSanId}")
    public ResponseEntity<?> layTheoDon(@PathVariable UUID datSanId, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.layTheoDon(datSanId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/thanh-toan/{id} — chi tiết 1 giao dịch */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/thanh-toan/{id}/xac-nhan — xác nhận đã thu được tiền */
    @PutMapping("/{id}/xac-nhan")
    public ResponseEntity<?> xacNhan(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.xacNhan(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/thanh-toan/{id}/hoan-tien — hoàn tiền giao dịch đã thu */
    @PutMapping("/{id}/hoan-tien")
    public ResponseEntity<?> hoanTien(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thanhToanService.hoanTien(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
