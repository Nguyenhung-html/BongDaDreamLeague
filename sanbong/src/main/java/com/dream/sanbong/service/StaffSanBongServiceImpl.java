package com.dream.sanbong.service;

import com.dream.sanbong.dto.LichSanPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.SanBong;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.SanBongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffSanBongServiceImpl implements StaffSanBongService {

    private final SanBongRepository sanBongRepo;
    private final DatSanRepository datSanRepo;

    public StaffSanBongServiceImpl(SanBongRepository sanBongRepo, DatSanRepository datSanRepo) {
        this.sanBongRepo = sanBongRepo;
        this.datSanRepo = datSanRepo;
    }

    @Override
    public List<LichSanPhanHoi> layLichSanTheoNgay(LocalDate ngay) {
        List<SanBong> tatCaSan = sanBongRepo.findAll();
        LocalTime gioHienTai = LocalTime.now();
        LocalDate homNay = LocalDate.now();

        return tatCaSan.stream().map(san -> {
            // Lấy tất cả đơn chưa huỷ trong ngày cho sân này
            List<DatSan> danhSachDat = datSanRepo
                    .findBySanBongIdAndNgayDaAndTrangThaiNotOrderByGioBatDauAsc(
                            san.getId(), ngay, "DA_HUY");

            List<LichSanPhanHoi.SlotDatSan> slots = danhSachDat.stream().map(d -> {
                // Xác định trạng thái hiển thị: đang đá nếu trong khung giờ hiện tại hôm nay
                String trangThaiHienThi = d.getTrangThai();
                if ("DA_COC".equals(d.getTrangThai()) && ngay.equals(homNay)) {
                    if (!gioHienTai.isBefore(d.getGioBatDau()) && gioHienTai.isBefore(d.getGioKetThuc())) {
                        trangThaiHienThi = "DANG_DA"; // Trạng thái ảo — suy luận từ thời gian
                    }
                }
                return new LichSanPhanHoi.SlotDatSan(
                        d.getId(),
                        d.getHoTenDat(),
                        d.getSoDienThoai(),
                        d.getGioBatDau().toString(),
                        d.getGioKetThuc().toString(),
                        trangThaiHienThi,
                        d.getTongTien()
                );
            }).collect(Collectors.toList());

            return new LichSanPhanHoi(
                    san.getId(),
                    san.getTenSan(),
                    san.getLoaiSan(),
                    san.getTrangThai(),
                    slots
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String capNhatTrangThaiSan(UUID sanBongId, String trangThai, String lyDo, String emailStaff) {
        SanBong sanBong = sanBongRepo.findById(sanBongId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân!"));

        if (!"HOAT_DONG".equals(trangThai) && !"BAO_TRI".equals(trangThai)) {
            throw new RuntimeException("Trạng thái không hợp lệ! Chỉ chấp nhận: HOAT_DONG, BAO_TRI");
        }

        String trangThaiCu = sanBong.getTrangThai();
        sanBong.setTrangThai(trangThai);
        sanBongRepo.save(sanBong);

        if ("BAO_TRI".equals(trangThai)) {
            return "Đã khóa sân " + sanBong.getTenSan() + " để bảo trì. Lý do: "
                    + (lyDo != null ? lyDo : "Không có lý do cụ thể");
        } else {
            return "Đã mở lại sân " + sanBong.getTenSan() + ". Sân đang hoạt động bình thường.";
        }
    }
}
