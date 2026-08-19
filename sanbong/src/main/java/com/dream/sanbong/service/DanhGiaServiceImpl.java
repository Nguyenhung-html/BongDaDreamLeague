package com.dream.sanbong.service;

import com.dream.sanbong.dto.DanhGiaRequest;
import com.dream.sanbong.dto.DanhGiaResponse;
import com.dream.sanbong.entity.DanhGia;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.SanBong;
import com.dream.sanbong.repository.DanhGiaRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.repository.SanBongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DanhGiaServiceImpl implements DanhGiaService {

    private final DanhGiaRepository danhGiaRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SanBongRepository sanBongRepository;

    @Override
    public boolean checkQuyenDanhGia(UUID userId, UUID sanBongId) {
        long soDonHoanThanh = danhGiaRepository.countCompletedOrders(userId, sanBongId);
        if (soDonHoanThanh == 0) return false;

        long daDanhGia = danhGiaRepository.countByNguoiDungIdAndSanBongId(userId, sanBongId);
        return daDanhGia == 0; 
    }

    @Override
    @Transactional
    public void guiDanhGiaTheoEmail(String email, DanhGiaRequest request) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
                
        SanBong sanBong = sanBongRepository.findById(request.getSanBongId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng tương ứng với ID được cung cấp."));

        DanhGia danhGia = new DanhGia();
        danhGia.setNguoiDung(nguoiDung);
        danhGia.setSanBong(sanBong); 
        
        danhGia.setSoSao(request.getSoSao()); 
        danhGia.setNoiDung(request.getNoiDung()); 
        danhGia.setNgayDanhGia(LocalDateTime.now()); 
        danhGia.setHienThi(true); 
        
        danhGiaRepository.save(danhGia);
    }

    // 1. Lấy danh sách bình luận CÔNG KHAI (Dành cho khách hàng xem - Chỉ lấy hienThi = true)
    @Override
    public List<DanhGiaResponse> layDanhGiaCuaSan(UUID sanBongId) {
        return danhGiaRepository.findActiveEvaluationsBySanBongId(sanBongId);
    }

    // 🌟 BỔ SUNG 2. Lấy TOÀN BỘ danh sách bình luận kể cả bị ẩn (Dành cho Admin quản lý)
   // BỔ SUNG 2. Lấy TOÀN BỘ danh sách bình luận kể cả bị ẩn (Dành cho Admin quản lý)
    @Override
    public List<DanhGiaResponse> adminLayDanhGiaCuaSan(UUID sanBongId) {
        List<DanhGia> dsDanhGia = danhGiaRepository.findBySanBongIdOrderByNgayDanhGiaDesc(sanBongId);

        return dsDanhGia.stream().map(dg -> new DanhGiaResponse(
            dg.getId(),
            dg.getNguoiDung() != null ? dg.getNguoiDung().getHoTen() : "Ẩn danh",
            dg.getSoSao(),
            dg.getNoiDung(),
            dg.getNgayDanhGia()
        )).collect(Collectors.toList());
    }

    // 🌟 TINH CHỈNH VÀ SỬA ĐỔI 3. Hàm ẩn/hiện bình luận (Chuyển sang cơ chế Đảo trạng thái true <-> false)
    @Override
    @Transactional
    public void adminAnBinhLuan(UUID danhGiaId) {
        DanhGia dg = danhGiaRepository.findById(danhGiaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận với ID: " + danhGiaId));
        
        // Đảo ngược trạng thái hiện tại: Nếu đang true (hiện) -> đổi thành false (ẩn) và ngược lại
        dg.setHienThi(!dg.isHienThi()); 
        danhGiaRepository.save(dg);
    }

    // 🌟 BỔ SUNG 4. Xóa vĩnh viễn đánh giá khỏi Database
    @Override
    @Transactional
    public void adminXoaBinhLuan(UUID danhGiaId) {
        if (!danhGiaRepository.existsById(danhGiaId)) {
            throw new RuntimeException("Không tìm thấy bình luận cần xóa với ID: " + danhGiaId);
        }
        danhGiaRepository.deleteById(danhGiaId);
    }
}