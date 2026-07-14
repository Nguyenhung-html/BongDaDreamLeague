package com.dream.sanbong.service;

import com.dream.sanbong.dto.SePayReturnPhanHoi;
import com.dream.sanbong.dto.SePayTaoGiaoDichPhanHoi;
import com.dream.sanbong.entity.DatSan;
import com.dream.sanbong.entity.ThanhToan;
import com.dream.sanbong.repository.DatSanRepository;
import com.dream.sanbong.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SePayServiceImpl implements SePayService {

    private final ThanhToanRepository thanhToanRepo;
    private final DatSanRepository datSanRepo;

    public SePayServiceImpl(ThanhToanRepository thanhToanRepo, DatSanRepository datSanRepo) {
        this.thanhToanRepo = thanhToanRepo;
        this.datSanRepo = datSanRepo;
    }

    @Override
    @Transactional
    public SePayTaoGiaoDichPhanHoi taoGiaoDich(UUID thanhToanId) {
        ThanhToan thanhToan = thanhToanRepo.findById(thanhToanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy giao dịch thanh toán!"));

        String maGiaoDich = "VNP" + System.currentTimeMillis();
        thanhToan.setMaGiaoDich(maGiaoDich);
        thanhToanRepo.save(thanhToan);

        long soTien = thanhToan.getSoTien().longValue();
        String noiDungChuyenKhoan = "DatSan " + maGiaoDich;

        String qrUrl;
        try {
            qrUrl = "https://img.vietqr.io/image/970422-0973728967-compact2.png"
                    + "?amount=" + soTien
                    + "&addInfo=" + URLEncoder.encode(noiDungChuyenKhoan, StandardCharsets.UTF_8.toString())
                    + "&accountName=NguyenTienHung";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Lỗi tạo mã QR: " + e.getMessage());
        }

        return new SePayTaoGiaoDichPhanHoi(maGiaoDich, qrUrl, soTien);
    }

    @Override
    @Transactional
    public SePayReturnPhanHoi xuLyReturn(String maGiaoDich, String vnpResponseCode) {
        ThanhToan thanhToan = thanhToanRepo.findByMaGiaoDich(maGiaoDich)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy giao dịch!"));

        DatSan datSan = thanhToan.getDatSan();

        if ("00".equals(vnpResponseCode)) {
            thanhToan.setTrangThai("THANH_CONG");
            datSan.setTrangThai("DA_COC");
            thanhToanRepo.save(thanhToan);
            datSanRepo.save(datSan);
            return new SePayReturnPhanHoi(true, "Thanh toán thành công! Đơn đã được xác nhận đặt cọc.", "DA_COC");
        }

        thanhToan.setTrangThai("THAT_BAI");
        thanhToanRepo.save(thanhToan);
        return new SePayReturnPhanHoi(false, "Thanh toán thất bại. Vui lòng thử lại!", datSan.getTrangThai());
    }

    @Override
    @Transactional
    public boolean hoanTien(UUID thanhToanId) {
        ThanhToan thanhToan = thanhToanRepo.findById(thanhToanId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy giao dịch!"));

        thanhToan.setTrangThai("DA_HOAN_TIEN");
        thanhToan.setNgayHoanTien(LocalDateTime.now());
        thanhToanRepo.save(thanhToan);
        return true;
    }
}