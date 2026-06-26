package com.dream.sanbong.repository;

import com.dream.sanbong.entity.SanBong;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SanBongRepository extends JpaRepository<SanBong, UUID> {

    // Lấy tất cả sân đang hoạt động
    List<SanBong> findByTrangThai(String trangThai);

    // Lọc theo loại sân (5 hoặc 7) và trạng thái
    List<SanBong> findByLoaiSanAndTrangThai(Integer loaiSan, String trangThai);
}
