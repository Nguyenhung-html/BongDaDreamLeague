package com.dream.sanbong.repository;

import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {

    Optional<NguoiDung> findByEmail(String email);

    Optional<NguoiDung> findByEmailOrSoDienThoai(String email, String soDienThoai);

    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);

    // MỚI THÊM: lấy toàn bộ Staff/Admin để gửi thông báo khi có thanh toán mới
    List<NguoiDung> findByVaiTroIn(List<VaiTro> vaiTros);
}