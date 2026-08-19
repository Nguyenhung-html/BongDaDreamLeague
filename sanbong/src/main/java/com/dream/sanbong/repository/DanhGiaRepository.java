package com.dream.sanbong.repository;

import com.dream.sanbong.entity.DanhGia;
import com.dream.sanbong.dto.DanhGiaResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, UUID> {

    // Lấy danh sách bình luận được phép hiển thị của 1 sân bóng
    @Query("SELECT new com.dream.sanbong.dto.DanhGiaResponse(d.id, d.nguoiDung.hoTen, d.soSao, d.noiDung, d.ngayDanhGia) " +
           "FROM DanhGia d WHERE d.sanBong.id = :sanBongId AND d.hienThi = true ORDER BY d.ngayDanhGia DESC")
    List<DanhGiaResponse> findActiveEvaluationsBySanBongId(@Param("sanBongId") UUID sanBongId);

    // KIỂM TRA NGHIỆP VỤ: Đếm số lượng đơn đặt sân đã 'HOAN_THANH' của User tại Sân này
    // Bạn nhớ check xem bảng DAT_SAN trong code Java của bạn đang đặt chuỗi trạng thái hoàn thành là gì nhé (ví dụ: 'HOAN_THANH' hoặc 'DA_DA')
    @Query(value = "SELECT COUNT(*) FROM DAT_SAN WHERE nguoi_dung_id = :userId AND san_bong_id = :sanBongId AND trang_thai = 'HOAN_THANH'", nativeQuery = true)
    long countCompletedOrders(@Param("userId") UUID userId, @Param("sanBongId") UUID sanBongId);

    

    // KIỂM TRA NGHIỆP VỤ: User này đã từng đánh giá sân này chưa
    long countByNguoiDungIdAndSanBongId(UUID nguoiDungId, UUID sanBongId);

    List<DanhGia> findBySanBongIdOrderByNgayDanhGiaDesc(UUID sanBongId);
}