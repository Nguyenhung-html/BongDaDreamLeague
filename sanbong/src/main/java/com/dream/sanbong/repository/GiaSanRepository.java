package com.dream.sanbong.repository;

import com.dream.sanbong.entity.GiaSan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GiaSanRepository extends JpaRepository<GiaSan, UUID> {

    // Lấy tất cả khung giờ của một sân, sắp xếp theo giờ bắt đầu
    List<GiaSan> findBySanBongIdOrderByGioBatDauAsc(UUID sanBongId);
}
