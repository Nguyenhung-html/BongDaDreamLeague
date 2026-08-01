package com.dream.sanbong.service;

import com.dream.sanbong.dto.ChiTietDichVuPhanHoi;
import com.dream.sanbong.dto.DongHoaDonPhanHoi;
import com.dream.sanbong.dto.HoaDonChiTietPhanHoi;
import com.dream.sanbong.dto.HoaDonPhanHoi;
import com.dream.sanbong.dto.HoaDonThongKePhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.GiaHanGio;
import com.dream.sanbong.entity.ThanhToan;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.GiaHanGioRepository;
import com.dream.sanbong.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ Admin xem và tra cứu dữ liệu hoá đơn.
 *
 * Hệ thống không có bảng HOA_DON riêng: mỗi đơn đặt sân chính là một hoá đơn,
 * được dựng lại từ tiền sân + các lần gia hạn giờ + đồ uống/dịch vụ đã gọi,
 * đối chiếu với các bản ghi trong bảng THANH_TOAN.
 */
@Service
public class AdminHoaDonService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final String CHUA_THANH_TOAN = "CHUA_THANH_TOAN";
    public static final String THANH_TOAN_MOT_PHAN = "THANH_TOAN_MOT_PHAN";
    public static final String DA_THANH_TOAN_DU = "DA_THANH_TOAN_DU";

    private final DatSanRepository datSanRepo;
    private final GiaHanGioRepository giaHanGioRepo;
    private final ThanhToanRepository thanhToanRepo;
    private final AdminThanhToanService adminThanhToanService;
    private final TinhTienDonHelper tinhTien;

    public AdminHoaDonService(DatSanRepository datSanRepo,
                              GiaHanGioRepository giaHanGioRepo,
                              ThanhToanRepository thanhToanRepo,
                              AdminThanhToanService adminThanhToanService,
                              TinhTienDonHelper tinhTien) {
        this.datSanRepo = datSanRepo;
        this.giaHanGioRepo = giaHanGioRepo;
        this.thanhToanRepo = thanhToanRepo;
        this.adminThanhToanService = adminThanhToanService;
        this.tinhTien = tinhTien;
    }

    /**
     * Danh sách hoá đơn với bộ lọc.
     *
     * @param trangThaiThanhToan CHUA_THANH_TOAN | THANH_TOAN_MOT_PHAN | DA_THANH_TOAN_DU
     * @param baoGomDaHuy        true thì lấy cả hoá đơn của đơn đã huỷ (mặc định bỏ qua)
     * @param tuKhoa             tìm theo tên khách / SĐT / tên sân / mã hoá đơn
     */
    public List<HoaDonPhanHoi> layDanhSach(LocalDate tuNgay, LocalDate denNgay,
                                           String trangThaiThanhToan, boolean baoGomDaHuy,
                                           String tuKhoa) {
        List<HoaDonPhanHoi> danhSach = locDon(tuNgay, denNgay, baoGomDaHuy).stream()
                .map(this::dungHoaDon)
                .toList();

        if (coGiaTri(trangThaiThanhToan)) {
            danhSach = danhSach.stream()
                    .filter(hd -> trangThaiThanhToan.equalsIgnoreCase(hd.getTrangThaiThanhToan()))
                    .toList();
        }
        if (coGiaTri(tuKhoa)) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(hd -> chuaChuoi(hd.getTenKhach(), tim)
                            || chuaChuoi(hd.getSoDienThoai(), tim)
                            || chuaChuoi(hd.getTenSan(), tim)
                            || chuaChuoi(hd.getMaHoaDon(), tim))
                    .toList();
        }

        return danhSach;
    }

    /** Hoá đơn đầy đủ của 1 đơn đặt sân, đủ dữ liệu để in hoặc xuất file */
    public HoaDonChiTietPhanHoi layChiTiet(UUID datSanId) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy hoá đơn của đơn đặt sân này!"));

        return HoaDonChiTietPhanHoi.builder()
                .thongTinChung(dungHoaDon(datSan))
                .cacDong(dungCacDong(datSan))
                .danhSachThanhToan(adminThanhToanService.layTheoDon(datSanId))
                .build();
    }

    /** Số liệu tổng hợp trên cùng bộ lọc khoảng ngày */
    public HoaDonThongKePhanHoi layThongKe(LocalDate tuNgay, LocalDate denNgay, boolean baoGomDaHuy) {
        List<HoaDonPhanHoi> danhSach = locDon(tuNgay, denNgay, baoGomDaHuy).stream()
                .map(this::dungHoaDon)
                .toList();

        return HoaDonThongKePhanHoi.builder()
                .tongHoaDon(danhSach.size())
                .soDaThanhToanDu(dem(danhSach, DA_THANH_TOAN_DU))
                .soThanhToanMotPhan(dem(danhSach, THANH_TOAN_MOT_PHAN))
                .soChuaThanhToan(dem(danhSach, CHUA_THANH_TOAN))
                .tongGiaTri(cong(danhSach, HoaDonPhanHoi::getTongCong))
                .tongDaThu(cong(danhSach, HoaDonPhanHoi::getDaThanhToan))
                .tongConLai(cong(danhSach, HoaDonPhanHoi::getConLai))
                .build();
    }

    // ── Dựng hoá đơn ─────────────────────────────────────────────

    private HoaDonPhanHoi dungHoaDon(DatSan d) {
        BigDecimal tienGiaHan = tongTienGiaHan(d.getId());
        BigDecimal tongTienSan = d.getTongTien() != null ? d.getTongTien() : BigDecimal.ZERO;

        // tongTien trong DB đã cộng sẵn tiền gia hạn, nên tiền sân gốc phải trừ ra
        BigDecimal tienSanGoc = tongTienSan.subtract(tienGiaHan);
        if (tienSanGoc.compareTo(BigDecimal.ZERO) < 0) {
            tienSanGoc = BigDecimal.ZERO;
        }

        BigDecimal tienDichVu = tinhTien.tongTienDichVu(d.getId());
        BigDecimal tongCong = tienSanGoc.add(tienGiaHan).add(tienDichVu);
        BigDecimal daThanhToan = tinhTien.daThanhToan(d.getId());
        BigDecimal conLai = tongCong.subtract(daThanhToan);
        if (conLai.compareTo(BigDecimal.ZERO) < 0) {
            conLai = BigDecimal.ZERO;
        }

        return HoaDonPhanHoi.builder()
                .datSanId(d.getId())
                .maHoaDon("HD-" + d.getId().toString().substring(0, 8).toUpperCase())
                .tenKhach(d.getHoTenDat())
                .soDienThoai(d.getSoDienThoai())
                .emailTaiKhoan(d.getNguoiDung() != null ? d.getNguoiDung().getEmail() : "")
                .tenSan(d.getSanBong().getTenSan())
                .ngayDa(d.getNgayDa().toString())
                .gioBatDau(d.getGioBatDau().toString())
                .gioKetThuc(d.getGioKetThuc().toString())
                .tienSan(tienSanGoc)
                .tienGiaHan(tienGiaHan)
                .tienDichVu(tienDichVu)
                .tongCong(tongCong)
                .daThanhToan(daThanhToan)
                .conLai(conLai)
                .trangThaiDon(d.getTrangThai())
                .trangThaiThanhToan(xacDinhTrangThaiThanhToan(tongCong, daThanhToan))
                .ngayXuat(xacDinhNgayXuat(d))
                .build();
    }

    /** Các dòng chi tiết: 1 dòng tiền sân + mỗi lần gia hạn 1 dòng + mỗi món dịch vụ 1 dòng */
    private List<DongHoaDonPhanHoi> dungCacDong(DatSan d) {
        List<DongHoaDonPhanHoi> cacDong = new ArrayList<>();

        BigDecimal tienGiaHan = tongTienGiaHan(d.getId());
        BigDecimal tongTienSan = d.getTongTien() != null ? d.getTongTien() : BigDecimal.ZERO;
        BigDecimal tienSanGoc = tongTienSan.subtract(tienGiaHan);
        if (tienSanGoc.compareTo(BigDecimal.ZERO) < 0) {
            tienSanGoc = BigDecimal.ZERO;
        }

        cacDong.add(new DongHoaDonPhanHoi(
                "TIEN_SAN",
                "Tiền sân " + d.getSanBong().getTenSan()
                        + " (" + d.getGioBatDau() + " - " + d.getGioKetThuc() + ")",
                1,
                tienSanGoc,
                tienSanGoc
        ));

        for (GiaHanGio gh : giaHanGioRepo.findByDatSanIdOrderByNgayGiaHanAsc(d.getId())) {
            cacDong.add(new DongHoaDonPhanHoi(
                    "GIA_HAN",
                    "Gia hạn giờ " + gh.getGioKetThucCu() + " → " + gh.getGioKetThucMoi(),
                    1,
                    gh.getSoTienThem(),
                    gh.getSoTienThem()
            ));
        }

        for (ChiTietDichVuPhanHoi dv : tinhTien.layChiTietDichVu(d.getId())) {
            cacDong.add(new DongHoaDonPhanHoi(
                    "DICH_VU",
                    dv.getTenSanPham(),
                    dv.getSoLuong(),
                    dv.getGia(),
                    dv.getThanhTien()
            ));
        }

        return cacDong;
    }

    // ── Helpers ───────────────────────────────────────────────────

    private List<DatSan> locDon(LocalDate tuNgay, LocalDate denNgay, boolean baoGomDaHuy) {
        List<DatSan> danhSach = datSanRepo.findAllByOrderByNgayDatDesc();

        if (!baoGomDaHuy) {
            danhSach = danhSach.stream()
                    .filter(d -> !"DA_HUY".equals(d.getTrangThai()))
                    .toList();
        }
        if (tuNgay != null) {
            danhSach = danhSach.stream().filter(d -> !d.getNgayDa().isBefore(tuNgay)).toList();
        }
        if (denNgay != null) {
            danhSach = danhSach.stream().filter(d -> !d.getNgayDa().isAfter(denNgay)).toList();
        }

        return danhSach;
    }

    private BigDecimal tongTienGiaHan(UUID datSanId) {
        return giaHanGioRepo.findByDatSanIdOrderByNgayGiaHanAsc(datSanId).stream()
                .map(GiaHanGio::getSoTienThem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String xacDinhTrangThaiThanhToan(BigDecimal tongCong, BigDecimal daThanhToan) {
        if (daThanhToan.compareTo(BigDecimal.ZERO) <= 0) {
            return CHUA_THANH_TOAN;
        }
        return daThanhToan.compareTo(tongCong) >= 0 ? DA_THANH_TOAN_DU : THANH_TOAN_MOT_PHAN;
    }

    /** Ngày xuất = lần thu tiền thành công gần nhất, chưa thu đồng nào thì lấy ngày đặt */
    private String xacDinhNgayXuat(DatSan d) {
        LocalDateTime moiNhat = thanhToanRepo.findByDatSanId(d.getId()).stream()
                .filter(tt -> "THANH_CONG".equals(tt.getTrangThai()))
                .map(ThanhToan::getNgayThanhToan)
                .filter(java.util.Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(d.getNgayDat());

        return moiNhat != null ? moiNhat.format(FMT) : "";
    }

    private long dem(List<HoaDonPhanHoi> danhSach, String trangThai) {
        return danhSach.stream().filter(hd -> trangThai.equals(hd.getTrangThaiThanhToan())).count();
    }

    private BigDecimal cong(List<HoaDonPhanHoi> danhSach,
                            java.util.function.Function<HoaDonPhanHoi, BigDecimal> lay) {
        return danhSach.stream().map(lay).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
