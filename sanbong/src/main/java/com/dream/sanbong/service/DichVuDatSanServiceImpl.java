package com.dream.sanbong.service;

import com.dream.sanbong.dto.ChiTietDichVuPhanHoi;
import com.dream.sanbong.dto.GioHangPhanHoi;
import com.dream.sanbong.dto.ThemDichVuYeuCau;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.DichVuDatSan;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.SanPham;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.DichVuDatSanRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.SanPhamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DichVuDatSanServiceImpl implements DichVuDatSanService {

    private final DichVuDatSanRepository dichVuRepo;
    private final SanPhamRepository sanPhamRepo;
    private final DatSanRepository datSanRepo;
    private final NguoiDungRepository nguoiDungRepo;

    public DichVuDatSanServiceImpl(DichVuDatSanRepository dichVuRepo, SanPhamRepository sanPhamRepo,
                                    DatSanRepository datSanRepo, NguoiDungRepository nguoiDungRepo) {
        this.dichVuRepo = dichVuRepo;
        this.sanPhamRepo = sanPhamRepo;
        this.datSanRepo = datSanRepo;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    // Kiểm tra người đang gọi API có quyền thao tác trên đơn đặt sân này không:
    // là chủ đơn, hoặc là Staff/Admin (hỗ trợ tại quầy)
    private DatSan xacThucQuyenTruyCap(UUID datSanId, String email) {
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
            throw new RuntimeException("Lỗi: Đơn đặt sân này đã kết thúc, không thể gọi thêm dịch vụ!");
        }
        return datSan;
    }

    private GioHangPhanHoi taoPhanHoiGioHang(DatSan datSan) {
        List<DichVuDatSan> items = dichVuRepo.findByDatSanIdOrderByNgayDatAsc(datSan.getId());

        List<ChiTietDichVuPhanHoi> danhSach = items.stream().map(dv -> {
            BigDecimal thanhTien = dv.getGia().multiply(BigDecimal.valueOf(dv.getSoLuong()));
            return new ChiTietDichVuPhanHoi(
                    dv.getId(), dv.getSanPham().getId(), dv.getSanPham().getTenSanPham(),
                    dv.getSanPham().getHinhAnh(), dv.getGia(), dv.getSoLuong(), thanhTien);
        }).collect(Collectors.toList());

        BigDecimal tongTienDichVu = danhSach.stream()
                .map(ChiTietDichVuPhanHoi::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tienConLaiSan = datSan.getTongTien().subtract(datSan.getTienCoc());
        BigDecimal tongCanThanhToan = tienConLaiSan.add(tongTienDichVu);

        return new GioHangPhanHoi(danhSach, tongTienDichVu, tienConLaiSan, tongCanThanhToan);
    }

    @Override
    @Transactional
    public GioHangPhanHoi themVaoGioHang(UUID datSanId, String email, ThemDichVuYeuCau yeuCau) {
        DatSan datSan = xacThucQuyenTruyCap(datSanId, email);

        if (yeuCau.getSoLuong() == null || yeuCau.getSoLuong() <= 0) {
            throw new RuntimeException("Lỗi: Số lượng không hợp lệ!");
        }

        SanPham sanPham = sanPhamRepo.findById(yeuCau.getSanPhamId())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm!"));
        if (!sanPham.isConBan()) {
            throw new RuntimeException("Lỗi: Sản phẩm này hiện đã ngừng bán!");
        }

        // Nếu đơn đã gọi sản phẩm này rồi thì cộng dồn số lượng, không tạo dòng mới
        DichVuDatSan dv = dichVuRepo.findByDatSanIdAndSanPhamId(datSanId, sanPham.getId())
                .orElse(null);

        if (dv != null) {
            dv.setSoLuong(dv.getSoLuong() + yeuCau.getSoLuong());
        } else {
            dv = DichVuDatSan.builder()
                    .datSan(datSan)
                    .sanPham(sanPham)
                    .soLuong(yeuCau.getSoLuong())
                    .gia(sanPham.getGia()) // snapshot giá tại thời điểm gọi
                    .build();
        }
        dichVuRepo.save(dv);

        return taoPhanHoiGioHang(datSan);
    }

    @Override
    public GioHangPhanHoi layGioHang(UUID datSanId, String email) {
        DatSan datSan = xacThucQuyenTruyCap(datSanId, email);
        return taoPhanHoiGioHang(datSan);
    }

    @Override
    @Transactional
    public GioHangPhanHoi xoaKhoiGioHang(UUID chiTietId, String email) {
        DichVuDatSan dv = dichVuRepo.findById(chiTietId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy mục cần xoá!"));

        DatSan datSan = xacThucQuyenTruyCap(dv.getDatSan().getId(), email);
        dichVuRepo.deleteById(chiTietId);

        return taoPhanHoiGioHang(datSan);
    }
}