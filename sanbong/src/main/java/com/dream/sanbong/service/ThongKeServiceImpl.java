package com.dream.sanbong.service;

import com.dream.sanbong.dto.*;
import com.dream.sanbong.repository.ThongKeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ThongKeServiceImpl implements ThongKeService {

    private final ThongKeRepository thongKeRepo;

    public ThongKeServiceImpl(ThongKeRepository thongKeRepo) {
        this.thongKeRepo = thongKeRepo;
    }

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════

    @Override
    public DashboardTongQuanDTO layTongQuanDashboard(int nam) {
        DashboardTongQuanDTO dto = new DashboardTongQuanDTO();

        // 1. Summary cards
        dto.setTongKhachHang(thongKeRepo.demTongKhachHang());
        dto.setDatSanHomNay(thongKeRepo.demDonHomNay());
        dto.setDoanhThuHomNay(thongKeRepo.doanhThuHomNay());

        // 2. Line chart 1 — Doanh thu theo tháng (fill đủ 12 tháng)
        List<Map<String, Object>> rawDoanhThu = thongKeRepo.doanhThuTheoThang(nam);
        dto.setDoanhThuTheoThang(fillDuThang(rawDoanhThu));

        // 3. Line chart 2 — Lượt đặt sân theo tháng (fill đủ 12 tháng)
        List<Map<String, Object>> rawDatSan = thongKeRepo.datSanTheoThang(nam);
        dto.setDatSanTheoThang(fillDuThang(rawDatSan));

        return dto;
    }

    // ══════════════════════════════════════════════════════════════
    // THỐNG KÊ DOANH THU CHI TIẾT
    // ══════════════════════════════════════════════════════════════

    @Override
    public DoanhThuChiTietDTO layDoanhThuChiTiet(int nam, Integer thang) {
        DoanhThuChiTietDTO dto = new DoanhThuChiTietDTO();

        // 1. Summary cards
        dto.setTongDoanhThu(thongKeRepo.tongDoanhThu(nam, thang));
        dto.setDonHoanThanh(thongKeRepo.demDonTheoTrangThai("HOAN_THANH", nam, thang));
        dto.setDonDaHuy(thongKeRepo.demDonTheoTrangThai("DA_HUY", nam, thang));

        // Tính tỷ lệ hủy
        long tongDon = dto.getDonHoanThanh() + dto.getDonDaHuy()
                     + thongKeRepo.demDonTheoTrangThai("CHO_XAC_NHAN", nam, thang)
                     + thongKeRepo.demDonTheoTrangThai("DA_XAC_NHAN", nam, thang);
        dto.setTyLeHuy(tongDon > 0
            ? Math.round((dto.getDonDaHuy() * 100.0 / tongDon) * 10) / 10.0
            : 0);

        // 2. Line chart — Doanh thu theo ngày
        if (thang != null) {
            List<Map<String, Object>> rawNgay = thongKeRepo.doanhThuTheoNgay(nam, thang);
            int soNgay = YearMonth.of(nam, thang).lengthOfMonth();
            dto.setDoanhThuTheoNgay(fillDuNgay(rawNgay, soNgay));
        } else {
            // Nếu không chọn tháng cụ thể → hiển thị theo 12 tháng
            List<Map<String, Object>> rawThang = thongKeRepo.doanhThuTheoThang(nam);
            dto.setDoanhThuTheoNgay(fillDuThang(rawThang));
        }

        // 3. Bảng chi tiết theo sân
        List<Map<String, Object>> rawSan = thongKeRepo.chiTietTheoSan(nam, thang);
        List<ChiTietSanDTO> chiTiet = new ArrayList<>();
        for (Map<String, Object> row : rawSan) {
            ChiTietSanDTO san = new ChiTietSanDTO();
            san.setTenSan((String) row.get("ten_san"));
            san.setSoLuotDat(toLong(row.get("so_luot_dat")));
            san.setSoGio(toDouble(row.get("so_gio")));
            san.setDoanhThu(toBigDecimal(row.get("doanh_thu")));
            chiTiet.add(san);
        }
        dto.setChiTietTheoSan(chiTiet);

        return dto;
    }

    // ══════════════════════════════════════════════════════════════
    // HELPERS — Fill đủ data cho line chart mượt
    // ══════════════════════════════════════════════════════════════

    /**
     * Fill đủ 12 tháng. Tháng nào không có dữ liệu → giá trị 0.
     */
    private List<ThongKeDTO> fillDuThang(List<Map<String, Object>> rawData) {
        // Tạo map tra cứu nhanh: month -> value
        java.util.Map<Integer, BigDecimal> dataMap = new java.util.HashMap<>();
        for (Map<String, Object> row : rawData) {
            int month = toInt(row.get("nhan"));
            BigDecimal value = toBigDecimal(row.get("gia_tri"));
            dataMap.put(month, value);
        }

        List<ThongKeDTO> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(new ThongKeDTO(
                "Tháng " + m,
                dataMap.getOrDefault(m, BigDecimal.ZERO)
            ));
        }
        return result;
    }

    /**
     * Fill đủ ngày trong tháng. Ngày nào không có dữ liệu → giá trị 0.
     */
    private List<ThongKeDTO> fillDuNgay(List<Map<String, Object>> rawData, int soNgay) {
        java.util.Map<Integer, BigDecimal> dataMap = new java.util.HashMap<>();
        for (Map<String, Object> row : rawData) {
            int day = toInt(row.get("nhan"));
            BigDecimal value = toBigDecimal(row.get("gia_tri"));
            dataMap.put(day, value);
        }

        List<ThongKeDTO> result = new ArrayList<>();
        for (int d = 1; d <= soNgay; d++) {
            result.add(new ThongKeDTO(
                String.valueOf(d),
                dataMap.getOrDefault(d, BigDecimal.ZERO)
            ));
        }
        return result;
    }

    // ── Type conversion helpers (JdbcTemplate returns Object) ──

    private int toInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        return 0;
    }

    private long toLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        return 0;
    }

    private double toDouble(Object val) {
        if (val instanceof Number) return ((Number) val).doubleValue();
        return 0;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).longValue());
        return BigDecimal.ZERO;
    }
}
