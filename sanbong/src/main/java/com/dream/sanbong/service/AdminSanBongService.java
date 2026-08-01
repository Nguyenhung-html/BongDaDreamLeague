package com.dream.sanbong.service;

import com.dream.sanbong.dto.AdminSanBongPhanHoi;
import com.dream.sanbong.dto.GiaSanPhanHoi;
import com.dream.sanbong.dto.GiaSanYeuCau;
import com.dream.sanbong.dto.SanBongYeuCau;
import com.dream.sanbong.entity.GiaSan;
import com.dream.sanbong.entity.SanBong;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.GiaSanRepository;
import com.dream.sanbong.repository.SanBongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Nghiệp vụ Admin quản lý sân bóng và bảng giá theo khung giờ.
 */
@Service
public class AdminSanBongService {

    private static final Set<String> TRANG_THAI_HOP_LE = Set.of("HOAT_DONG", "BAO_TRI", "DONG_CUA");
    private static final Set<Integer> LOAI_SAN_HOP_LE = Set.of(5, 7);

    private final SanBongRepository sanBongRepo;
    private final GiaSanRepository giaSanRepo;
    private final DatSanRepository datSanRepo;

    public AdminSanBongService(SanBongRepository sanBongRepo,
                               GiaSanRepository giaSanRepo,
                               DatSanRepository datSanRepo) {
        this.sanBongRepo = sanBongRepo;
        this.giaSanRepo = giaSanRepo;
        this.datSanRepo = datSanRepo;
    }

    // ══════════════════════════════════════════════════════════════
    // SÂN BÓNG
    // ══════════════════════════════════════════════════════════════

    /**
     * Danh sách toàn bộ sân (kể cả sân đang bảo trì / đã đóng cửa,
     * khác với API công khai chỉ trả sân HOAT_DONG).
     */
    public List<AdminSanBongPhanHoi> layDanhSach(String tuKhoa, Integer loaiSan, String trangThai) {
        List<SanBong> danhSach = sanBongRepo.findAll().stream()
                .sorted(Comparator.comparing(SanBong::getTenSan, Comparator.nullsLast(String::compareTo)))
                .toList();

        if (loaiSan != null) {
            danhSach = danhSach.stream().filter(s -> loaiSan.equals(s.getLoaiSan())).toList();
        }
        if (trangThai != null && !trangThai.isBlank()) {
            danhSach = danhSach.stream().filter(s -> trangThai.equalsIgnoreCase(s.getTrangThai())).toList();
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String tim = tuKhoa.trim().toLowerCase();
            danhSach = danhSach.stream()
                    .filter(s -> chuaChuoi(s.getTenSan(), tim) || chuaChuoi(s.getDiaChi(), tim))
                    .toList();
        }

        return danhSach.stream().map(this::sangDto).toList();
    }

    public AdminSanBongPhanHoi layChiTiet(UUID id) {
        return sangDto(timSan(id));
    }

    /** Thêm sân mới. Tên sân, loại sân và địa chỉ là bắt buộc. */
    @Transactional
    public AdminSanBongPhanHoi them(SanBongYeuCau yeuCau) {
        if (!coGiaTri(yeuCau.getTenSan())) {
            throw new RuntimeException("Lỗi: Vui lòng nhập tên sân!");
        }
        if (!coGiaTri(yeuCau.getDiaChi())) {
            throw new RuntimeException("Lỗi: Vui lòng nhập địa chỉ sân!");
        }
        kiemTraLoaiSan(yeuCau.getLoaiSan());

        String trangThai = coGiaTri(yeuCau.getTrangThai())
                ? chuanHoaTrangThai(yeuCau.getTrangThai())
                : "HOAT_DONG";

        SanBong san = SanBong.builder()
                .tenSan(yeuCau.getTenSan())
                .loaiSan(yeuCau.getLoaiSan())
                .moTa(yeuCau.getMoTa())
                .diaChi(yeuCau.getDiaChi())
                .viDo(yeuCau.getViDo())
                .kinhDo(yeuCau.getKinhDo())
                .hinhAnh(yeuCau.getHinhAnh())
                .trangThai(trangThai)
                .build();

        return sangDto(sanBongRepo.save(san));
    }

    /** Sửa thông tin sân, trường nào null thì giữ nguyên */
    @Transactional
    public AdminSanBongPhanHoi capNhat(UUID id, SanBongYeuCau yeuCau) {
        SanBong san = timSan(id);

        if (coGiaTri(yeuCau.getTenSan())) {
            san.setTenSan(yeuCau.getTenSan());
        }
        if (yeuCau.getLoaiSan() != null) {
            kiemTraLoaiSan(yeuCau.getLoaiSan());
            san.setLoaiSan(yeuCau.getLoaiSan());
        }
        if (yeuCau.getMoTa() != null) {
            san.setMoTa(yeuCau.getMoTa());
        }
        if (coGiaTri(yeuCau.getDiaChi())) {
            san.setDiaChi(yeuCau.getDiaChi());
        }
        if (yeuCau.getViDo() != null) {
            san.setViDo(yeuCau.getViDo());
        }
        if (yeuCau.getKinhDo() != null) {
            san.setKinhDo(yeuCau.getKinhDo());
        }
        if (yeuCau.getHinhAnh() != null) {
            san.setHinhAnh(yeuCau.getHinhAnh());
        }
        if (coGiaTri(yeuCau.getTrangThai())) {
            san.setTrangThai(chuanHoaTrangThai(yeuCau.getTrangThai()));
        }

        return sangDto(sanBongRepo.save(san));
    }

    /** Đổi riêng trạng thái sân (mở / bảo trì / đóng cửa) */
    @Transactional
    public AdminSanBongPhanHoi doiTrangThai(UUID id, String trangThai) {
        SanBong san = timSan(id);
        san.setTrangThai(chuanHoaTrangThai(trangThai));
        return sangDto(sanBongRepo.save(san));
    }

    /**
     * Xoá sân. Chỉ cho phép khi sân chưa từng có đơn đặt nào, vì DAT_SAN
     * có khoá ngoại trỏ tới SAN_BONG. Sân đã có lịch sử thì nên chuyển
     * trạng thái sang DONG_CUA thay vì xoá.
     */
    @Transactional
    public String xoa(UUID id) {
        SanBong san = timSan(id);

        long soDon = datSanRepo.countBySanBongId(san.getId());
        if (soDon > 0) {
            throw new RuntimeException("Lỗi: Sân này đã có " + soDon
                    + " đơn đặt nên không thể xoá. Hãy chuyển trạng thái sang Đóng cửa!");
        }

        // Bảng giá phải dọn trước vì GIA_SAN có khoá ngoại trỏ tới SAN_BONG
        giaSanRepo.deleteBySanBongId(san.getId());
        sanBongRepo.delete(san);
        return "Đã xoá sân " + san.getTenSan() + "!";
    }

    // ══════════════════════════════════════════════════════════════
    // BẢNG GIÁ THEO KHUNG GIỜ
    // ══════════════════════════════════════════════════════════════

    public List<GiaSanPhanHoi> layBangGia(UUID sanBongId) {
        timSan(sanBongId); // xác nhận sân tồn tại
        return giaSanRepo.findBySanBongIdOrderByGioBatDauAsc(sanBongId).stream()
                .map(this::sangDtoGia)
                .toList();
    }

    /** Thêm 1 khung giờ vào bảng giá, chặn trùng lặp với khung giờ đã có */
    @Transactional
    public GiaSanPhanHoi themKhungGio(UUID sanBongId, GiaSanYeuCau yeuCau) {
        SanBong san = timSan(sanBongId);

        LocalTime gioBat = parseGio(yeuCau.getGioBatDau(), "giờ bắt đầu");
        LocalTime gioKet = parseGio(yeuCau.getGioKetThuc(), "giờ kết thúc");
        kiemTraKhoangGio(gioBat, gioKet);
        kiemTraGiaTien(yeuCau.getGiaTien());
        kiemTraTrungKhungGio(sanBongId, gioBat, gioKet, null);

        GiaSan gia = GiaSan.builder()
                .sanBong(san)
                .gioBatDau(gioBat)
                .gioKetThuc(gioKet)
                .giaTien(yeuCau.getGiaTien())
                .build();

        return sangDtoGia(giaSanRepo.save(gia));
    }

    /** Sửa 1 khung giờ đã có */
    @Transactional
    public GiaSanPhanHoi suaKhungGio(UUID giaSanId, GiaSanYeuCau yeuCau) {
        GiaSan gia = giaSanRepo.findById(giaSanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy khung giờ trong bảng giá!"));

        LocalTime gioBat = coGiaTri(yeuCau.getGioBatDau())
                ? parseGio(yeuCau.getGioBatDau(), "giờ bắt đầu")
                : gia.getGioBatDau();
        LocalTime gioKet = coGiaTri(yeuCau.getGioKetThuc())
                ? parseGio(yeuCau.getGioKetThuc(), "giờ kết thúc")
                : gia.getGioKetThuc();
        kiemTraKhoangGio(gioBat, gioKet);
        kiemTraTrungKhungGio(gia.getSanBong().getId(), gioBat, gioKet, gia.getId());

        if (yeuCau.getGiaTien() != null) {
            kiemTraGiaTien(yeuCau.getGiaTien());
            gia.setGiaTien(yeuCau.getGiaTien());
        }
        gia.setGioBatDau(gioBat);
        gia.setGioKetThuc(gioKet);

        return sangDtoGia(giaSanRepo.save(gia));
    }

    @Transactional
    public String xoaKhungGio(UUID giaSanId) {
        GiaSan gia = giaSanRepo.findById(giaSanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy khung giờ trong bảng giá!"));
        giaSanRepo.delete(gia);
        return "Đã xoá khung giờ " + gia.getGioBatDau() + " - " + gia.getGioKetThuc() + "!";
    }

    // ── Helpers ───────────────────────────────────────────────────

    private SanBong timSan(UUID id) {
        return sanBongRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sân bóng!"));
    }

    private AdminSanBongPhanHoi sangDto(SanBong san) {
        List<GiaSanPhanHoi> bangGia = giaSanRepo.findBySanBongIdOrderByGioBatDauAsc(san.getId())
                .stream()
                .map(this::sangDtoGia)
                .toList();

        return AdminSanBongPhanHoi.builder()
                .id(san.getId())
                .tenSan(san.getTenSan())
                .loaiSan(san.getLoaiSan())
                .moTa(san.getMoTa())
                .diaChi(san.getDiaChi())
                .viDo(san.getViDo())
                .kinhDo(san.getKinhDo())
                .hinhAnh(san.getHinhAnh())
                .trangThai(san.getTrangThai())
                .soKhungGio(bangGia.size())
                .soDonDaDat(datSanRepo.countBySanBongId(san.getId()))
                .danhSachGia(bangGia)
                .build();
    }

    private GiaSanPhanHoi sangDtoGia(GiaSan gia) {
        return new GiaSanPhanHoi(
                gia.getId(),
                gia.getGioBatDau().toString(),
                gia.getGioKetThuc().toString(),
                gia.getGiaTien()
        );
    }

    private LocalTime parseGio(String gio, String ten) {
        if (!coGiaTri(gio)) {
            throw new RuntimeException("Lỗi: Vui lòng nhập " + ten + "!");
        }
        try {
            return LocalTime.parse(gio.trim());
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Lỗi: " + ten + " không hợp lệ, cần đúng dạng HH:mm (ví dụ 18:00)!");
        }
    }

    private void kiemTraKhoangGio(LocalTime gioBat, LocalTime gioKet) {
        if (!gioBat.isBefore(gioKet)) {
            throw new RuntimeException("Lỗi: Giờ bắt đầu phải nhỏ hơn giờ kết thúc!");
        }
    }

    private void kiemTraGiaTien(BigDecimal giaTien) {
        if (giaTien == null || giaTien.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Lỗi: Giá tiền phải lớn hơn 0!");
        }
    }

    /** Chặn hai khung giờ chồng lên nhau trong cùng một sân */
    private void kiemTraTrungKhungGio(UUID sanBongId, LocalTime gioBat, LocalTime gioKet, UUID boQuaId) {
        boolean bitrung = giaSanRepo
                .timKhungGioTrung(sanBongId, gioBat, gioKet)
                .stream()
                .anyMatch(g -> boQuaId == null || !g.getId().equals(boQuaId));

        if (bitrung) {
            throw new RuntimeException("Lỗi: Khung giờ " + gioBat + " - " + gioKet
                    + " bị trùng với một khung giờ đã có trong bảng giá!");
        }
    }

    private void kiemTraLoaiSan(Integer loaiSan) {
        if (loaiSan == null || !LOAI_SAN_HOP_LE.contains(loaiSan)) {
            throw new RuntimeException("Lỗi: Loại sân chỉ có thể là 5 hoặc 7 người!");
        }
    }

    private String chuanHoaTrangThai(String trangThai) {
        String kq = trangThai == null ? "" : trangThai.trim().toUpperCase();
        if (!TRANG_THAI_HOP_LE.contains(kq)) {
            throw new RuntimeException("Lỗi: Trạng thái sân chỉ có thể là HOAT_DONG, BAO_TRI hoặc DONG_CUA!");
        }
        return kq;
    }

    private boolean coGiaTri(String s) {
        return s != null && !s.isBlank();
    }

    private boolean chuaChuoi(String nguon, String tim) {
        return nguon != null && nguon.toLowerCase().contains(tim);
    }
}
