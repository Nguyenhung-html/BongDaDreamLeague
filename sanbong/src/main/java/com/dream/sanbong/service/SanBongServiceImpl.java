package com.dream.sanbong.service;

import com.dream.sanbong.dto.GiaSanPhanHoi;
import com.dream.sanbong.dto.SanBongPhanHoi;
import com.dream.sanbong.entity.GiaSan;
import com.dream.sanbong.entity.SanBong;
import com.dream.sanbong.repository.GiaSanRepository;
import com.dream.sanbong.repository.SanBongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SanBongServiceImpl implements SanBongService {

    private final SanBongRepository sanBongRepo;
    private final GiaSanRepository giaSanRepo;

    public SanBongServiceImpl(SanBongRepository sanBongRepo, GiaSanRepository giaSanRepo) {
        this.sanBongRepo = sanBongRepo;
        this.giaSanRepo = giaSanRepo;
    }

    @Override
    public List<SanBongPhanHoi> layTatCaSan() {
        return sanBongRepo.findByTrangThai("HOAT_DONG")
                .stream()
                .map(this::chuyenDoiSanBong)
                .toList();
    }

    @Override
    public SanBongPhanHoi layChiTietSan(UUID sanId) {
        SanBong san = sanBongRepo.findById(sanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân với ID: " + sanId));
        return chuyenDoiSanBong(san);
    }

    private SanBongPhanHoi chuyenDoiSanBong(SanBong san) {
        List<GiaSanPhanHoi> danhSachGia = giaSanRepo
                .findBySanBongIdOrderByGioBatDauAsc(san.getId())
                .stream()
                .map(g -> new GiaSanPhanHoi(
                        g.getId(),
                        g.getGioBatDau().toString(),
                        g.getGioKetThuc().toString(),
                        g.getGiaTien()
                ))
                .toList();

        return new SanBongPhanHoi(
                san.getId(),
                san.getTenSan(),
                san.getLoaiSan(),
                san.getMoTa(),
                san.getDiaChi(),
                san.getHinhAnh(),
                san.getTrangThai(),
                danhSachGia
        );
    }
}
