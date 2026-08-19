package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.CapNhatThongBaoQuanTriYeuCau;
import com.dream.sanbong.dto.TaoThongBaoQuanTriYeuCau;
import com.dream.sanbong.entity.DoiTuongNhan;
import com.dream.sanbong.service.AdminThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * [API] [Admin] Quản lý thông báo quảng bá / thông tin chung
 * Đường dẫn gốc: /api/admin/thong-bao — toàn bộ yêu cầu quyền ADMIN.
 *
 * LƯU Ý: khác với /api/thong-bao (ThongBaoController) vốn là API phía người
 * dùng xem thông báo hệ thống tự sinh RIÊNG của họ (thanh toán, gia hạn...).
 * API này dùng để Admin CHỦ ĐỘNG tạo thông báo theo nhóm đối tượng
 * (Tất cả / Người dùng / Nhân viên), có ngày bắt đầu - kết thúc và ẩn/hiện.
 */
@RestController
@RequestMapping("/api/admin/thong-bao")
@CrossOrigin(origins = "*")
public class AdminThongBaoController {

    private final AdminThongBaoService thongBaoService;
    private final QuyenTruyCap quyenTruyCap;

    public AdminThongBaoController(AdminThongBaoService thongBaoService, QuyenTruyCap quyenTruyCap) {
        this.thongBaoService = thongBaoService;
        this.quyenTruyCap = quyenTruyCap;
    }

    /** GET /api/admin/thong-bao?doiTuongNhan=STAFF|USER|TAT_CA — bỏ trống lấy tất cả */
    @GetMapping
    public ResponseEntity<?> layDanhSach(
            @RequestParam(required = false) DoiTuongNhan doiTuongNhan,
            HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.layDanhSach(doiTuongNhan));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** GET /api/admin/thong-bao/{id} — chi tiết 1 thông báo */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTiet(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.layChiTiet(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** POST /api/admin/thong-bao — tạo thông báo mới */
    @PostMapping
    public ResponseEntity<?> them(@RequestBody TaoThongBaoQuanTriYeuCau yeuCau, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.them(yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/thong-bao/{id} — sửa thông báo */
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhat(@PathVariable UUID id,
                                     @RequestBody CapNhatThongBaoQuanTriYeuCau yeuCau,
                                     HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.capNhat(id, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** DELETE /api/admin/thong-bao/{id} — xoá thông báo */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            thongBaoService.xoa(id);
            return ResponseEntity.ok(Map.of("message", "Xoá thông báo thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/thong-bao/{id}/hien — bật hiển thị */
    @PutMapping("/{id}/hien")
    public ResponseEntity<?> hien(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.doiTrangThaiHienThi(id, true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** PUT /api/admin/thong-bao/{id}/an — ẩn thông báo */
    @PutMapping("/{id}/an")
    public ResponseEntity<?> an(@PathVariable UUID id, HttpServletRequest request) {
        try {
            quyenTruyCap.batBuocAdmin(request);
            return ResponseEntity.ok(thongBaoService.doiTrangThaiHienThi(id, false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}