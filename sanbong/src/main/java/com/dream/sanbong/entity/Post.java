package com.dream.sanbong.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Khớp với BIGINT IDENTITY(1,1)

    @Column(name = "author_id", nullable = false)
    private UUID authorId; // Khớp với UNIQUEIDENTIFIER

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String summary;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(nullable = false, length = 20)
    private String status; // 'draft', 'published', 'archived'

    @Column(name = "views_count", nullable = false)
    @Builder.Default
    private Integer viewsCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}