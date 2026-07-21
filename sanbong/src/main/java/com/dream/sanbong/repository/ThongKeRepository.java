package com.dream.sanbong.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Custom repository chuyên xử lý các query thống kê phức tạp.
 * Dùng JdbcTemplate thay vì Spring Data JPA để linh hoạt hơn
 * với native SQL và tránh phình to các repository chính.
 */
@Repository
public class ThongKeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ThongKeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD — Summary Cards
    // ══════════════════════════════════════════════════════════════

    /** Tổng khách hàng (vai_tro = 'USER') */
    public long demTongKhachHang() {
        Long count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM USERS WHERE vai_tro = 'USER'",
            Long.class
        );
        return count != null ? count : 0;
    }

    /** Số đơn đặt sân hôm nay (không tính DA_HUY) */
    public long demDonHomNay() {
        Long count = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*) FROM DAT_SAN
            WHERE CAST(ngay_da AS DATE) = CAST(GETDATE() AS DATE)
              AND trang_thai NOT IN ('DA_HUY')
            """,
            Long.class
        );
        return count != null ? count : 0;
    }

    /** Doanh thu hôm nay (đơn HOAN_THANH) */
    public BigDecimal doanhThuHomNay() {
        BigDecimal total = jdbcTemplate.queryForObject(
            """
            SELECT COALESCE(SUM(tong_tien), 0) FROM DAT_SAN
            WHERE CAST(ngay_da AS DATE) = CAST(GETDATE() AS DATE)
              AND trang_thai = 'HOAN_THANH'
            """,
            BigDecimal.class
        );
        return total != null ? total : BigDecimal.ZERO;
    }

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD — Line Charts (12 tháng)
    // ══════════════════════════════════════════════════════════════

    /** Doanh thu theo 12 tháng trong năm → line chart 1 */
    public List<Map<String, Object>> doanhThuTheoThang(int nam) {
        return jdbcTemplate.queryForList(
            """
            SELECT
                MONTH(ds.ngay_da) AS nhan,
                COALESCE(SUM(ds.tong_tien), 0) AS gia_tri
            FROM DAT_SAN ds
            WHERE ds.trang_thai = 'HOAN_THANH'
              AND YEAR(ds.ngay_da) = ?
            GROUP BY MONTH(ds.ngay_da)
            ORDER BY MONTH(ds.ngay_da)
            """,
            nam
        );
    }

    /** Lượt đặt sân theo 12 tháng → line chart 2 */
    public List<Map<String, Object>> datSanTheoThang(int nam) {
        return jdbcTemplate.queryForList(
            """
            SELECT
                MONTH(ds.ngay_da) AS nhan,
                COUNT(*) AS gia_tri
            FROM DAT_SAN ds
            WHERE ds.trang_thai NOT IN ('DA_HUY')
              AND YEAR(ds.ngay_da) = ?
            GROUP BY MONTH(ds.ngay_da)
            ORDER BY MONTH(ds.ngay_da)
            """,
            nam
        );
    }

    // ══════════════════════════════════════════════════════════════
    // THỐNG KÊ DOANH THU — Summary Cards
    // ══════════════════════════════════════════════════════════════

    /** Tổng doanh thu theo bộ lọc tháng/năm */
    public BigDecimal tongDoanhThu(int nam, Integer thang) {
        String sql;
        Object[] params;

        if (thang != null) {
            sql = """
                SELECT COALESCE(SUM(tong_tien), 0) FROM DAT_SAN
                WHERE trang_thai = 'HOAN_THANH'
                  AND YEAR(ngay_da) = ? AND MONTH(ngay_da) = ?
                """;
            params = new Object[]{nam, thang};
        } else {
            sql = """
                SELECT COALESCE(SUM(tong_tien), 0) FROM DAT_SAN
                WHERE trang_thai = 'HOAN_THANH'
                  AND YEAR(ngay_da) = ?
                """;
            params = new Object[]{nam};
        }

        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
        return total != null ? total : BigDecimal.ZERO;
    }

    /** Đếm đơn theo trạng thái và bộ lọc tháng/năm */
    public long demDonTheoTrangThai(String trangThai, int nam, Integer thang) {
        String sql;
        Object[] params;

        if (thang != null) {
            sql = """
                SELECT COUNT(*) FROM DAT_SAN
                WHERE trang_thai = ?
                  AND YEAR(ngay_da) = ? AND MONTH(ngay_da) = ?
                """;
            params = new Object[]{trangThai, nam, thang};
        } else {
            sql = """
                SELECT COUNT(*) FROM DAT_SAN
                WHERE trang_thai = ?
                  AND YEAR(ngay_da) = ?
                """;
            params = new Object[]{trangThai, nam};
        }

        Long count = jdbcTemplate.queryForObject(sql, Long.class, params);
        return count != null ? count : 0;
    }

    // ══════════════════════════════════════════════════════════════
    // THỐNG KÊ DOANH THU — Line Chart (ngày trong tháng)
    // ══════════════════════════════════════════════════════════════

    /** Doanh thu theo từng ngày trong tháng */
    public List<Map<String, Object>> doanhThuTheoNgay(int nam, int thang) {
        return jdbcTemplate.queryForList(
            """
            SELECT
                DAY(ds.ngay_da) AS nhan,
                COALESCE(SUM(ds.tong_tien), 0) AS gia_tri
            FROM DAT_SAN ds
            WHERE ds.trang_thai = 'HOAN_THANH'
              AND YEAR(ds.ngay_da) = ?
              AND MONTH(ds.ngay_da) = ?
            GROUP BY DAY(ds.ngay_da)
            ORDER BY DAY(ds.ngay_da)
            """,
            nam, thang
        );
    }

    // ══════════════════════════════════════════════════════════════
    // THỐNG KÊ DOANH THU — Bảng chi tiết theo sân
    // ══════════════════════════════════════════════════════════════

    /** Chi tiết doanh thu theo từng sân */
    public List<Map<String, Object>> chiTietTheoSan(int nam, Integer thang) {
        String sql;
        Object[] params;

        if (thang != null) {
            sql = """
                SELECT
                    sb.ten_san AS ten_san,
                    COUNT(ds.id) AS so_luot_dat,
                    COALESCE(SUM(DATEDIFF(MINUTE, ds.gio_bat_dau, ds.gio_ket_thuc) / 60.0), 0) AS so_gio,
                    COALESCE(SUM(ds.tong_tien), 0) AS doanh_thu
                FROM DAT_SAN ds
                JOIN SAN_BONG sb ON ds.san_bong_id = sb.id
                WHERE ds.trang_thai = 'HOAN_THANH'
                  AND YEAR(ds.ngay_da) = ? AND MONTH(ds.ngay_da) = ?
                GROUP BY sb.ten_san
                ORDER BY doanh_thu DESC
                """;
            params = new Object[]{nam, thang};
        } else {
            sql = """
                SELECT
                    sb.ten_san AS ten_san,
                    COUNT(ds.id) AS so_luot_dat,
                    COALESCE(SUM(DATEDIFF(MINUTE, ds.gio_bat_dau, ds.gio_ket_thuc) / 60.0), 0) AS so_gio,
                    COALESCE(SUM(ds.tong_tien), 0) AS doanh_thu
                FROM DAT_SAN ds
                JOIN SAN_BONG sb ON ds.san_bong_id = sb.id
                WHERE ds.trang_thai = 'HOAN_THANH'
                  AND YEAR(ds.ngay_da) = ?
                GROUP BY sb.ten_san
                ORDER BY doanh_thu DESC
                """;
            params = new Object[]{nam};
        }

        return jdbcTemplate.queryForList(sql, params);
    }
}
