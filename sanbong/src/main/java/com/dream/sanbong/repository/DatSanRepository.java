package com.dream.sanbong.repository;

import com.dream.sanbong.entity.DatSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface DatSanRepository extends JpaRepository<DatSan, UUID> {

    // Lịch sử đặt sân của người dùng, sắp xếp mới nhất trước
    List<DatSan> findByNguoiDungIdOrderByNgayDatDesc(UUID nguoiDungId);

    /**
     * Kiểm tra xem sân có bị đặt trong khung giờ đó chưa.
     * Điều kiện: cùng sân, cùng ngày, trạng thái chưa huỷ, và khoảng giờ giao nhau.
     * Hai khoảng giờ [A,B] và [C,D] giao nhau khi: A < D và C < B
     */
    @Query(value = """
        SELECT COUNT(ds.id) FROM DAT_SAN ds
        WHERE ds.san_bong_id = :sanId
          AND ds.ngay_da = :ngay
          AND ds.trang_thai NOT IN ('DA_HUY')
          AND ds.gio_bat_dau < CAST(:gioKet AS TIME)
          AND ds.gio_ket_thuc > CAST(:gioBat AS TIME)
        """, nativeQuery = true)
    long demTrungLich(
        @Param("sanId") UUID sanId,
        @Param("ngay") LocalDate ngay,
        @Param("gioBat") LocalTime gioBat,
        @Param("gioKet") LocalTime gioKet
    );

    /**
     * Đếm số sân khách hàng đang đặt trong ngày hôm nay chưa huỷ.
     * Giới hạn tối đa 2 sân / người / ngày.
     */
    @Query(value = """
        SELECT COUNT(ds.id) FROM DAT_SAN ds
        WHERE ds.nguoi_dung_id = :nguoiDungId
          AND ds.ngay_da = :ngay
          AND ds.trang_thai NOT IN ('DA_HUY')
        """, nativeQuery = true)
    long demSanDatTrongNgay(
        @Param("nguoiDungId") UUID nguoiDungId,
        @Param("ngay") LocalDate ngay
    );

    // Danh sách tất cả đặt sân theo ngày (cho staff/admin)
    List<DatSan> findBySanBongIdAndNgayDaOrderByGioBatDauAsc(UUID sanId, LocalDate ngay);

    // Danh sách đặt sân theo trạng thái (cho admin)
    List<DatSan> findByTrangThaiOrderByNgayDatDesc(String trangThai);

    // ── Staff queries ──────────────────────────────────────────────

    // Tất cả đơn đặt sân, mới nhất trước (Staff xem toàn bộ)
    List<DatSan> findAllByOrderByNgayDatDesc();

    // Lịch sân theo ngày, loại trừ đơn đã huỷ
    List<DatSan> findBySanBongIdAndNgayDaAndTrangThaiNotOrderByGioBatDauAsc(
        UUID sanBongId, LocalDate ngayDa, String trangThai);

    // Tất cả đơn theo ngày (cho calendar view)
    List<DatSan> findByNgayDaAndTrangThaiNotOrderByGioBatDauAsc(LocalDate ngayDa, String trangThai);

    // Tất cả đơn theo ngày
    List<DatSan> findByNgayDaOrderByGioBatDauAsc(LocalDate ngayDa);
}
