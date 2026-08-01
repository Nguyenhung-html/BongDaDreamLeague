package com.dream.sanbong.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Helper dùng chung cho các controller khu vực Admin.
 * Gom lại một chỗ phần đọc JWT từ header + kiểm tra vai trò, thay vì
 * chép đi chép lại hàm layEmailTuToken/xacThucQuyenAdmin ở từng controller.
 */
@Component
public class QuyenTruyCap {

    private final JwtUtils jwtUtils;

    public QuyenTruyCap(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /** Cắt chuỗi "Bearer xxx" lấy phần token, ném lỗi nếu thiếu header */
    public String layToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Lỗi: Không tìm thấy token xác thực!");
        }
        return authHeader.substring(7);
    }

    public String layEmail(HttpServletRequest request) {
        return jwtUtils.getEmailFromToken(layToken(request));
    }

    public String layVaiTro(HttpServletRequest request) {
        return jwtUtils.getVaiTroFromToken(layToken(request));
    }

    /** Bắt buộc phải là ADMIN, nếu không thì ném lỗi */
    public void batBuocAdmin(HttpServletRequest request) {
        if (!"ADMIN".equals(layVaiTro(request))) {
            throw new RuntimeException("Lỗi: Bạn không có quyền Admin để thực hiện thao tác này!");
        }
    }

    /** Cho phép cả STAFF lẫn ADMIN */
    public void batBuocStaffHoacAdmin(HttpServletRequest request) {
        String vaiTro = layVaiTro(request);
        if (!"STAFF".equals(vaiTro) && !"ADMIN".equals(vaiTro)) {
            throw new RuntimeException("Lỗi: Bạn không có quyền thực hiện thao tác này!");
        }
    }
}
