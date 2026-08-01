package com.dream.sanbong.dto;

import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfilePhanHoi {
    private UUID id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private VaiTro vaiTro;

    public static ProfilePhanHoi fromEntity(NguoiDung nguoiDung) {
        return ProfilePhanHoi.builder()
                .id(nguoiDung.getId())
                .hoTen(nguoiDung.getHoTen())
                .email(nguoiDung.getEmail())
                .soDienThoai(nguoiDung.getSoDienThoai())
                .vaiTro(nguoiDung.getVaiTro())
                .build();
    }
}