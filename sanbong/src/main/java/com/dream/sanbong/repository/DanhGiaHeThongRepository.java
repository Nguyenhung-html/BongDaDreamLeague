package com.dream.sanbong.repository;

import com.dream.sanbong.entity.DanhGiaHeThong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DanhGiaHeThongRepository extends JpaRepository<DanhGiaHeThong, UUID> {
    // Lấy danh sách các đánh giá được phép hiển thị, sắp xếp mới nhất lên đầu
    List<DanhGiaHeThong> findByHienThiTrueOrderByNgayDanhGiaDesc();
}