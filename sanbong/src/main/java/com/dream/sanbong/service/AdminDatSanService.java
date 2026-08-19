package com.dream.sanbong.service;

import com.dream.sanbong.dto.AdminDatSanChiTietPhanHoi;
import com.dream.sanbong.dto.AdminDatSanPhanHoi;
import com.dream.sanbong.dto.AdminDatSanThongKePhanHoi;
import com.dream.sanbong.dto.LichSuGiaHanPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.GiaHanGioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ Admin quản lý toàn bộ đơn đặt sân của hệ thống.
 * Các thao tác đổi trạng thái (xác nhận / huỷ / hoàn thành) dùng lại
 * DatSanService để quy tắc nghiệp vụ luôn khớp với luồng Staff.
 */
@Service
public class AdminDatSanService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DatSanRepository datSanRepo;
    private final GiaHanGioRepository giaHanGioRepo;
    private final DatSanService datSanService;
    private final AdminThanhToanService adminThanhToanService;
    private final TinhTienDonHelper tinhTien;

    public AdminDatSanService(DatSanRepository datSanRepo,
                              GiaHanGioRepository giaHanGioRepo,
                              DatSanService datSanService,
                              AdminThanhToanService adminThanhToanService,
                              TinhTienDonHelper tinhTien) {
        this.datSanRepo = datSanRepo;
        this.giaHanGioRepo = giaHanGioRepo;
        this.datSanService = datSanService;
        this.adminThanhToanService = adminThanhToanService;
        this.tinhTien = tinhTien;
    }

    /**
     * Danh sách đơn đặt sân toàn hệ thống với bộ lọc đầy đủ.
     *
     * @param trangThai CHO_XAC_NHAN | DA_COC | HOAN_THANH | DA_HUY
     * @param tuKhoa    tìm theo tên khách / SĐT / tên sân / mã đơn
     */
    public List<AdminDatSanPhanHoi> layDanhSach(String trangThai, LocalDate tuNgay, LocalDate denNgay,
                                                UUID sanBongId, String tuKhoa) {
        return loc(trangThai, tuNgay, denNgay, sanBongId, tuKhoa).stream()
                .map(this::sangDto)
                .toList();
    }

    /** Chi tiết đơn: thông tin đơn + dịch vụ + lịch sử gia hạn + các giao dịch */
    public AdminDatSanChiTietPhanHoi layChiTiet(UUID id) {
        DatSan datSan = timDon(id);

        List<LichSuGiaHanPhanHoi> danhSachGiaHan = giaHanGioRepo
                .findByDatSanIdOrderByNgayGiaHanAsc(id)
                .stream()
                .map(gh -> new LichSuGiaHanPhanHoi(
                        gh.getId(),
                        gh.getGioKetThucCu().toString(),
                        gh.getGioKetThucMoi().toString(),
                        gh.getSoTienThem(),
                        gh.getNgayGiaHan() != null ? gh.getNgayGiaHan().format(FMT) : ""
                ))
                .toList();

        return AdminDatSanChiTietPhanHoi.builder()
                .thongTinDon(sangDto(datSan))
                .danhSachDichVu(tinhTien.layChiTietDichVu(id))
                .tongTienDichVu(tinhTien.tongTienDichVu(id))
                .danhSachGiaHan(danhSachGiaHan)
                .danhSachThanhToan(adminThanhToanService.layTheoDon(id))
                .tongPhaiThu(tinhTien.tongPhaiThu(datSan))
                .daThanhToan(tinhTien.daThanhToan(id))
                .conLai(tinhTien.conLai(datSan))
                .build();
    }

    /** Số liệu tổng hợp theo cùng bộ lọc khoảng ngày */
    public AdminDatSanThongKePhanHoi layThongKe(LocalDate tuNgay, LocalDate denNgay) {
        List<DatSan> danhSach = loc(null, tuNgay, denNgay, null, null);

        return AdminDatSanThongKePhanHoi.builder()
                .tongDon(danhSach.size())
                .choXacNhan(dem(danhSach, "CHO_XAC_NHAN"))
                .daCoc(dem(danhSach, "DA_COC"))
                .hoanThanh(dem(danhSach, "HOAN_THANH"))
                .daHuy(dem(danhSach, "DA_HUY"))
                .doanhThuHoanThanh(tongTien(danhSach, "HOAN_THANH"))
                .tienCocDangGiu(danhSach.stream()
                        .filter(d -> "DA_COC".equals(d.getTrangThai()))
                        .map(d -> d.getTienCoc() != null ? d.getTienCoc() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }

    // ── Thao tác đổi trạng thái (uỷ quyền cho DatSanService) ──────

    public String xacNhan(UUID id, String emailAdmin) {
        return datSanService.xacNhanDon(id, emailAdmin);
    }

    public String huy(UUID id, String lyDo, String emailAdmin) {
        return datSanService.staffHuyDon(id, lyDo, emailAdmin);
    }

    public String hoanThanh(UUID id, String emailAdmin) {
        return datSanService.hoanThanhDon(id, emailAdmin);
    }

    // ── Helpers ───────────────────────────────────────────────────

    private List<DatSan> loc(String trangThai, LocalDate tuNgay, LocalDate denNgay,
                             UUID sanBongId, String tuKhoa) {
        List<DatSan> danhSach = datSanRepo.findAllByOrderByNgayDatDesc();

        if (tuNgay != null) {
            danhSach = danhSach.stream().filter(d -> !d.getNgayDa().isBefore(tuNgay)).toList();
        }
        if (denNgay != null) {
            danhSach = danhSach.stream().filter(d -> !d.getNgayDa().isAfter(denNgay)).toList();
        }
        if (coGiaTri(trangThai)) {
            danhSach = danhSach.stream()
                    .filter(d -> trangThai.equalsIgnoreCase(d.getTrangThai()))
                    .toList();
        }
        if (sanBongId != null) {
            danhSach = danhSach.stream()
                    .filter(d -> sanBongId.equals(d.getSanBong().getId()))
                    .toList();
        }
        if (coGiaTri(tuKhoa)) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(d -> chuaChuoi(d.getHoTenDat(), tim)
                            || chuaChuoi(d.getSoDienThoai(), tim)
                            || chuaChuoi(d.getSanBong().getTenSan(), tim)
                            || chuaChuoi(maDon(d), tim))
                    .toList();
        }

        return danhSach;
    }

    private AdminDatSanPhanHoi sangDto(DatSan d) {
        return AdminDatSanPhanHoi.builder()
                .id(d.getId())
                .maDon(maDon(d))
                .sanBongId(d.getSanBong().getId())
                .tenSan(d.getSanBong().getTenSan())
                .loaiSan(d.getSanBong().getLoaiSan())
                .hoTenDat(d.getHoTenDat())
                .soDienThoai(d.getSoDienThoai())
                // Đơn khách vãng lai do Staff đặt hộ có thể không gắn tài khoản nào
                .emailTaiKhoan(d.getNguoiDung() != null ? d.getNguoiDung().getEmail() : "")
                .ngayDa(d.getNgayDa().toString())
                .gioBatDau(d.getGioBatDau().toString())
                .gioKetThuc(d.getGioKetThuc().toString())
                .tongTien(d.getTongTien())
                .tienCoc(d.getTienCoc())
                .daThanhToan(tinhTien.daThanhToan(d.getId()))
                .conLai(tinhTien.conLai(d))
                .trangThai(d.getTrangThai())
                .ngayDat(d.getNgayDat() != null ? d.getNgayDat().format(FMT) : "")
                .ngayHuy(d.getNgayHuy() != null ? d.getNgayHuy().format(FMT) : "")
                .build();
    }

    private DatSan timDon(UUID id) {
        return datSanRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn đặt sân!"));
    }

    private String maDon(DatSan d) {
        return d.getId().toString().substring(0, 8).toUpperCase();
    }

    private long dem(List<DatSan> danhSach, String trangThai) {
        return danhSach.stream().filter(d -> trangThai.equals(d.getTrangThai())).count();
    }

    private BigDecimal tongTien(List<DatSan> danhSach, String trangThai) {
        return danhSach.stream()
                .filter(d -> trangThai.equals(d.getTrangThai()))
                .map(d -> d.getTongTien() != null ? d.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
