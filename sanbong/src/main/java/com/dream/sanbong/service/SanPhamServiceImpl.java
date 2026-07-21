package com.dream.sanbong.service;

import com.dream.sanbong.dto.SanPhamPhanHoi;
import com.dream.sanbong.dto.SanPhamYeuCau;
import com.dream.sanbong.entity.SanPham;
import com.dream.sanbong.repository.SanPhamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepo;

    public SanPhamServiceImpl(SanPhamRepository sanPhamRepo) {
        this.sanPhamRepo = sanPhamRepo;
    }

    private SanPhamPhanHoi mapPhanHoi(SanPham sp) {
        return new SanPhamPhanHoi(sp.getId(), sp.getTenSanPham(), sp.getGia(),
                sp.getHinhAnh(), sp.getMoTa(), sp.getLoai(), sp.isConBan());
    }

    @Override
    public List<SanPhamPhanHoi> layDanhSachConBan() {
        return sanPhamRepo.findByConBanTrueOrderByLoaiAscTenSanPhamAsc()
                .stream().map(this::mapPhanHoi).collect(Collectors.toList());
    }

    @Override
    public List<SanPhamPhanHoi> layTatCa() {
        return sanPhamRepo.findAllByOrderByLoaiAscTenSanPhamAsc()
                .stream().map(this::mapPhanHoi).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SanPhamPhanHoi them(SanPhamYeuCau yeuCau) {
        if (yeuCau.getTenSanPham() == null || yeuCau.getTenSanPham().isBlank()) {
            throw new RuntimeException("Lỗi: Tên sản phẩm không được để trống!");
        }
        if (yeuCau.getGia() == null || yeuCau.getGia().signum() < 0) {
            throw new RuntimeException("Lỗi: Giá sản phẩm không hợp lệ!");
        }
        SanPham sp = SanPham.builder()
                .tenSanPham(yeuCau.getTenSanPham())
                .gia(yeuCau.getGia())
                .hinhAnh(yeuCau.getHinhAnh())
                .moTa(yeuCau.getMoTa())
                .loai(yeuCau.getLoai())
                .conBan(yeuCau.isConBan())
                .build();
        return mapPhanHoi(sanPhamRepo.save(sp));
    }

    @Override
    @Transactional
    public SanPhamPhanHoi sua(UUID id, SanPhamYeuCau yeuCau) {
        SanPham sp = sanPhamRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm!"));
        sp.setTenSanPham(yeuCau.getTenSanPham());
        sp.setGia(yeuCau.getGia());
        sp.setHinhAnh(yeuCau.getHinhAnh());
        sp.setMoTa(yeuCau.getMoTa());
        sp.setLoai(yeuCau.getLoai());
        sp.setConBan(yeuCau.isConBan());
        return mapPhanHoi(sanPhamRepo.save(sp));
    }

    @Override
    @Transactional
    public void xoa(UUID id) {
        if (!sanPhamRepo.existsById(id)) {
            throw new RuntimeException("Lỗi: Không tìm thấy sản phẩm!");
        }
        sanPhamRepo.deleteById(id);
    }
}