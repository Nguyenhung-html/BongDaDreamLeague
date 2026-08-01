package com.dream.sanbong.controller;

import com.dream.sanbong.config.JwtUtils;
import com.dream.sanbong.dto.SanPhamPhanHoi;
import com.dream.sanbong.dto.SanPhamYeuCau;
import com.dream.sanbong.service.SanPhamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin(origins = "*")
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final JwtUtils jwtUtils;

    public SanPhamController(SanPhamService sanPhamService, JwtUtils jwtUtils) {
        this.sanPhamService = sanPhamService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * GET /api/san-pham
     * Menu cho khách xem khi gọi dịch vụ (chỉ sản phẩm còn bán, yêu cầu JWT)
     */
    @GetMapping
    public ResponseEntity<?> layDanhSachChoKhach(HttpServletRequest request) {
        try {
            layEmailTuToken(request); // chỉ cần đăng nhập, không cần vai trò cụ thể
            List<SanPhamPhanHoi> ketQua = sanPhamService.layDanhSachConBan();
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * GET /api/san-pham/tat-ca
     * Danh sách đầy đủ cho Admin quản lý (kể cả sản phẩm đã ngừng bán)
     */
    @GetMapping("/tat-ca")
    public ResponseEntity<?> layTatCaChoAdmin(HttpServletRequest request) {
        try {
            xacThucQuyenAdmin(request);
            return ResponseEntity.ok(sanPhamService.layTatCa());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/san-pham
     * Admin thêm sản phẩm mới
     */
    @PostMapping
    public ResponseEntity<?> them(@RequestBody SanPhamYeuCau yeuCau, HttpServletRequest request) {
        try {
            xacThucQuyenAdmin(request);
            return ResponseEntity.ok(sanPhamService.them(yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * PUT /api/san-pham/{id}
     * Admin sửa sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> sua(@PathVariable UUID id, @RequestBody SanPhamYeuCau yeuCau,
                                  HttpServletRequest request) {
        try {
            xacThucQuyenAdmin(request);
            return ResponseEntity.ok(sanPhamService.sua(id, yeuCau));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * DELETE /api/san-pham/{id}
     * Admin xoá sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoa(@PathVariable UUID id, HttpServletRequest request) {
        try {
            xacThucQuyenAdmin(request);
            sanPhamService.xoa(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá sản phẩm thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── Helper: lấy email từ JWT (đồng bộ cách làm với DatSanController) ──
    private String layEmailTuToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        return jwtUtils.getEmailFromToken(authHeader.substring(7));
    }

    // ── Helper: bắt buộc phải là ADMIN mới được thao tác ──
    private void xacThucQuyenAdmin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        String token = authHeader.substring(7);
        String vaiTro = jwtUtils.getVaiTroFromToken(token);
        if (!"ADMIN".equals(vaiTro)) {
            throw new RuntimeException("Lỗi: Bạn không có quyền Admin để thực hiện thao tác này!");
        }
    }
}