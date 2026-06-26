package com.dream.sanbong.service;

import com.dream.sanbong.dto.*;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.GiaSan;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.SanBong;
import com.dream.sanbong.entity.ThanhToan;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.GiaSanRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.SanBongRepository;
import com.dream.sanbong.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DatSanServiceImpl implements DatSanService {

    private final DatSanRepository datSanRepo;
    private final SanBongRepository sanBongRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final ThanhToanRepository thanhToanRepo;
    private final GiaSanRepository giaSanRepo;

    public DatSanServiceImpl(
            DatSanRepository datSanRepo,
            SanBongRepository sanBongRepo,
            NguoiDungRepository nguoiDungRepo,
            ThanhToanRepository thanhToanRepo,
            GiaSanRepository giaSanRepo) {
        this.datSanRepo = datSanRepo;
        this.sanBongRepo = sanBongRepo;
        this.nguoiDungRepo = nguoiDungRepo;
        this.thanhToanRepo = thanhToanRepo;
        this.giaSanRepo = giaSanRepo;
    }

    // ══════════════════════════════════════════════════════════════
    // USER FEATURES
    // ══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public DatSanPhanHoi datSan(DatSanYeuCau yeuCau, String emailNguoiDung) {

        // 1. Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(emailNguoiDung)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy tài khoản!"));

        // 2. Tìm sân
        SanBong sanBong = sanBongRepo.findById(yeuCau.getSanBongId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Sân không tồn tại!"));

        if (!"HOAT_DONG".equals(sanBong.getTrangThai())) {
            throw new RuntimeException("Lỗi: Sân này hiện không hoạt động!");
        }

        // 3. Parse ngày giờ
        LocalDate ngayDa = LocalDate.parse(yeuCau.getNgayDa());
        LocalTime gioBat = LocalTime.parse(yeuCau.getGioBatDau());
        LocalTime gioKet = LocalTime.parse(yeuCau.getGioKetThuc());

        // 4. Kiểm tra ngày đặt không được là thời gian đã qua
        LocalDateTime thoiGianDatSan = LocalDateTime.of(ngayDa, gioBat);
        if (thoiGianDatSan.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Lỗi: Không thể đặt sân vào thời gian đã qua!");
        }

        // 5. Kiểm tra trùng lịch trên sân
        boolean biBan = datSanRepo.demTrungLich(sanBong.getId(), ngayDa, gioBat, gioKet) > 0;
        if (biBan) {
            throw new RuntimeException("Lỗi: Khung giờ này đã có người đặt, vui lòng chọn khung giờ khác!");
        }

        // 6. Kiểm tra giới hạn 2 sân / người / ngày
        long soSanDaDat = datSanRepo.demSanDatTrongNgay(nguoiDung.getId(), ngayDa);
        if (soSanDaDat >= 2) {
            throw new RuntimeException("Lỗi: Bạn đã đặt tối đa 2 sân trong ngày " + ngayDa
                    + ". Vui lòng liên hệ chủ sân nếu cần thêm!");
        }

        // 7. Tính tổng tiền theo bảng giá (cộng từng slot 1 giờ nằm trong khoảng đặt)
        BigDecimal tongTien = tinhTongTien(sanBong.getId(), gioBat, gioKet);
        BigDecimal tienCoc = tongTien.divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_UP);

        // 8. Tạo bản ghi DAT_SAN
        DatSan datSan = DatSan.builder()
                .nguoiDung(nguoiDung)
                .sanBong(sanBong)
                .hoTenDat(yeuCau.getHoTenDat())
                .soDienThoai(yeuCau.getSoDienThoai())
                .ngayDa(ngayDa)
                .gioBatDau(gioBat)
                .gioKetThuc(gioKet)
                .tongTien(tongTien)
                .tienCoc(tienCoc)
                .trangThai("CHO_XAC_NHAN")
                .build();
        datSanRepo.save(datSan);

        // 9. Tạo bản ghi THANH_TOAN (đặt cọc 50%)
        ThanhToan thanhToan = ThanhToan.builder()
                .datSan(datSan)
                .soTien(tienCoc)
                .loai("COC")
                .phuongThuc(yeuCau.getPhuongThuc() != null ? yeuCau.getPhuongThuc() : "QR")
                .trangThai("DANG_CHO")
                .build();
        thanhToanRepo.save(thanhToan);

        return new DatSanPhanHoi(
                datSan.getId(),
                sanBong.getTenSan(),
                ngayDa.toString(),
                gioBat.toString(),
                gioKet.toString(),
                tongTien,
                tienCoc,
                "CHO_XAC_NHAN",
                "Đặt sân thành công! Vui lòng thanh toán cọc "
                        + String.format("%,.0f", tienCoc.doubleValue())
                        + " VNĐ để xác nhận booking."
        );
    }

    /**
     * Tính tổng tiền bằng cách cộng từng slot 1 giờ trong bảng GIA_SAN
     * nằm trong khoảng [gioBat, gioKet).
     */
    private BigDecimal tinhTongTien(UUID sanBongId, LocalTime gioBat, LocalTime gioKet) {
        List<GiaSan> danhSachGia = giaSanRepo.findBySanBongIdOrderByGioBatDauAsc(sanBongId);
        BigDecimal tong = BigDecimal.ZERO;
        for (GiaSan gs : danhSachGia) {
            if (!gs.getGioBatDau().isBefore(gioKet) || !gs.getGioKetThuc().isAfter(gioBat)) {
                continue;
            }
            tong = tong.add(gs.getGiaTien());
        }
        if (tong.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("Lỗi: Không tìm thấy bảng giá cho khung giờ này!");
        }
        return tong;
    }

    @Override
    public List<LichSuDatSanPhanHoi> layLichSu(String emailNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(emailNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        return datSanRepo.findByNguoiDungIdOrderByNgayDatDesc(nguoiDung.getId())
                .stream()
                .map(d -> {
                    LocalDateTime thoiGianBatDau = LocalDateTime.of(d.getNgayDa(), d.getGioBatDau());
                    boolean coTheHuy = !"DA_HUY".equals(d.getTrangThai())
                            && !"HOAN_THANH".equals(d.getTrangThai())
                            && LocalDateTime.now().plusMinutes(20).isBefore(thoiGianBatDau);

                    return new LichSuDatSanPhanHoi(
                            d.getId(),
                            d.getSanBong().getTenSan(),
                            d.getSanBong().getLoaiSan(),
                            d.getNgayDa().toString(),
                            d.getGioBatDau().toString(),
                            d.getGioKetThuc().toString(),
                            d.getTongTien(),
                            d.getTienCoc(),
                            d.getTrangThai(),
                            d.getNgayDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            coTheHuy
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public String huySan(UUID datSanId, String emailNguoiDung) {
        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(emailNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân!"));

        if (!datSan.getNguoiDung().getId().equals(nguoiDung.getId())) {
            throw new RuntimeException("Lỗi: Bạn không có quyền huỷ đơn đặt sân này!");
        }

        if ("DA_HUY".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Lỗi: Đơn đặt sân này đã bị huỷ rồi!");
        }

        LocalDateTime gioBatDauThucTe = LocalDateTime.of(datSan.getNgayDa(), datSan.getGioBatDau());
        boolean conHuyCo = LocalDateTime.now().plusMinutes(20).isBefore(gioBatDauThucTe);

        String thongBao;
        if (conHuyCo) {
            thongBao = "Huỷ sân thành công! Tiền cọc sẽ được hoàn trả trong 1–3 ngày làm việc.";
        } else {
            thongBao = "Huỷ sân thành công! Lưu ý: vì huỷ muộn (< 20 phút trước giờ đá), "
                    + "tiền cọc " + String.format("%,.0f", datSan.getTienCoc().doubleValue())
                    + " VNĐ sẽ không được hoàn lại theo chính sách.";
        }

        datSan.setTrangThai("DA_HUY");
        datSan.setNgayHuy(LocalDateTime.now());
        datSanRepo.save(datSan);

        return thongBao;
    }

    // ══════════════════════════════════════════════════════════════
    // STAFF FEATURES
    // ══════════════════════════════════════════════════════════════

    @Override
    public List<StaffDatSanPhanHoi> layTatCaDon(String trangThai, String ngay) {
        List<DatSan> danhSach;

        if (ngay != null && !ngay.isBlank()) {
            LocalDate localDate = LocalDate.parse(ngay);
            danhSach = datSanRepo.findByNgayDaOrderByGioBatDauAsc(localDate);
        } else {
            danhSach = datSanRepo.findAllByOrderByNgayDatDesc();
        }

        // Lọc theo trạng thái nếu có
        if (trangThai != null && !trangThai.isBlank()) {
            danhSach = danhSach.stream()
                    .filter(d -> trangThai.equals(d.getTrangThai()))
                    .collect(Collectors.toList());
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return danhSach.stream()
                .map(d -> new StaffDatSanPhanHoi(
                        d.getId(),
                        d.getId().toString().substring(0, 8).toUpperCase(),
                        d.getSanBong().getTenSan(),
                        d.getSanBong().getLoaiSan(),
                        d.getHoTenDat(),
                        d.getSoDienThoai(),
                        d.getNgayDa().toString(),
                        d.getGioBatDau().toString(),
                        d.getGioKetThuc().toString(),
                        d.getTongTien(),
                        d.getTienCoc(),
                        d.getTrangThai(),
                        d.getNgayDat().format(fmt)
                ))
                .toList();
    }

    @Override
    @Transactional
    public String xacNhanDon(UUID datSanId, String emailStaff) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân!"));

        if (!"CHO_XAC_NHAN".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể xác nhận đơn đang ở trạng thái Chờ xác nhận!");
        }

        // Cập nhật trạng thái đơn
        datSan.setTrangThai("DA_COC");
        datSanRepo.save(datSan);

        // Cập nhật trạng thái thanh toán cọc → THANH_CONG
        thanhToanRepo.findByDatSanIdAndLoai(datSanId, "COC").ifPresent(tt -> {
            tt.setTrangThai("THANH_CONG");
            thanhToanRepo.save(tt);
        });

        return "Xác nhận đơn thành công! Đơn đặt sân đã chuyển sang trạng thái Đã cọc.";
    }

    @Override
    @Transactional
    public String staffHuyDon(UUID datSanId, String lyDo, String emailStaff) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân!"));

        if ("DA_HUY".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Đơn này đã bị huỷ rồi!");
        }
        if ("HOAN_THANH".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Không thể huỷ đơn đã hoàn thành!");
        }

        datSan.setTrangThai("DA_HUY");
        datSan.setNgayHuy(LocalDateTime.now());
        datSanRepo.save(datSan);

        return "Đã huỷ đơn thành công. Lý do: " + (lyDo != null ? lyDo : "Không có lý do");
    }

    @Override
    @Transactional
    public String hoanThanhDon(UUID datSanId, String emailStaff) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân!"));

        if (!"DA_COC".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hoàn thành đơn đang ở trạng thái Đã cọc!");
        }

        datSan.setTrangThai("HOAN_THANH");
        datSanRepo.save(datSan);

        return "Đã đánh dấu đơn hoàn thành!";
    }
}
