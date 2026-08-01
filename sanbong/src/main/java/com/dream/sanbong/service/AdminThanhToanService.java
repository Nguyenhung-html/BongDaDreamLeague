package com.dream.sanbong.service;

import com.dream.sanbong.dto.AdminThanhToanPhanHoi;
import com.dream.sanbong.dto.AdminThanhToanTongHopPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.ThanhToan;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ Admin đối soát giao dịch thanh toán toàn hệ thống.
 * Khác StaffThanhToanService ở chỗ Admin xem được cả giao dịch thất bại,
 * giao dịch đã hoàn tiền, và có thêm bộ số liệu tổng hợp.
 */
@Service
public class AdminThanhToanService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ThanhToanRepository thanhToanRepo;
    private final DatSanRepository datSanRepo;
    private final SePayService sePayService;

    public AdminThanhToanService(ThanhToanRepository thanhToanRepo,
                                 DatSanRepository datSanRepo,
                                 SePayService sePayService) {
        this.thanhToanRepo = thanhToanRepo;
        this.datSanRepo = datSanRepo;
        this.sePayService = sePayService;
    }

    /**
     * Danh sách giao dịch với đầy đủ bộ lọc.
     *
     * @param trangThai  DANG_CHO | THANH_CONG | THAT_BAI | DA_HOAN_TIEN
     * @param loai       COC | THANH_TOAN_CON_LAI
     * @param phuongThuc QR | TIEN_MAT
     * @param tuKhoa     tìm theo mã giao dịch / tên khách / SĐT / mã đơn
     */
    public List<AdminThanhToanPhanHoi> layDanhSach(LocalDate tuNgay, LocalDate denNgay,
                                                   String trangThai, String loai,
                                                   String phuongThuc, String tuKhoa) {
        return locGiaoDich(tuNgay, denNgay, trangThai, loai, phuongThuc, tuKhoa)
                .stream()
                .map(this::sangDto)
                .toList();
    }

    /** Số liệu tổng hợp, dùng chung bộ lọc khoảng ngày với danh sách */
    public AdminThanhToanTongHopPhanHoi layTongHop(LocalDate tuNgay, LocalDate denNgay) {
        List<ThanhToan> danhSach = locGiaoDich(tuNgay, denNgay, null, null, null, null);

        return AdminThanhToanTongHopPhanHoi.builder()
                .tongGiaoDich(danhSach.size())
                .soThanhCong(dem(danhSach, "THANH_CONG"))
                .soDangCho(dem(danhSach, "DANG_CHO"))
                .soThatBai(dem(danhSach, "THAT_BAI"))
                .soHoanTien(dem(danhSach, "DA_HOAN_TIEN"))
                .tienThanhCong(tong(danhSach, "THANH_CONG"))
                .tienDangCho(tong(danhSach, "DANG_CHO"))
                .tienHoanTien(tong(danhSach, "DA_HOAN_TIEN"))
                .tienMat(tongTheoPhuongThuc(danhSach, "TIEN_MAT"))
                .tienChuyenKhoan(tongTheoPhuongThuc(danhSach, "QR"))
                .build();
    }

    public AdminThanhToanPhanHoi layChiTiet(UUID id) {
        return sangDto(timGiaoDich(id));
    }

    /** Tất cả giao dịch của một đơn đặt sân */
    public List<AdminThanhToanPhanHoi> layTheoDon(UUID datSanId) {
        return thanhToanRepo.findByDatSanId(datSanId).stream()
                .sorted(Comparator.comparing(ThanhToan::getNgayThanhToan,
                        Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())))
                .map(this::sangDto)
                .toList();
    }

    /**
     * Admin xác nhận một giao dịch đang chờ đã thu được tiền.
     * Nếu là giao dịch cọc thì đẩy luôn đơn sang trạng thái DA_COC.
     */
    @Transactional
    public AdminThanhToanPhanHoi xacNhan(UUID id) {
        ThanhToan tt = timGiaoDich(id);

        if ("THANH_CONG".equals(tt.getTrangThai())) {
            throw new RuntimeException("Lỗi: Giao dịch này đã được xác nhận rồi!");
        }
        if ("DA_HOAN_TIEN".equals(tt.getTrangThai())) {
            throw new RuntimeException("Lỗi: Giao dịch này đã hoàn tiền, không thể xác nhận lại!");
        }

        tt.setTrangThai("THANH_CONG");
        thanhToanRepo.save(tt);

        DatSan datSan = tt.getDatSan();
        if ("COC".equals(tt.getLoai()) && "CHO_XAC_NHAN".equals(datSan.getTrangThai())) {
            datSan.setTrangThai("DA_COC");
            datSanRepo.save(datSan);
        }

        return sangDto(tt);
    }

    /**
     * Hoàn tiền một giao dịch đã thu thành công.
     * Dùng lại SePayService.hoanTien để trạng thái và ngày hoàn tiền
     * được ghi thống nhất với luồng khách tự huỷ sân.
     */
    @Transactional
    public AdminThanhToanPhanHoi hoanTien(UUID id) {
        ThanhToan tt = timGiaoDich(id);

        if (!"THANH_CONG".equals(tt.getTrangThai())) {
            throw new RuntimeException("Lỗi: Chỉ hoàn tiền được cho giao dịch đã thu thành công!");
        }

        sePayService.hoanTien(tt.getId());
        return sangDto(timGiaoDich(id));
    }

    // ── Helpers ───────────────────────────────────────────────────

    private List<ThanhToan> locGiaoDich(LocalDate tuNgay, LocalDate denNgay,
                                        String trangThai, String loai,
                                        String phuongThuc, String tuKhoa) {
        List<ThanhToan> danhSach = thanhToanRepo.findAllByOrderByNgayThanhToanDesc();

        if (tuNgay != null) {
            danhSach = danhSach.stream()
                    .filter(tt -> tt.getNgayThanhToan() != null
                            && !tt.getNgayThanhToan().toLocalDate().isBefore(tuNgay))
                    .toList();
        }
        if (denNgay != null) {
            danhSach = danhSach.stream()
                    .filter(tt -> tt.getNgayThanhToan() != null
                            && !tt.getNgayThanhToan().toLocalDate().isAfter(denNgay))
                    .toList();
        }
        if (coGiaTri(trangThai)) {
            danhSach = danhSach.stream()
                    .filter(tt -> trangThai.equalsIgnoreCase(tt.getTrangThai()))
                    .toList();
        }
        if (coGiaTri(loai)) {
            danhSach = danhSach.stream()
                    .filter(tt -> loai.equalsIgnoreCase(tt.getLoai()))
                    .toList();
        }
        if (coGiaTri(phuongThuc)) {
            danhSach = danhSach.stream()
                    .filter(tt -> phuongThuc.equalsIgnoreCase(tt.getPhuongThuc()))
                    .toList();
        }
        if (coGiaTri(tuKhoa)) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(tt -> chuaChuoi(tt.getMaGiaoDich(), tim)
                            || chuaChuoi(tt.getDatSan().getHoTenDat(), tim)
                            || chuaChuoi(tt.getDatSan().getSoDienThoai(), tim)
                            || chuaChuoi(maDon(tt.getDatSan()), tim))
                    .toList();
        }

        return danhSach;
    }

    /** Mapper dùng chung, các service Admin khác cũng gọi lại hàm này */
    public AdminThanhToanPhanHoi sangDto(ThanhToan tt) {
        DatSan ds = tt.getDatSan();
        return AdminThanhToanPhanHoi.builder()
                .id(tt.getId())
                .datSanId(ds.getId())
                .maDon(maDon(ds))
                .tenKhach(ds.getHoTenDat())
                .soDienThoai(ds.getSoDienThoai())
                .tenSan(ds.getSanBong().getTenSan())
                .ngayDa(ds.getNgayDa().toString())
                .gioBatDau(ds.getGioBatDau().toString())
                .gioKetThuc(ds.getGioKetThuc().toString())
                .soTien(tt.getSoTien())
                .loai(tt.getLoai())
                .phuongThuc(tt.getPhuongThuc())
                .maGiaoDich(tt.getMaGiaoDich())
                .trangThai(tt.getTrangThai())
                .ngayThanhToan(tt.getNgayThanhToan() != null ? tt.getNgayThanhToan().format(FMT) : "")
                .ngayHoanTien(tt.getNgayHoanTien() != null ? tt.getNgayHoanTien().format(FMT) : "")
                .trangThaiDon(ds.getTrangThai())
                .build();
    }

    private ThanhToan timGiaoDich(UUID id) {
        return thanhToanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy giao dịch thanh toán!"));
    }

    private String maDon(DatSan ds) {
        return ds.getId().toString().substring(0, 8).toUpperCase();
    }

    private long dem(List<ThanhToan> danhSach, String trangThai) {
        return danhSach.stream().filter(tt -> trangThai.equals(tt.getTrangThai())).count();
    }

    private BigDecimal tong(List<ThanhToan> danhSach, String trangThai) {
        return danhSach.stream()
                .filter(tt -> trangThai.equals(tt.getTrangThai()))
                .map(ThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal tongTheoPhuongThuc(List<ThanhToan> danhSach, String phuongThuc) {
        return danhSach.stream()
                .filter(tt -> "THANH_CONG".equals(tt.getTrangThai())
                        && phuongThuc.equals(tt.getPhuongThuc()))
                .map(ThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
