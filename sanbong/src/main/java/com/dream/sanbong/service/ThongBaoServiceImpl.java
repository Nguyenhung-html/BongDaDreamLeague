package com.dream.sanbong.service;

import com.dream.sanbong.dto.ThongBaoPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.ThongBao;
import com.dream.sanbong.entity.VaiTro;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.ThongBaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ThongBaoServiceImpl implements ThongBaoService {

    private final ThongBaoRepository thongBaoRepo;
    private final NguoiDungRepository nguoiDungRepo;

    public ThongBaoServiceImpl(ThongBaoRepository thongBaoRepo, NguoiDungRepository nguoiDungRepo) {
        this.thongBaoRepo = thongBaoRepo;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    @Override
    public List<ThongBaoPhanHoi> layDanhSach(UUID nguoiDungId) {
        return thongBaoRepo.findByNguoiDungIdOrderByNgayTaoDesc(nguoiDungId)
                .stream()
                .map(tb -> new ThongBaoPhanHoi(
                        tb.getId(), tb.getTieuDe(), tb.getNoiDung(),
                        tb.getLoai(), tb.isDaDoc(), tb.getNgayTao()))
                .collect(Collectors.toList());
    }

    @Override
    public long demChuaDoc(UUID nguoiDungId) {
        return thongBaoRepo.countByNguoiDungIdAndDaDocFalse(nguoiDungId);
    }

    @Override
    @Transactional
    public void danhDauDaDoc(UUID thongBaoId) {
        thongBaoRepo.findById(thongBaoId).ifPresent(tb -> {
            tb.setDaDoc(true);
            thongBaoRepo.save(tb);
        });
    }

    @Override
    @Transactional
    public void danhDauTatCaDaDoc(UUID nguoiDungId) {
        List<ThongBao> chuaDoc = thongBaoRepo.findByNguoiDungIdAndDaDocFalse(nguoiDungId);
        chuaDoc.forEach(tb -> tb.setDaDoc(true));
        thongBaoRepo.saveAll(chuaDoc);
    }

    @Override
    @Transactional
    public void taoThongBaoThanhToanThanhCong(DatSan datSan) {
        NguoiDung khachHang = datSan.getNguoiDung();
        String tenSan = datSan.getSanBong().getTenSan();
        String khungGio = datSan.getGioBatDau() + " - " + datSan.getGioKetThuc();

        // 1. Thông báo cho khách hàng vừa đặt sân
        ThongBao tbKhachHang = ThongBao.builder()
                .nguoiDung(khachHang)
                .tieuDe("Thanh toán thành công 🎉")
                .noiDung("Bạn đã đặt cọc thành công cho " + tenSan + " lúc " + khungGio
                        + " ngày " + datSan.getNgayDa() + ". Booking đã được xác nhận tự động.")
                .loai("booking")
                .build();
        thongBaoRepo.save(tbKhachHang);

        // 2. Thông báo cho toàn bộ Staff/Admin để biết có đơn mới cần chuẩn bị sân
        List<NguoiDung> nhanVien = nguoiDungRepo.findByVaiTroIn(List.of(VaiTro.STAFF, VaiTro.ADMIN));
        for (NguoiDung nv : nhanVien) {
            ThongBao tbNhanVien = ThongBao.builder()
                    .nguoiDung(nv)
                    .tieuDe("Có đơn đặt sân mới thanh toán 💳")
                    .noiDung(khachHang.getHoTen() + " vừa đặt cọc " + tenSan + " lúc " + khungGio
                            + " ngày " + datSan.getNgayDa() + ".")
                    .loai("booking")
                    .build();
            thongBaoRepo.save(tbNhanVien);
        }
    }

    @Override
    @Transactional
    public void taoThongBaoYeuCauGiaHan(DatSan datSan) {
        NguoiDung khachHang = datSan.getNguoiDung();
        String tenSan = datSan.getSanBong().getTenSan();
        String khungGio = datSan.getGioBatDau() + " - " + datSan.getGioKetThuc();

        // Chỉ thông báo cho Staff/Admin - khách đã biết mình vừa gửi yêu cầu rồi,
        // không cần thông báo lại cho chính họ.
        List<NguoiDung> nhanVien = nguoiDungRepo.findByVaiTroIn(List.of(VaiTro.STAFF, VaiTro.ADMIN));
        for (NguoiDung nv : nhanVien) {
            ThongBao tb = ThongBao.builder()
                    .nguoiDung(nv)
                    .tieuDe("Khách yêu cầu gia hạn thêm giờ ⏱️")
                    .noiDung(khachHang.getHoTen() + " muốn gia hạn thêm 30 phút cho " + tenSan
                            + " (hiện đang " + khungGio + ") ngày " + datSan.getNgayDa()
                            + ". Vui lòng vào Quản lý đặt sân để xác nhận gia hạn nếu đồng ý.")
                    .loai("booking")
                    .build();
            thongBaoRepo.save(tb);
        }
    }
}