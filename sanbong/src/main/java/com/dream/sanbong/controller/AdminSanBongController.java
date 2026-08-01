package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.CapNhatTrangThaiYeuCau;
import com.dream.sanbong.dto.GiaSanYeuCau;
import com.dream.sanbong.dto.SanBongYeuCau;
import com.dream.sanbong.service.AdminSanBongService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý thông tin sân bóng
 * Đường dẫn gốc: /api/admin/san-bong — toàn bộ yêu cầu quyền ADMIN.
 * Gồm CRUD sân và CRUD bảng giá theo khung giờ của sân đó.
 */
@RestController
@RequestMapping("/api/admin/san-bong")
@CrossOrigin(origins = "*")
public class AdminSanBongController {

    private final AdminSanBongService sanBongService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminSanBongController(AdminSanBongService sanBongService, QuyenTruyCap quyenTruyCap) {
        this.sanBongService = sanBongService;
        this.quyenTruyCap = quyenTruyCap;
    }

    // ══════════════════════════════════════════════════════════════
    // SÂN BÓNG
    // ══════════════════════════════════════════════════════════════

    /**
     * GET /api/admin/san-bong?tuKhoa=&loaiSan=&trangThai=
     * Danh sách toàn bộ sân, kể cả sân đang bảo trì hoặc đã đóng cửa.
     */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(required = false) Integer loaiSan,
            @RequestParam(required = false) String trangThai,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.layDanhSach(tuKhoa, loaiSan, trangThai));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/san-bong/{id} — chi tiết sân kèm bảng giá */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/admin/san-bong — thêm sân mới */
    @PostMapping
    public ResponseEntity<?> them(@RequestBody SanBongYeuCau yeuCau, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.them(yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/san-bong/{id} — sửa thông tin sân */
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhat(@PathVariable UUID id,
                                     @RequestBody SanBongYeuCau yeuCau,
                                     HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.capNhat(id, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/san-bong/{id}/trang-thai — body {"trangThai":"BAO_TRI"} */
    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> doiTrangThai(@PathVariable UUID id,
                                          @RequestBody CapNhatTrangThaiYeuCau yeuCau,
                                          HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.doiTrangThai(id, yeuCau.getTrangThai()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** DELETE /api/admin/san-bong/{id} — xoá sân chưa phát sinh đơn nào */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(Map.of("message", sanBongService.xoa(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // BẢNG GIÁ THEO KHUNG GIỜ
    // ══════════════════════════════════════════════════════════════

    /** GET /api/admin/san-bong/{id}/gia — bảng giá của 1 sân */
    @GetMapping("/{id}/gia")
    public ResponseEntity<?> layBangGia(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.layBangGia(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/admin/san-bong/{id}/gia — thêm 1 khung giờ vào bảng giá */
    @PostMapping("/{id}/gia")
    public ResponseEntity<?> themKhungGio(@PathVariable UUID id,
                                          @RequestBody GiaSanYeuCau yeuCau,
                                          HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.themKhungGio(id, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/san-bong/gia/{giaSanId} — sửa 1 khung giờ */
    @PutMapping("/gia/{giaSanId}")
    public ResponseEntity<?> suaKhungGio(@PathVariable UUID giaSanId,
                                         @RequestBody GiaSanYeuCau yeuCau,
                                         HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(sanBongService.suaKhungGio(giaSanId, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** DELETE /api/admin/san-bong/gia/{giaSanId} — xoá 1 khung giờ */
    @DeleteMapping("/gia/{giaSanId}")
    public ResponseEntity<?> xoaKhungGio(@PathVariable UUID giaSanId, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(Map.of("message", sanBongService.xoaKhungGio(giaSanId)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
