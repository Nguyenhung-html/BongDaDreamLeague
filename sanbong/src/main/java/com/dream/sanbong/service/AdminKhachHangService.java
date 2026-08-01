package com.dream.sanbong.service;

import com.dream.sanbong.dto.AdminKhachHangPhanHoi;
import com.dream.sanbong.dto.CapNhatKhachHangYeuCau;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Nghiệp vụ Admin quản lý thông tin khách hàng (tài khoản vai trò USER).
 * Admin chỉ được xem / sửa thông tin / khoá - mở tài khoản, KHÔNG được xoá
 * vì khách hàng còn gắn với lịch sử đặt sân và hoá đơn.
 */
@Service
public class AdminKhachHangService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NguoiDungRepository nguoiDungRepo;
    private final DatSanRepository datSanRepo;

    public AdminKhachHangService(NguoiDungRepository nguoiDungRepo, DatSanRepository datSanRepo) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.datSanRepo = datSanRepo;
    }

    /**
     * Danh sách khách hàng, lọc tuỳ chọn theo từ khoá (tên / email / SĐT)
     * và theo trạng thái tài khoản.
     *
     * @param tuKhoa    chuỗi tìm kiếm, có thể null
     * @param trangThai "HOAT_DONG" | "DA_KHOA" | null (lấy tất cả)
     */
    public List<AdminKhachHangPhanHoi> layDanhSach(String tuKhoa, String trangThai) {
        List<NguoiDung> danhSach = nguoiDungRepo.findByVaiTroOrderByNgayTaoDesc(VaiTro.USER);

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(nd -> chuaChuoi(nd.getHoTen(), tim)
                            || chuaChuoi(nd.getEmail(), tim)
                            || chuaChuoi(nd.getSoDienThoai(), tim))
                    .toList();
        }

        if (trangThai != null && !trangThai.isBlank()) {
            boolean mongMuon = "HOAT_DONG".equalsIgnoreCase(trangThai);
            danhSach = danhSach.stream()
                    .filter(nd -> nd.isTrangThai() == mongMuon)
                    .toList();
        }

        return danhSach.stream().map(this::sangDto).toList();
    }

    /** Chi tiết 1 khách hàng */
    public AdminKhachHangPhanHoi layChiTiet(UUID id) {
        return sangDto(timKhachHang(id));
    }

    /** Admin sửa thông tin khách hàng, có kiểm tra trùng email / số điện thoại */
    @Transactional
    public AdminKhachHangPhanHoi capNhat(UUID id, CapNhatKhachHangYeuCau yeuCau) {
        NguoiDung khach = timKhachHang(id);

        if (coGiaTri(yeuCau.getEmail()) && !yeuCau.getEmail().equals(khach.getEmail())) {
            if (nguoiDungRepo.existsByEmail(yeuCau.getEmail())) {
                throw new RuntimeException("Lỗi: Email này đã được tài khoản khác sử dụng!");
            }
            khach.setEmail(yeuCau.getEmail());
        }

        if (coGiaTri(yeuCau.getSoDienThoai()) && !yeuCau.getSoDienThoai().equals(khach.getSoDienThoai())) {
            if (nguoiDungRepo.existsBySoDienThoai(yeuCau.getSoDienThoai())) {
                throw new RuntimeException("Lỗi: Số điện thoại này đã được tài khoản khác sử dụng!");
            }
            khach.setSoDienThoai(yeuCau.getSoDienThoai());
        }

        if (coGiaTri(yeuCau.getHoTen())) {
            khach.setHoTen(yeuCau.getHoTen());
        }
        if (yeuCau.getDiaChi() != null) {
            khach.setDiaChi(yeuCau.getDiaChi());
        }

        return sangDto(nguoiDungRepo.save(khach));
    }

    /**
     * Khoá hoặc mở khoá tài khoản khách hàng.
     * Không cho khoá khi khách còn đơn đang hoạt động, vì họ sẽ không đăng nhập
     * được để theo dõi / huỷ đơn đã đặt.
     */
    @Transactional
    public AdminKhachHangPhanHoi doiTrangThai(UUID id, boolean kichHoat) {
        NguoiDung khach = timKhachHang(id);

        if (khach.isTrangThai() == kichHoat) {
            throw new RuntimeException(kichHoat
                    ? "Lỗi: Tài khoản này đang hoạt động rồi!"
                    : "Lỗi: Tài khoản này đã bị khoá trước đó rồi!");
        }

        if (!kichHoat) {
            long donDangHoatDong = datSanRepo.demDonDangHoatDong(khach.getId());
            if (donDangHoatDong > 0) {
                throw new RuntimeException("Lỗi: Khách hàng còn " + donDangHoatDong
                        + " đơn đặt sân chưa kết thúc. Hãy xử lý các đơn này trước khi khoá tài khoản!");
            }
        }

        khach.setTrangThai(kichHoat);
        return sangDto(nguoiDungRepo.save(khach));
    }

    // ── Helpers ───────────────────────────────────────────────────

    private NguoiDung timKhachHang(UUID id) {
        NguoiDung nd = nguoiDungRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy khách hàng!"));
        if (nd.getVaiTro() != VaiTro.USER) {
            throw new RuntimeException("Lỗi: Tài khoản này không phải khách hàng!");
        }
        return nd;
    }

    private AdminKhachHangPhanHoi sangDto(NguoiDung nd) {
        BigDecimal tongChiTieu = datSanRepo.tinhTongChiTieu(nd.getId());
        return AdminKhachHangPhanHoi.builder()
                .id(nd.getId())
                .hoTen(nd.getHoTen())
                .email(nd.getEmail())
                .soDienThoai(nd.getSoDienThoai())
                .diaChi(nd.getDiaChi())
                .trangThai(nd.isTrangThai())
                .ngayTao(nd.getNgayTao() != null ? nd.getNgayTao().format(FMT) : "")
                .soDonDaDat(datSanRepo.countByNguoiDungId(nd.getId()))
                .soDonDangHoatDong(datSanRepo.demDonDangHoatDong(nd.getId()))
                .tongChiTieu(tongChiTieu != null ? tongChiTieu : BigDecimal.ZERO)
                .build();
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
