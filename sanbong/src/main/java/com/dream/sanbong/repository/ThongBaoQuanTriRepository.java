package com.dream.sanbong.repository;

import com.dream.sanbong.entity.ThongBaoQuanTri;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ThongBaoQuanTriRepository extends JpaRepository<ThongBaoQuanTri, UUID> {
    List<ThongBaoQuanTri> findAllByOrderByNgayTaoDesc();
}