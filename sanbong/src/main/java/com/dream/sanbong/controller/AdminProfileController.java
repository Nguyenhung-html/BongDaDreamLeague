package com.dream.sanbong.controller;

import com.dream.sanbong.dto.CapNhatProfileYeuCau;
import com.dream.sanbong.dto.ProfilePhanHoi;
import com.dream.sanbong.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ProfilePhanHoi> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(nguoiDungService.getProfile(id));
    }

    /**
     * 2. Chỉnh sửa thông tin cá nhân Admin (Họ tên, SĐT, Email,...)
     * Endpoint: PUT /api/admin/profile/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfilePhanHoi> updateProfile(
            @PathVariable UUID id,
            @RequestBody CapNhatProfileYeuCau yeuCau) {
        return ResponseEntity.ok(nguoiDungService.updateProfile(id, yeuCau));
    }
}