package com.dream.sanbong.service;

import com.dream.sanbong.dto.CapNhatProfileYeuCau;
import com.dream.sanbong.dto.ProfilePhanHoi;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;

    // Xem thông tin cá nhân
    public ProfilePhanHoi getProfile(UUID nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ProfilePhanHoi.fromEntity(nguoiDung);
    }

    // Cập nhật thông tin cá nhân
    @Transactional
    public ProfilePhanHoi updateProfile(UUID nguoiDungId, CapNhatProfileYeuCau yeuCau) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (yeuCau.getHoTen() != null) {
            nguoiDung.setHoTen(yeuCau.getHoTen());
        }
        if (yeuCau.getSoDienThoai() != null) {
            nguoiDung.setSoDienThoai(yeuCau.getSoDienThoai());
        }
        if (yeuCau.getEmail() != null) {
            nguoiDung.setEmail(yeuCau.getEmail());
        }

        NguoiDung updated = nguoiDungRepository.save(nguoiDung);
        return ProfilePhanHoi.fromEntity(updated);
    }
}