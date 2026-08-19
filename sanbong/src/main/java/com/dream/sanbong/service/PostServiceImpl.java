package com.dream.sanbong.service;

import com.dream.sanbong.dto.PostRequest;
import com.dream.sanbong.dto.PostResponse;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.Post;
import com.dream.sanbong.repository.NguoiDungRepository; // Đảm bảo import đúng Repository NguoiDung của bạn
import com.dream.sanbong.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final NguoiDungRepository nguoiDungRepository; // Tiêm Repository để tìm người dùng thực tế

    @Override
    public List<PostResponse> layDanhSachBaiViet() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse layChiTietBaiViet(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        post.setViewsCount((post.getViewsCount() == null ? 0 : post.getViewsCount()) + 1);
        postRepository.save(post);

        return mapToResponse(post);
    }

    @Override
    @Transactional
    public void taoBaiViet(String userEmail, PostRequest request) {
        // 1. Tìm người dùng thực tế trong DB theo Email lấy từ JWT Token
        NguoiDung user = nguoiDungRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + userEmail));

        // 2. Tự động sinh slug nếu người dùng không nhập
        String slug = (request.getSlug() != null && !request.getSlug().isBlank())
                ? request.getSlug()
                : generateSlug(request.getTitle());

        if (postRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        // 3. Build đối tượng Post với authorId lấy từ người dùng hợp lệ
        Post post = Post.builder()
                .authorId(user.getId()) // Gán đúng ID của tài khoản đang thao tác trong dbo.USERS
                .title(request.getTitle())
                .slug(slug)
                .summary(request.getSummary())
                .content(request.getContent())
                .thumbnailUrl(request.getThumbnailUrl())
                .status(request.getStatus() != null ? request.getStatus() : "draft")
                .viewsCount(0)
                .build();

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void capNhatBaiViet(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết để cập nhật!"));

        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setThumbnailUrl(request.getThumbnailUrl());

        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void xoaBaiViet(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Bài viết không tồn tại!");
        }
        postRepository.deleteById(id);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .thumbnailUrl(post.getThumbnailUrl())
                .status(post.getStatus())
                .viewsCount(post.getViewsCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[áàảãạâấầẩẫậăắằẳẵặ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[iíìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}