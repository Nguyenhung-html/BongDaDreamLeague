package com.dream.sanbong.controller;

import com.dream.sanbong.config.QuyenTruyCap;
import com.dream.sanbong.dto.PostRequest;
import com.dream.sanbong.dto.PostResponse;
import com.dream.sanbong.entity.Post;
import com.dream.sanbong.repository.PostRepository;
import com.dream.sanbong.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PostController {

    private final PostService postService;
    private final QuyenTruyCap quyenTruyCap;
    private final PostRepository postRepository;

    // 1. API TRANG CHỦ: Chỉ lấy bài viết 'published'
    @PreAuthorize("permitAll()")
    @GetMapping("/published")
    public ResponseEntity<?> getPublishedPosts() {
        List<Post> posts = postRepository.findByStatusOrderByCreatedAtDesc("published");
        return ResponseEntity.ok(posts);
    }

    // 2. API QUẢN LÝ: Lấy tất cả hoặc lọc theo ?status=published/draft/archived
    // (Đã gộp layDanhSachBaiViet và getAllPosts làm một để hết lỗi trùng URL)
    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(required = false) String status) {
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(postRepository.findByStatusOrderByCreatedAtDesc(status));
        }
        return ResponseEntity.ok(postService.layDanhSachBaiViet());
    }

    // 3. Lấy chi tiết bài viết
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> layChiTietBaiViet(@PathVariable Long id) {
        return ResponseEntity.ok(postService.layChiTietBaiViet(id));
    }

    // 4. Tạo bài viết
    @PostMapping
    public ResponseEntity<?> taoBaiViet(@Valid @RequestBody PostRequest request, HttpServletRequest httpRequest) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(httpRequest);
            String userEmail = quyenTruyCap.layEmail(httpRequest);
            postService.taoBaiViet(userEmail, request);
            return ResponseEntity.ok("Tạo bài viết thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 5. Cập nhật bài viết
    @PutMapping("/{id}")
    public ResponseEntity<?> capNhatBaiViet(
            @PathVariable Long id, 
            @Valid @RequestBody PostRequest request,
            HttpServletRequest httpRequest) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(httpRequest);
            postService.capNhatBaiViet(id, request);
            return ResponseEntity.ok("Cập nhật bài viết thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 6. Xóa bài viết
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoaBaiViet(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            quyenTruyCap.batBuocStaffHoacAdmin(httpRequest);
            postService.xoaBaiViet(id);
            return ResponseEntity.ok("Xóa bài viết thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // 7. API Tăng lượt xem (Public)
    @PreAuthorize("permitAll()")
    @GetMapping("/{id}/view")
    public ResponseEntity<?> incrementPostView(@PathVariable Long id) {
        int updatedRows = postRepository.incrementViewsCount(id);
        if (updatedRows == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body("Tăng lượt xem thành công");
    }
}