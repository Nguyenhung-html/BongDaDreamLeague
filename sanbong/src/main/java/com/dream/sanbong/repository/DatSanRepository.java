package com.dream.sanbong.repository;

import com.dream.sanbong.entity.DatSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // ── Dashboard queries (SCRUM-76) ───────────────────────────────

    /**
     * Đếm số lượt đặt sân hợp lệ theo ngày (loại trừ các đơn bị hủy)
     */
    @Query(value = """
        SELECT COUNT(ds.id) FROM DAT_SAN ds
        WHERE ds.ngay_da = :ngay
          AND ds.trang_thai NOT IN ('DA_HUY')
        """, nativeQuery = true)
    long demSoLuotDatTheoNgay(@Param("ngay") LocalDate ngay);

    /**
     * Tính tổng doanh thu theo ngày (chỉ tính các đơn đã hoàn thành/thành công)
     */
    @Query(value = """
        SELECT COALESCE(SUM(ds.tong_tien), 0) FROM DAT_SAN ds
        WHERE ds.ngay_da = :ngay
          AND ds.trang_thai IN ('THANH_TOAN', 'HOAN_THANH')
        """, nativeQuery = true)
    BigDecimal tinhTongDoanhThuTheoNgay(@Param("ngay") LocalDate ngay);

    @Query(value = """
        SELECT COUNT(ds.id) FROM DAT_SAN ds
        WHERE ds.nguoi_dung_id = :nguoiDungId
          AND ds.trang_thai IN ('HOAN_THANH', 'DA_THANH_TOAN', 'THANH_TOAN')
          AND ds.ngay_dat >= DATEADD(day, -30, GETDATE())
        """, nativeQuery = true)
    long demSoLuotDatVoucher30Ngay(@Param("nguoiDungId") UUID nguoiDungId);

    // ── Admin queries ──────────────────────────────────────────────

    // Tổng số đơn của 1 khách (kể cả đã huỷ) — dùng cho trang quản lý khách hàng
    long countByNguoiDungId(UUID nguoiDungId);

    // Số đơn gắn với 1 sân — dùng để chặn xoá sân đang có lịch sử đặt
    long countBySanBongId(UUID sanBongId);

    /**
     * Tổng chi tiêu thực tế của 1 khách hàng: chỉ cộng các đơn đã hoàn thành.
     */
    @Query(value = """
        SELECT COALESCE(SUM(ds.tong_tien), 0) FROM DAT_SAN ds
        WHERE ds.nguoi_dung_id = :nguoiDungId
          AND ds.trang_thai = 'HOAN_THANH'
        """, nativeQuery = true)
    BigDecimal tinhTongChiTieu(@Param("nguoiDungId") UUID nguoiDungId);

    /**
     * Số đơn còn hiệu lực (chưa huỷ, chưa hoàn thành) của 1 khách —
     * dùng để cảnh báo trước khi Admin khoá tài khoản.
     */
    @Query(value = """
        SELECT COUNT(ds.id) FROM DAT_SAN ds
        WHERE ds.nguoi_dung_id = :nguoiDungId
          AND ds.trang_thai NOT IN ('DA_HUY', 'HOAN_THANH')
        """, nativeQuery = true)
    long demDonDangHoatDong(@Param("nguoiDungId") UUID nguoiDungId);

    // Đơn trong khoảng ngày đá (Admin lọc theo khoảng thời gian)
    List<DatSan> findByNgayDaBetweenOrderByNgayDaDescGioBatDauAsc(LocalDate tuNgay, LocalDate denNgay);

    // Bổ sung vào DatSanRepository.java
    @Query("SELECT COUNT(d) FROM DatSan d WHERE (d.ipAddress = :ip OR d.nguoiDung.id = :userId) " +
        "AND d.trangThai = 'CHO_XAC_NHAN' AND d.expireAt > :now")
    long countActiveHoldSlots(@Param("ip") String ip, @Param("userId") UUID userId, @Param("now") LocalDateTime now);

    List<DatSan> findByTrangThaiAndExpireAtBefore(String trangThai, LocalDateTime expireAt);
}