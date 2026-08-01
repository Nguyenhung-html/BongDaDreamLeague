package com.dream.sanbong.controller;

import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.GioHangPhanHoi;
import com.dream.sanbong.dto.ThemDichVuYeuCau;
import com.dream.sanbong.service.DichVuDatSanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class DichVuDatSanController {

    private final DichVuDatSanService dichVuService;
    private final JwtUtils jwtUtils;

    public DichVuDatSanController(DichVuDatSanService dichVuService, JwtUtils jwtUtils) {
        this.dichVuService = dichVuService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /api/dat-san/{datSanId}/dich-vu
     * Gọi thêm 1 sản phẩm vào giỏ hàng dịch vụ của đơn đặt sân
     */
    @PostMapping("/api/dat-san/{datSanId}/dich-vu")
    public ResponseEntity<?> themDichVu(@PathVariable UUID datSanId,
                                         @RequestBody ThemDichVuYeuCau yeuCau,
                                         HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            GioHangPhanHoi ketQua = dichVuService.themVaoGioHang(datSanId, email, yeuCau);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/dat-san/{datSanId}/dich-vu
     * Xem giỏ hàng dịch vụ hiện tại + tổng tiền cần thanh toán tại sân
     */
    @GetMapping("/api/dat-san/{datSanId}/dich-vu")
    public ResponseEntity<?> layGioHang(@PathVariable UUID datSanId, HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            GioHangPhanHoi ketQua = dichVuService.layGioHang(datSanId, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/dich-vu/{id}
     * Xoá 1 món khỏi giỏ hàng dịch vụ
     */
    @DeleteMapping("/api/dich-vu/{id}")
    public ResponseEntity<?> xoaDichVu(@PathVariable UUID id, HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            GioHangPhanHoi ketQua = dichVuService.xoaKhoiGioHang(id, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private String layEmailTuToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        return jwtUtils.getEmailFromToken(authHeader.substring(7));
    }
}