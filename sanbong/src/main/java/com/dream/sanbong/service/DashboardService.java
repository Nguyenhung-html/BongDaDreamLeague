package com.dream.sanbong.service;

import com.dream.sanbong.dto.DashboardTongQuanPhanHoi;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DatSanRepository datSanRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public DashboardTongQuanPhanHoi getThongKeTheoNgay(LocalDate ngay) {
        if (ngay == null) {
            ngay = LocalDate.now();
        }

        long soLuotDat = datSanRepository.demSoLuotDatTheoNgay(ngay);
        BigDecimal tongDoanhThu = datSanRepository.tinhTongDoanhThuTheoNgay(ngay);
        long soKhachHangMoi = nguoiDungRepository.demNguoiDungMoiTheoNgay(ngay);

        return new DashboardTongQuanPhanHoi(soLuotDat, tongDoanhThu, soKhachHangMoi);
    }
}