package com.dream.sanbong.repository;

import com.dream.sanbong.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByStatusOrderByCreatedAtDesc(String status);

    // BỔ SUNG HÀM NÀY ĐỂ TĂNG LƯỢT XEM TRỰC TIẾP TRONG SQL
    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.viewsCount = COALESCE(p.viewsCount, 0) + 1 WHERE p.id = :id")
    int incrementViewsCount(@Param("id") Long id);
}