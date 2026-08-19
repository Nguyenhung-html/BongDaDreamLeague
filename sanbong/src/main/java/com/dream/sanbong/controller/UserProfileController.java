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
@RequestMapping("/api/user/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final NguoiDungService nguoiDungService;

    /**
     * 1. Lấy thông tin cá nhân khách hàng
     * Endpoint: GET /api/user/profile/{id}
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
     * 2. Chỉnh sửa thông tin cá nhân khách hàng (Họ tên, SĐT, Email, Địa chỉ)
     * Endpoint: PUT /api/user/profile/{id}
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
     * 3. Đổi mật khẩu tài khoản
     * Endpoint: PUT /api/user/profile/{id}/doi-mat-khau
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