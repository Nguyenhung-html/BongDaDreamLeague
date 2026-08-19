package com.dream.sanbong.controller;

import com.dream.sanbong.dto.SanBongPhanHoi;
import com.dream.sanbong.service.SanBongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/san-bong")
@CrossOrigin(origins = "*")
public class SanBongController {

    private final SanBongService sanBongService;

    public SanBongController(SanBongService sanBongService) {
        this.sanBongService = sanBongService;
    }

    /**
     * GET /api/san-bong
     * Lấy danh sách tất cả sân đang hoạt động
     */
    @GetMapping
    public ResponseEntity<List<SanBongPhanHoi>> layTatCaSan() {
        return ResponseEntity.ok(sanBongService.layTatCaSan());
    }

    /**
     * GET /api/san-bong/{id}
     * Lấy chi tiết sân + danh sách khung giờ / giá
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> layChiTietSan(@PathVariable UUID id) {
        try {
            SanBongPhanHoi phanHoi = sanBongService.layChiTietSan(id);
            return ResponseEntity.ok(phanHoi);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
