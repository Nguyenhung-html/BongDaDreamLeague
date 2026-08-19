package com.dream.sanbong.controller;

import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.GiaHanGioPhanHoi;
import com.dream.sanbong.service.GiaHanGioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class GiaHanGioController {

    private final GiaHanGioService giaHanGioService;
    private final JwtUtils jwtUtils;

    public GiaHanGioController(GiaHanGioService giaHanGioService, JwtUtils jwtUtils) {
        this.giaHanGioService = giaHanGioService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /api/dat-san/{datSanId}/gia-han
     * Gia hạn thêm 30 phút THẬT cho 1 đơn đặt sân (Staff làm - cộng tiền + đổi giờ thật)
     */
    @PostMapping("/api/dat-san/{datSanId}/gia-han")
    public ResponseEntity<?> giaHan(@PathVariable UUID datSanId, HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            GiaHanGioPhanHoi ketQua = giaHanGioService.giaHanThemGio(datSanId, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/dat-san/{datSanId}/yeu-cau-gia-han
     * MỚI THÊM: Khách bấm "Yêu cầu gia hạn" - CHỈ gửi thông báo cho Staff,
     * KHÔNG đổi giờ/tiền. Staff xác nhận thật bằng endpoint /gia-han ở trên.
     */
    @PostMapping("/api/dat-san/{datSanId}/yeu-cau-gia-han")
    public ResponseEntity<?> yeuCauGiaHan(@PathVariable UUID datSanId, HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            giaHanGioService.yeuCauGiaHan(datSanId, email);
            return ResponseEntity.ok(Map.of("message",
                    "Đã gửi yêu cầu gia hạn tới nhân viên. Vui lòng chờ nhân viên xác nhận!"));
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