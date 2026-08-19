package com.dream.sanbong.controller;

import com.dream.sanbong.dto.DanhGiaHeThongRequest;
import com.dream.sanbong.dto.DanhGiaHeThongResponse;
import com.dream.sanbong.dto.PhanHoiRequest;
import com.dream.sanbong.entity.DanhGiaHeThong;
import com.dream.sanbong.service.DanhGiaHeThongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/danh-gia-he-thong")
@CrossOrigin(origins = "*")
public class DanhGiaHeThongController {

    @Autowired
    private DanhGiaHeThongService danhGiaHTService;

    // --- PUBLIC ENDPOINTS (Khách hàng) ---
    @GetMapping
    public ResponseEntity<List<DanhGiaHeThongResponse>> getDanhGiaList() {
        return ResponseEntity.ok(danhGiaHTService.getAllDanhGiaHienThi());
    }

    @PostMapping
    public ResponseEntity<?> createDanhGia(
            @RequestBody DanhGiaHeThongRequest request, 
            Authentication authentication) {
        
        // 1. Kiểm tra xem người dùng đã đăng nhập chưa (Token có hợp lệ không)
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Vui lòng đăng nhập để gửi đánh giá.");
        }

        // 2. Validate nội dung bình luận
        if (request.getQuote() == null || request.getQuote().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nội dung đánh giá không được để trống");
        }

        // 3. Lấy email/username của tài khoản từ Authentication Object
        String userEmail = authentication.getName();

        // 4. Gọi Service tạo đánh giá (Truyền thêm userEmail vào Service)
        return ResponseEntity.ok(danhGiaHTService.createDanhGia(request, userEmail));
    }

    // --- STAFF & ADMIN ENDPOINTS ---
    
    // Lấy tất cả đánh giá dành cho trang quản lý Admin/Staff
    @GetMapping("/quan-ly")
    public ResponseEntity<List<DanhGiaHeThongResponse>> getAllDanhGiaChoQuanLy() {
        return ResponseEntity.ok(danhGiaHTService.getAllDanhGiaChoQuanLy());
    }

    // [Staff & Admin] Gửi nội dung phản hồi đánh giá
    @PostMapping("/{id}/phan-hoi")
    public ResponseEntity<?> phanHoiDanhGia(@PathVariable UUID id, @RequestBody PhanHoiRequest request) {
        if (request.getNoiDungPhanHoi() == null || request.getNoiDungPhanHoi().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nội dung phản hồi không được để trống");
        }
        return ResponseEntity.ok(danhGiaHTService.phanHoiDanhGia(id, request));
    }

    // --- ADMIN ONLY ENDPOINTS ---

    // [Admin] Ẩn/Hiện đánh giá
    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> thayDoiTrangThaiHienThi(@PathVariable UUID id, @RequestParam boolean hienThi) {
        return ResponseEntity.ok(danhGiaHTService.thayDoiTrangThaiHienThi(id, hienThi));
    }

    // [Admin] Xóa đánh giá
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoaDanhGia(@PathVariable UUID id) {
        danhGiaHTService.xoaDanhGia(id);
        return ResponseEntity.ok("Đã xóa đánh giá thành công");
    }
}