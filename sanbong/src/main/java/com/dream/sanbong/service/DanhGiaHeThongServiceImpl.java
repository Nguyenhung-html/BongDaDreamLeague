package com.dream.sanbong.service;

import com.dream.sanbong.dto.DanhGiaHeThongRequest;
import com.dream.sanbong.dto.DanhGiaHeThongResponse;
import com.dream.sanbong.dto.PhanHoiRequest;
import com.dream.sanbong.entity.DanhGiaHeThong;
import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.DanhGiaHeThongRepository;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DanhGiaHeThongServiceImpl implements DanhGiaHeThongService {

    @Autowired
    private DanhGiaHeThongRepository danhGiaRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public List<DanhGiaHeThongResponse> getAllDanhGiaHienThi() {
        return danhGiaRepository.findByHienThiTrueOrderByNgayDanhGiaDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DanhGiaHeThongResponse> getAllDanhGiaChoQuanLy() {
        return danhGiaRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayDanhGia"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Hàm tạo đánh giá từ Token (Tự động tra cứu NguoiDung theo email)
    @Override
    public DanhGiaHeThong createDanhGia(DanhGiaHeThongRequest request, String email) {
        DanhGiaHeThong dg = new DanhGiaHeThong();
        dg.setSoSao(request.getRating());
        dg.setNoiDung(request.getQuote());
        dg.setNgayDanhGia(LocalDateTime.now());
        dg.setHienThi(true); // Mặc định hiển thị hoặc false tùy quy trình duyệt

        if (email != null && !email.isBlank()) {
            NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
            if (user != null) {
                dg.setNguoiDungId(user.getId());
                String fullName = user.getHoTen();
                dg.setTenNguoiDung(fullName != null && !fullName.isBlank() ? fullName : user.getEmail());
                String roleName = formatRoleName(user.getVaiTro() != null ? user.getVaiTro().name() : null);
                dg.setVaiTro(roleName);
            } else {
                setFallbackUserInfo(dg, request);
            }
        } else {
            setFallbackUserInfo(dg, request);
        }

        return danhGiaRepository.save(dg);
    }

    // Giữ lại overload cũ nếu muốn hỗ trợ tạo đánh giá không bắt buộc login
    @Override
    public DanhGiaHeThong createDanhGia(DanhGiaHeThongRequest request) {
        return createDanhGia(request, null);
    }

    // [Staff & Admin] Phản hồi đánh giá
    @Override
    @Transactional
    public DanhGiaHeThongResponse phanHoiDanhGia(UUID id, PhanHoiRequest request) {
        DanhGiaHeThong dg = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));

        dg.setPhanHoi(request.getNoiDungPhanHoi());
        dg.setNgayPhanHoi(LocalDateTime.now());

        // 1. Nếu Frontend có gửi nguoiPhanHoiId lên thì lấy trực tiếp
        if (request.getNguoiPhanHoiId() != null) {
            dg.setNguoiPhanHoiId(request.getNguoiPhanHoiId());
        } else {
            // 2. Lấy từ Spring Security Context
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    // Giả sử email/username được lưu trong Principal
                    String email = auth.getName(); 
                    nguoiDungRepository.findByEmail(email).ifPresent(user -> {
                        dg.setNguoiPhanHoiId(user.getId());
                    });
                }
            } catch (Exception e) {
                System.err.println("Lỗi lấy user từ Security: " + e.getMessage());
            }
        }

        DanhGiaHeThong saved = danhGiaRepository.save(dg);
        return mapToResponse(saved);
    }

    // [Admin] Đổi trạng thái Ẩn/Hiện
    @Override
    public DanhGiaHeThong thayDoiTrangThaiHienThi(UUID id, boolean hienThi) {
        DanhGiaHeThong dg = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá với ID: " + id));

        dg.setHienThi(hienThi);
        return danhGiaRepository.save(dg);
    }

    // [Admin] Xóa đánh giá
    @Override
    public void xoaDanhGia(UUID id) {
        if (!danhGiaRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy đánh giá để xóa");
        }
        danhGiaRepository.deleteById(id);
    }

    private DanhGiaHeThongResponse mapToResponse(DanhGiaHeThong dg) {
    String tenNguoiPhanHoi = null;
    String vaiTroNguoiPhanHoi = null;

    // Lấy thông tin Tên và Vai trò từ nguoiDungRepository (chữ n viết thường)
    if (dg.getNguoiPhanHoiId() != null) {
        var userOptional = nguoiDungRepository.findById(dg.getNguoiPhanHoiId());
        if (userOptional.isPresent()) {
            var user = userOptional.get();
            tenNguoiPhanHoi = user.getHoTen(); // Hoặc user.getTen()
            if (user.getVaiTro() != null) {
                vaiTroNguoiPhanHoi = user.getVaiTro().name(); // 👉 SỬA DÒNG NÀY
            }
        }
    }

    return DanhGiaHeThongResponse.builder()
            .id(dg.getId())
            .name(dg.getTenNguoiDung())
            .role(dg.getVaiTro())
            .rating(dg.getSoSao())
            .quote(dg.getNoiDung())
            .hienThi(dg.getHienThi())
            .ngayDanhGia(dg.getNgayDanhGia())
            .phanHoi(dg.getPhanHoi())
            .ngayPhanHoi(dg.getNgayPhanHoi())
            .nguoiPhanHoiId(dg.getNguoiPhanHoiId())
            .tenNguoiPhanHoi(tenNguoiPhanHoi)
            .vaiTroNguoiPhanHoi(vaiTroNguoiPhanHoi)
            .build();
}

    private void setFallbackUserInfo(DanhGiaHeThong dg, DanhGiaHeThongRequest request) {
        dg.setTenNguoiDung(request.getName() != null && !request.getName().isBlank() ? request.getName() : "Khách hàng");
        dg.setVaiTro("Khách hàng");
    }

    private String formatRoleName(String rawRole) {
        if (rawRole == null) return "Khách hàng";
        switch (rawRole.toUpperCase()) {
            case "ADMIN":
            case "ROLE_ADMIN":
            case "QUAN_TRI":
                return "Quản trị viên";
            case "STAFF":
            case "ROLE_STAFF":
            case "NHAN_VIEN":
                return "Nhân viên sân";
            default:
                return "Khách hàng";
        }
    }
}