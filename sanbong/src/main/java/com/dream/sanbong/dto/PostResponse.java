package com.dream.sanbong.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private UUID authorId; // <-- Đảm bảo khai báo đúng tên authorId kiểu UUID ở đây
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private String status;
    private Integer viewsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}