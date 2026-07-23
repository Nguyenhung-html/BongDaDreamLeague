package com.dream.sanbong.service;

import com.dream.sanbong.dto.GiaHanGioPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.GiaHanGio;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.GiaHanGioRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class GiaHanGioServiceImpl implements GiaHanGioService {

    private static final LocalTime GIO_DONG_CUA = LocalTime.of(22, 0);
    private static final int PHUT_MOI_LAN_GIA_HAN = 30;

    private final DatSanRepository datSanRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final GiaHanGioRepository giaHanGioRepo;
    private final ThongBaoService thongBaoService; // MỚI THÊM

    public GiaHanGioServiceImpl(DatSanRepository datSanRepo, NguoiDungRepository nguoiDungRepo,
                                 GiaHanGioRepository giaHanGioRepo, ThongBaoService thongBaoService) {
        this.datSanRepo = datSanRepo;
        this.nguoiDungRepo = nguoiDungRepo;
        this.giaHanGioRepo = giaHanGioRepo;
        this.thongBaoService = thongBaoService;
    }

    // ===== Staff làm - CỘNG TIỀN THẬT + ĐỔI GIỜ THẬT =====
    @Override
    @Transactional
    public GiaHanGioPhanHoi giaHanThemGio(UUID datSanId, String email) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn đặt sân!"));

        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng!"));

        boolean laChuDon = datSan.getNguoiDung().getId().equals(nguoiDung.getId());
        boolean laNhanVien = "STAFF".equals(nguoiDung.getVaiTro().name())
                || "ADMIN".equals(nguoiDung.getVaiTro().name());
        if (!laChuDon && !laNhanVien) {
            throw new RuntimeException("Lỗi: Bạn không có quyền thao tác trên đơn đặt sân này!");
        }

        if ("DA_HUY".equals(datSan.getTrangThai()) || "HOAN_THANH".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Lỗi: Đơn đặt sân này đã kết thúc, không thể gia hạn thêm giờ!");
        }

        LocalTime gioKetThucCu = datSan.getGioKetThuc();
        LocalTime gioKetThucMoi = gioKetThucCu.plusMinutes(PHUT_MOI_LAN_GIA_HAN);

        if (gioKetThucMoi.isAfter(GIO_DONG_CUA)) {
            throw new RuntimeException("Lỗi: Sân chỉ hoạt động đến 22:00, không thể gia hạn thêm giờ!");
        }

        long soTrung = datSanRepo.demTrungLich(
                datSan.getSanBong().getId(), datSan.getNgayDa(), gioKetThucCu, gioKetThucMoi);
        if (soTrung > 0) {
            throw new RuntimeException("Lỗi: Khung giờ tiếp theo đã có người đặt, không thể gia hạn!");
        }

        // Tiền gia hạn mỗi lần = đúng bằng tiền cọc gốc của đơn
        BigDecimal soTienThem = datSan.getTienCoc();

        datSan.setGioKetThuc(gioKetThucMoi);
        datSan.setTongTien(datSan.getTongTien().add(soTienThem));
        datSanRepo.save(datSan);

        GiaHanGio log = GiaHanGio.builder()
                .datSan(datSan)
                .gioKetThucCu(gioKetThucCu)
                .gioKetThucMoi(gioKetThucMoi)
                .soTienThem(soTienThem)
                .build();
        giaHanGioRepo.save(log);

        return new GiaHanGioPhanHoi(
                gioKetThucMoi,
                soTienThem,
                datSan.getTongTien(),
                "Đã gia hạn thêm 30 phút (tới " + gioKetThucMoi + "), cộng thêm "
                        + soTienThem + "đ vào tổng cần thanh toán tại sân."
        );
    }

    // ===== MỚI THÊM: Khách bấm yêu cầu - CHỈ gửi thông báo, KHÔNG đổi giờ/tiền =====
    @Override
    @Transactional
    public void yeuCauGiaHan(UUID datSanId, String email) {
        DatSan datSan = datSanRepo.findById(datSanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn đặt sân!"));

        NguoiDung nguoiDung = nguoiDungRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng!"));

        boolean laChuDon = datSan.getNguoiDung().getId().equals(nguoiDung.getId());
        if (!laChuDon) {
            throw new RuntimeException("Lỗi: Bạn không phải chủ đơn đặt sân này!");
        }

        if ("DA_HUY".equals(datSan.getTrangThai()) || "HOAN_THANH".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Lỗi: Đơn đặt sân này đã kết thúc, không thể yêu cầu gia hạn!");
        }

        // Chỉ tạo thông báo cho Staff/Admin - không đụng gì tới DatSan cả
        thongBaoService.taoThongBaoYeuCauGiaHan(datSan);
    }
}