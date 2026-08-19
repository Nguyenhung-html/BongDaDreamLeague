package com.dream.sanbong.controller;

import com.dream.sanbong.dto.CapNhatProfileYeuCau;
import com.dream.sanbong.dto.DoiMatKhauYeuCau;
import com.dream.sanbong.dto.ProfilePhanHoi;
import com.dream.sanbong.service.NguoiDungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminProfileController {

    private final NguoiDungService nguoiDungService;

    /**
     * 1. Lấy thông tin cá nhân của Admin
     * Endpoint: GET /api/admin/profile/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable UUID id) {
        try {
            ProfilePhanHoi profile = nguoiDungService.getProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 2. Chỉnh sửa thông tin cá nhân Admin (Họ tên, SĐT, Email,...)
     * Endpoint: PUT /api/admin/profile/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable UUID id,
            @RequestBody CapNhatProfileYeuCau yeuCau) {
        try {
            ProfilePhanHoi updated = nguoiDungService.updateProfile(id, yeuCau);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 3. Đổi mật khẩu tài khoản Admin
     * Endpoint: PUT /api/admin/profile/{id}/doi-mat-khau
     */
    @PutMapping("/{id}/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(
            @PathVariable UUID id,
            @RequestBody @Valid DoiMatKhauYeuCau yeuCau) {
        try {
            String msg = nguoiDungService.doiMatKhau(id, yeuCau);
            return ResponseEntity.ok(Map.of("message", msg));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}