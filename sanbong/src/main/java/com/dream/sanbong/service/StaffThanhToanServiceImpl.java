package com.dream.sanbong.service;

import com.dream.sanbong.dto.StaffThanhToanPhanHoi;
import com.dream.sanbong.dto.ThanhToanConLaiYeuCau;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffThanhToanServiceImpl implements StaffThanhToanService {

    private final ThanhToanRepository thanhToanRepo;
    private final DatSanRepository datSanRepo;

    public StaffThanhToanServiceImpl(ThanhToanRepository thanhToanRepo, DatSanRepository datSanRepo) {
        this.thanhToanRepo = thanhToanRepo;
        this.datSanRepo = datSanRepo;
    }

    @Override
    public List<StaffThanhToanPhanHoi> layDanhSachThanhToan(LocalDate tuNgay, LocalDate denNgay) {
        List<ThanhToan> danhSach;

        if (tuNgay != null && denNgay != null) {
            danhSach = thanhToanRepo.findByNgayThanhToanBetweenOrderByNgayThanhToanDesc(
                    tuNgay.atStartOfDay(),
                    denNgay.plusDays(1).atStartOfDay()
            );
        } else {
            danhSach = thanhToanRepo.findAllByOrderByNgayThanhToanDesc();
        }

        return danhSach.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StaffThanhToanPhanHoi xacNhanCoc(UUID thanhToanId, String emailStaff) {
        ThanhToan tt = thanhToanRepo.findById(thanhToanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch thanh toán!"));

        if (!"COC".equals(tt.getLoai())) {
            throw new RuntimeException("Chỉ có thể xác nhận giao dịch loại Cọc!");
        }
        if ("THANH_CONG".equals(tt.getTrangThai())) {
            throw new RuntimeException("Giao dịch này đã được xác nhận rồi!");
        }

        tt.setTrangThai("THANH_CONG");
        thanhToanRepo.save(tt);

        // Cập nhật trạng thái đơn → DA_COC
        DatSan datSan = tt.getDatSan();
        if ("CHO_XAC_NHAN".equals(datSan.getTrangThai())) {
            datSan.setTrangThai("DA_COC");
            datSanRepo.save(datSan);
        }

        return mapToDto(tt);
    }

    @Override
    @Transactional
    public StaffThanhToanPhanHoi thanhToanConLai(ThanhToanConLaiYeuCau yeuCau, String emailStaff) {
        DatSan datSan = datSanRepo.findById(yeuCau.getDatSanId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân!"));

        if (!"DA_COC".equals(datSan.getTrangThai())) {
            throw new RuntimeException("Đơn phải ở trạng thái Đã cọc mới có thể thu tiền nốt!");
        }

        // Kiểm tra xem đã thu tiền nốt chưa
        boolean daCoConLai = thanhToanRepo.findByDatSanId(datSan.getId())
                .stream()
                .anyMatch(t -> "THANH_TOAN_CON_LAI".equals(t.getLoai()) && "THANH_CONG".equals(t.getTrangThai()));
        if (daCoConLai) {
            throw new RuntimeException("Đơn này đã được thanh toán đầy đủ rồi!");
        }

        // Tính số tiền còn lại = tổng tiền - tiền cọc
        BigDecimal tienConLai = datSan.getTongTien().subtract(datSan.getTienCoc());

        // Tạo bản ghi thanh toán phần còn lại
        ThanhToan thanhToanConLai = ThanhToan.builder()
                .datSan(datSan)
                .soTien(tienConLai)
                .loai("THANH_TOAN_CON_LAI")
                .phuongThuc(yeuCau.getPhuongThuc() != null ? yeuCau.getPhuongThuc() : "TIEN_MAT")
                .trangThai("THANH_CONG")
                .build();
        thanhToanRepo.save(thanhToanConLai);

        // Cập nhật đơn → HOAN_THANH
        datSan.setTrangThai("HOAN_THANH");
        datSanRepo.save(datSan);

        return mapToDto(thanhToanConLai);
    }

    // ── Helper: chuyển entity sang DTO ────────────────────────────
    private StaffThanhToanPhanHoi mapToDto(ThanhToan tt) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DatSan ds = tt.getDatSan();
        return new StaffThanhToanPhanHoi(
                tt.getId(),
                ds.getId(),
                ds.getId().toString().substring(0, 8).toUpperCase(),
                ds.getHoTenDat(),
                ds.getSoDienThoai(),
                ds.getSanBong().getTenSan(),
                ds.getNgayDa().toString(),
                ds.getGioBatDau().toString(),
                ds.getGioKetThuc().toString(),
                tt.getSoTien(),
                tt.getLoai(),
                tt.getPhuongThuc(),
                tt.getMaGiaoDich(),
                tt.getTrangThai(),
                tt.getNgayThanhToan() != null ? tt.getNgayThanhToan().format(fmt) : ""
        );
    }
}
