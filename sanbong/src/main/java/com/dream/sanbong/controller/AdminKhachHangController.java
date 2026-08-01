package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.CapNhatKhachHangYeuCau;
import com.dream.sanbong.service.AdminKhachHangService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý thông tin khách hàng
 * Đường dẫn gốc: /api/admin/khach-hang — toàn bộ yêu cầu quyền ADMIN.
 */
@RestController
@RequestMapping("/api/admin/khach-hang")
@CrossOrigin(origins = "*")
public class AdminKhachHangController {

    private final AdminKhachHangService khachHangService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminKhachHangController(AdminKhachHangService khachHangService, QuyenTruyCap quyenTruyCap) {
        this.khachHangService = khachHangService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /**
     * GET /api/admin/khach-hang?tuKhoa=&trangThai=
     * Danh sách khách hàng, lọc theo từ khoá (tên/email/SĐT) và trạng thái tài khoản.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) String trangThai,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(khachHangService.layDanhSach(tuKhoa, trangThai));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/khach-hang/{id} — chi tiết 1 khách hàng */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(khachHangService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/khach-hang/{id} — sửa thông tin khách hàng */
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhat(@PathVariable UUID id,
                                     @RequestBody CapNhatKhachHangYeuCau yeuCau,
                                     HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(khachHangService.capNhat(id, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/khach-hang/{id}/khoa — khoá tài khoản */
    @PutMapping("/{id}/khoa")
    public ResponseEntity<?> khoaTaiKhoan(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(khachHangService.doiTrangThai(id, false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/khach-hang/{id}/mo-khoa — mở lại tài khoản */
    @PutMapping("/{id}/mo-khoa")
    public ResponseEntity<?> moKhoaTaiKhoan(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(khachHangService.doiTrangThai(id, true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
