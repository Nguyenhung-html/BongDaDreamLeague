package com.dream.sanbong.repository;

import com.dream.sanbong.entity.DichVuDatSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DichVuDatSanRepository extends JpaRepository<DichVuDatSan, UUID> {

    List<DichVuDatSan> findByDatSanIdOrderByNgayDatAsc(UUID datSanId);

    // Tìm xem đơn này đã gọi đúng sản phẩm này chưa (để cộng dồn số lượng thay vì tạo dòng mới)
    java.util.Optional<DichVuDatSan> findByDatSanIdAndSanPhamId(UUID datSanId, UUID sanPhamId);
}