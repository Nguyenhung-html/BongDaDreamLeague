package com.dream.sanbong.repository;

import com.dream.sanbong.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, UUID> {

    List<ThanhToan> findByDatSanId(UUID datSanId);

    // Tìm thanh toán cọc chờ xử lý của một đơn
    Optional<ThanhToan> findByDatSanIdAndLoai(UUID datSanId, String loai);

    // Tất cả thanh toán sắp xếp mới nhất (cho Staff)
    List<ThanhToan> findAllByOrderByNgayThanhToanDesc();

    // Thanh toán trong khoảng thời gian
    @Query(value = """
        SELECT t.* FROM THANH_TOAN t
        WHERE t.ngay_thanh_toan BETWEEN :tuNgay AND :denNgay
        ORDER BY t.ngay_thanh_toan DESC
        """, nativeQuery = true)
    List<ThanhToan> findByNgayThanhToanBetweenOrderByNgayThanhToanDesc(
        @Param("tuNgay") LocalDateTime tuNgay,
        @Param("denNgay") LocalDateTime denNgay
    );
}
