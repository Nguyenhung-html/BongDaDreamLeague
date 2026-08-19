package com.dream.sanbong.repository;

import com.dream.sanbong.entity.GiaSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface GiaSanRepository extends JpaRepository<GiaSan, UUID> {

    // Lấy tất cả khung giờ của một sân, sắp xếp theo giờ bắt đầu
    List<GiaSan> findBySanBongIdOrderByGioBatDauAsc(UUID sanBongId);

    // ── Admin queries ──────────────────────────────────────────────

    // Xoá toàn bộ bảng giá của 1 sân (dùng khi Admin xoá sân)
    void deleteBySanBongId(UUID sanBongId);

    /**
     * Kiểm tra trùng khung giờ khi Admin thêm/sửa giá:
     * hai khoảng [A,B) và [C,D) giao nhau khi A < D và C < B.
     *
     * Phải viết native query kèm CAST(... AS TIME) chứ không dùng được derived query:
     * SQL Server bind LocalTime thành datetime nên so sánh thẳng với cột TIME sẽ báo
     * "The data types time and datetime are incompatible in the less than operator."
     * (DatSanRepository.demTrungLich cũng xử lý theo đúng cách này.)
     */
    @Query(value = """
        SELECT * FROM GIA_SAN gs
        WHERE gs.san_bong_id = :sanBongId
          AND gs.gio_bat_dau < CAST(:gioKetThuc AS TIME)
          AND gs.gio_ket_thuc > CAST(:gioBatDau AS TIME)
        """, nativeQuery = true)
    List<GiaSan> timKhungGioTrung(
        @Param("sanBongId") UUID sanBongId,
        @Param("gioBatDau") java.time.LocalTime gioBatDau,
        @Param("gioKetThuc") java.time.LocalTime gioKetThuc);
}
