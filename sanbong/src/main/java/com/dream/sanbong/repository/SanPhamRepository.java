package com.dream.sanbong.repository;

import com.dream.sanbong.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, UUID> {

    // Danh sách hiển thị cho khách (chỉ sản phẩm còn bán)
    List<SanPham> findByConBanTrueOrderByLoaiAscTenSanPhamAsc();

    // Danh sách đầy đủ cho Admin (kể cả đã ngừng bán)
    List<SanPham> findAllByOrderByLoaiAscTenSanPhamAsc();
}