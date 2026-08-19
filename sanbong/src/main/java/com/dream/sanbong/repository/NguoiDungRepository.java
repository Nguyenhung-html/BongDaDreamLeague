package com.dream.sanbong.repository;

import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {

    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);

    Optional<NguoiDung> findByEmailOrSoDienThoai(String email, String soDienThoai);

    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);

    // MỚI THÊM: lấy toàn bộ Staff/Admin để gửi thông báo khi có thanh toán mới
    List<NguoiDung> findByVaiTroIn(List<VaiTro> vaiTros);

    // ── Admin queries ──────────────────────────────────────────────

    // Danh sách theo 1 vai trò, mới đăng ký trước (Admin quản lý khách hàng)
    List<NguoiDung> findByVaiTroOrderByNgayTaoDesc(VaiTro vaiTro);

    // Danh sách theo nhiều vai trò, mới đăng ký trước (Admin quản lý nhân viên)
    List<NguoiDung> findByVaiTroInOrderByNgayTaoDesc(List<VaiTro> vaiTros);

    // ── Dashboard queries (SCRUM-76) ───────────────────────────────

    /**
     * Đếm số lượng người dùng mới đăng ký theo ngày (loại trừ vai trò ADMIN / STAFF nếu cần)
     */
    @Query(value = """
        SELECT COUNT(nd.id) FROM NGUOI_DUNG nd
        WHERE CAST(nd.ngay_tao AS DATE) = :ngay
          AND nd.vai_tro = 'CUSTOMER'
        """, nativeQuery = true)
    long demNguoiDungMoiTheoNgay(@Param("ngay") LocalDate ngay);
}