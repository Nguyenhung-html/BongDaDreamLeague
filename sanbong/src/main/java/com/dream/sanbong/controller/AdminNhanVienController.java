package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.CapNhatNhanVienYeuCau;
import com.dream.sanbong.dto.DatLaiMatKhauYeuCau;
import com.dream.sanbong.dto.TaoNhanVienYeuCau;
import com.dream.sanbong.service.AdminNhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý tài khoản nhân viên
 * Đường dẫn gốc: /api/admin/nhan-vien — toàn bộ yêu cầu quyền ADMIN.
 */
@RestController
@RequestMapping("/api/admin/nhan-vien")
@CrossOrigin(origins = "*")
public class AdminNhanVienController {

    private final AdminNhanVienService nhanVienService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminNhanVienController(AdminNhanVienService nhanVienService, QuyenTruyCap quyenTruyCap) {
        this.nhanVienService = nhanVienService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /**
     * GET /api/admin/nhan-vien?tuKhoa=&vaiTro=
     * Danh sách nhân viên (STAFF + ADMIN), lọc theo từ khoá và vai trò.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) String vaiTro,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(nhanVienService.layDanhSach(tuKhoa, vaiTro));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/nhan-vien/{id} — chi tiết 1 nhân viên */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(nhanVienService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/admin/nhan-vien — tạo tài khoản nhân viên mới */
    @PostMapping
    public ResponseEntity<?> them(@RequestBody TaoNhanVienYeuCau yeuCau, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(nhanVienService.them(yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/nhan-vien/{id} — sửa thông tin nhân viên */
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhat(@PathVariable UUID id,
                                     @RequestBody CapNhatNhanVienYeuCau yeuCau,
                                     HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String emailAdmin = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(nhanVienService.capNhat(id, yeuCau, emailAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/nhan-vien/{id}/khoa — khoá tài khoản nhân viên */
    @PutMapping("/{id}/khoa")
    public ResponseEntity<?> khoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String emailAdmin = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(nhanVienService.doiTrangThai(id, false, emailAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/nhan-vien/{id}/mo-khoa — mở lại tài khoản nhân viên */
    @PutMapping("/{id}/mo-khoa")
    public ResponseEntity<?> moKhoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String emailAdmin = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(nhanVienService.doiTrangThai(id, true, emailAdmin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/nhan-vien/{id}/mat-khau — đặt lại mật khẩu */
    @PutMapping("/{id}/mat-khau")
    public ResponseEntity<?> datLaiMatKhau(@PathVariable UUID id,
                                           @RequestBody DatLaiMatKhauYeuCau yeuCau,
                                           HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String ketQua = nhanVienService.datLaiMatKhau(id, yeuCau.getMatKhauMoi());
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** DELETE /api/admin/nhan-vien/{id} — xoá tài khoản nhân viên */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            String emailAdmin = quyenTruyCap.layEmail(request);
            return ResponseEntity.ok(Map.of("message", nhanVienService.xoa(id, emailAdmin)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
