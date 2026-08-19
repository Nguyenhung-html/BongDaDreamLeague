package com.dream.sanbong.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String slug;

    private String summary;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private String thumbnailUrl;

    private String status; // 'draft', 'published', 'archived'
}