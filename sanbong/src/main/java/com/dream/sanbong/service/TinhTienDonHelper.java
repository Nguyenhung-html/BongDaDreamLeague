package com.dream.sanbong.service;

import com.dream.sanbong.dto.ChiTietDichVuPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.DichVuDatSan;
import com.dream.sanbong.entity.ThanhToan;
import com.dream.sanbong.repository.DichVuDatSanRepository;
import com.dream.sanbong.repository.ThanhToanRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Gom một chỗ phần tính tiền của 1 đơn đặt sân, vì cả ba màn hình Admin
 * (đơn đặt sân, giao dịch thanh toán, hoá đơn) đều cần đúng công thức này.
 *
 * Lưu ý về dữ liệu: DAT_SAN.tong_tien đã bao gồm tiền gia hạn giờ
 * (GiaHanGioService cộng thẳng vào), nhưng KHÔNG bao gồm tiền đồ uống/dịch vụ
 * — phần đó nằm riêng ở bảng DICH_VU_DAT_SAN nên phải cộng thủ công.
 */
@Component
public class TinhTienDonHelper {

    private final DichVuDatSanRepository dichVuRepo;
    private final ThanhToanRepository thanhToanRepo;

    public TinhTienDonHelper(DichVuDatSanRepository dichVuRepo, ThanhToanRepository thanhToanRepo) {
        this.dichVuRepo = dichVuRepo;
        this.thanhToanRepo = thanhToanRepo;
    }

    /** Danh sách đồ uống / dịch vụ khách đã gọi cho đơn này */
    public List<ChiTietDichVuPhanHoi> layChiTietDichVu(UUID datSanId) {
        return dichVuRepo.findByDatSanIdOrderByNgayDatAsc(datSanId).stream()
                .map(dv -> new ChiTietDichVuPhanHoi(
                        dv.getId(),
                        dv.getSanPham().getId(),
                        dv.getSanPham().getTenSanPham(),
                        dv.getSanPham().getHinhAnh(),
                        dv.getGia(),
                        dv.getSoLuong(),
                        thanhTien(dv)
                ))
                .toList();
    }

    /** Tổng tiền đồ uống / dịch vụ của đơn */
    public BigDecimal tongTienDichVu(UUID datSanId) {
        return dichVuRepo.findByDatSanIdOrderByNgayDatAsc(datSanId).stream()
                .map(this::thanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Tổng số tiền đã thực thu (chỉ cộng giao dịch THANH_CONG) */
    public BigDecimal daThanhToan(UUID datSanId) {
        return thanhToanRepo.findByDatSanId(datSanId).stream()
                .filter(tt -> "THANH_CONG".equals(tt.getTrangThai()))
                .map(ThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Tổng phải thu = tiền sân (đã gồm gia hạn) + tiền dịch vụ */
    public BigDecimal tongPhaiThu(DatSan datSan) {
        BigDecimal tienSan = datSan.getTongTien() != null ? datSan.getTongTien() : BigDecimal.ZERO;
        return tienSan.add(tongTienDichVu(datSan.getId()));
    }

    /** Số tiền còn phải thu, không bao giờ âm */
    public BigDecimal conLai(DatSan datSan) {
        BigDecimal conLai = tongPhaiThu(datSan).subtract(daThanhToan(datSan.getId()));
        return conLai.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : conLai;
    }

    private BigDecimal thanhTien(DichVuDatSan dv) {
        return dv.getGia().multiply(BigDecimal.valueOf(dv.getSoLuong()));
    }
}
