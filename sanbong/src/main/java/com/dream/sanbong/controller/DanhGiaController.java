package com.dream.sanbong.controller;

import com.dream.sanbong.dto.DanhGiaRequest;
import com.dream.sanbong.dto.DanhGiaResponse;
import com.dream.sanbong.service.DanhGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/danh-gia")
@RequiredArgsConstructor
@CrossOrigin("*") // Thêm CrossOrigin để tránh lỗi chặn CORS từ Frontend Vue
public class DanhGiaController {

    private final DanhGiaService danhGiaService;

    // 1. API lấy danh sách bình luận công khai của 1 sân bóng (Người dùng thường xem)
    @GetMapping("/san/{sanBongId}")
    public ResponseEntity<List<DanhGiaResponse>> layDanhGiaCuaSan(@PathVariable UUID sanBongId) {
        return ResponseEntity.ok(danhGiaService.layDanhGiaCuaSan(sanBongId));
    }

    // 🌟 BỔ SUNG: 2. API dành riêng cho ADMIN lấy TOÀN BỘ đánh giá của 1 sân (Cả ẩn và hiện)
    // Đường dẫn này khớp chính xác với lệnh gọi từ file Vue của bạn
    @GetMapping("/admin/san/{sanBongId}")
    public ResponseEntity<List<DanhGiaResponse>> adminLayDanhGiaCuaSan(@PathVariable UUID sanBongId) {
        // Hãy đảm bảo bạn đã tạo hàm 'adminLayDanhGiaCuaSan' này trong DanhGiaService
        return ResponseEntity.ok(danhGiaService.adminLayDanhGiaCuaSan(sanBongId));
    }

    // 3. API gửi bình luận lên hệ thống
    @PostMapping("/gui")
    public ResponseEntity<String> guiDanhGia(@Valid @RequestBody DanhGiaRequest request, Authentication authentication) {
        try {
            String userEmail = authentication.getName(); 
            danhGiaService.guiDanhGiaTheoEmail(userEmail, request);
            return ResponseEntity.ok("Gửi đánh giá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. API dành cho ADMIN ẩn/hiện bình luận
    @PutMapping("/admin/an/{danhGiaId}")
    public ResponseEntity<String> adminAnBinhLuan(@PathVariable UUID danhGiaId) {
        danhGiaService.adminAnBinhLuan(danhGiaId);
        return ResponseEntity.ok("Đổi trạng thái ẩn/hiện bình luận thành công.");
    }

    // 🌟 BỔ SUNG: 5. API dành cho ADMIN xóa vĩnh viễn đánh giá
    // Đường dẫn khớp với hàm deleteReview trên Vue: DELETE /api/danh-gia/admin/xoa/{id}
    @DeleteMapping("/admin/xoa/{danhGiaId}")
    public ResponseEntity<String> adminXoaBinhLuan(@PathVariable UUID danhGiaId) {
        // Hãy đảm bảo bạn đã tạo hàm này trong DanhGiaService để thực hiện delete khỏi DB
        danhGiaService.adminXoaBinhLuan(danhGiaId);
        return ResponseEntity.ok("Đã xóa vĩnh viễn đánh giá thành công.");
    }
}