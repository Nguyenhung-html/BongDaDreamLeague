package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.CapNhatTrangThaiYeuCau;
import com.dream.sanbong.service.AdminDatSanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý đơn đặt sân hệ thống
 * Đường dẫn gốc: /api/admin/dat-san — toàn bộ yêu cầu quyền ADMIN.
 */
@RestController
@RequestMapping("/api/admin/dat-san")
@CrossOrigin(origins = "*")
public class AdminDatSanController {

    private final AdminDatSanService adminDatSanService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminDatSanController(AdminDatSanService adminDatSanService, QuyenTruyCap quyenTruyCap) {
        this.adminDatSanService = adminDatSanService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /**
     * GET /api/admin/dat-san?trangThai=&tuNgay=&denNgay=&sanBongId=&tuKhoa=
     * Danh sách toàn bộ đơn đặt sân với bộ lọc.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) UUID sanBongId,
            @RequestParam(required = false) String tuKhoa,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(
                    adminDatSanService.layDanhSach(trangThai, tuNgay, denNgay, sanBongId, tuKhoa));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/admin/dat-san/thong-ke?tuNgay=&denNgay=
     * Số liệu tổng hợp theo trạng thái đơn.
     */
    @GetMapping("/thong-ke")
    public ResponseEntity<?> layThongKe(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(adminDatSanService.layThongKe(tuNgay, denNgay));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/dat-san/{id} — chi tiết đơn kèm dịch vụ, gia hạn, thanh toán */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(adminDatSanService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/dat-san/{id}/xac-nhan — xác nhận đơn, chuyển sang Đã cọc */
    @PutMapping("/{id}/xac-nhan")
    public ResponseEntity<?> xacNhan(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String email = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(Map.of("message", adminDatSanService.xacNhan(id, email)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/dat-san/{id}/huy — huỷ đơn kèm lý do */
    @PutMapping("/{id}/huy")
    public ResponseEntity<?> huy(@PathVariable UUID id,
                                 @RequestBody(required = false) CapNhatTrangThaiYeuCau body,
                                 HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String email = quyenTruyCap.layEmail(request);
            String lyDo = (body != null) ? body.getLyDo() : null;
            return ResponseEntity.ok(Map.of("message", adminDatSanService.huy(id, lyDo, email)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/dat-san/{id}/hoan-thanh — đánh dấu đơn đã hoàn thành */
    @PutMapping("/{id}/hoan-thanh")
    public ResponseEntity<?> hoanThanh(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String email = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(Map.of("message", adminDatSanService.hoanThanh(id, email)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
