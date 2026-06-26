package com.dream.sanbong.controller;

import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.DatSanPhanHoi;
import com.dream.sanbong.dto.DatSanYeuCau;
import com.dream.sanbong.dto.LichSuDatSanPhanHoi;
import com.dream.sanbong.service.DatSanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dat-san")
@CrossOrigin(origins = "*")
public class DatSanController {

    private final DatSanService datSanService;
    private final JwtUtils jwtUtils;

    public DatSanController(DatSanService datSanService, JwtUtils jwtUtils) {
        this.datSanService = datSanService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /api/dat-san
     * Đặt sân (yêu cầu JWT)
     */
    @PostMapping
    public ResponseEntity<?> datSan(
            @RequestBody DatSanYeuCau yeuCau,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            DatSanPhanHoi ketQua = datSanService.datSan(yeuCau, email);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            // Trả về JSON {"message": "..."} thay vì plain text
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/dat-san/lich-su
     * Lịch sử đặt sân của người dùng hiện tại (yêu cầu JWT)
     */
    @GetMapping("/lich-su")
    public ResponseEntity<?> layLichSu(HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            List<LichSuDatSanPhanHoi> lichSu = datSanService.layLichSu(email);
            return ResponseEntity.ok(lichSu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/dat-san/{id}/huy
     * Huỷ đặt sân (yêu cầu JWT, phải là chủ đặt)
     */
    @PutMapping("/{id}/huy")
    public ResponseEntity<?> huySan(
            @PathVariable UUID id,
            HttpServletRequest request) {
        try {
            String email = layEmailTuToken(request);
            String ketQua = datSanService.huySan(id, email);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── Helper: Trích xuất email từ JWT Bearer Token ──
    private String layEmailTuToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        String token = authHeader.substring(7);
        return jwtUtils.getEmailFromToken(token);
    }
}
