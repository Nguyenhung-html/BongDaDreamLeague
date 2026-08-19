package com.dream.sanbong.repository;

import com.dream.sanbong.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ThongBaoRepository extends JpaRepository<ThongBao, UUID> {

    /** Danh sách thông báo của 1 người dùng, mới nhất trước — dùng cho GET /api/thong-bao */
    List<ThongBao> findByNguoiDungIdOrderByNgayTaoDesc(UUID nguoiDungId);

    /** Đếm số thông báo chưa đọc — dùng cho GET /api/thong-bao/chua-doc */
    long countByNguoiDungIdAndDaDocFalse(UUID nguoiDungId);

    /** Đánh dấu tất cả thông báo của 1 người dùng là đã đọc — dùng cho PUT /api/thong-bao/da-doc-tat-ca */
    @Modifying
    @Query("UPDATE ThongBao t SET t.daDoc = true WHERE t.nguoiDung.id = :nguoiDungId AND t.daDoc = false")
    void danhDauTatCaDaDoc(@Param("nguoiDungId") UUID nguoiDungId);

    /**
     * Xoá toàn bộ thông báo của 1 người dùng — bắt buộc phải gọi trước khi xoá
     * NguoiDung, vì THONG_BAO.nguoi_dung_id là khoá ngoại nullable = false.
     * Dùng trong AdminNhanVienService.xoa(...).
     */
    void deleteByNguoiDungId(UUID nguoiDungId);

    // Đánh dấu 1 thông báo cụ thể là đã đọc (PUT /{id}/da-doc) không cần method
    // riêng — dùng findById() + set daDoc(true) + save() ngay trong Service là đủ.
}