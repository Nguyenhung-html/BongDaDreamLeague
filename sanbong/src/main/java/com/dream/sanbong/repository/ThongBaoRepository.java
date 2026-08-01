package com.dream.sanbong.repository;

import com.dream.sanbong.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, UUID> {

    List<ThongBao> findByNguoiDungIdOrderByNgayTaoDesc(UUID nguoiDungId);

    long countByNguoiDungIdAndDaDocFalse(UUID nguoiDungId);

    List<ThongBao> findByNguoiDungIdAndDaDocFalse(UUID nguoiDungId);

    // Dọn thông báo của 1 tài khoản trước khi Admin xoá tài khoản đó
    // (bảng THONG_BAO có khoá ngoại trỏ tới USERS)
    void deleteByNguoiDungId(UUID nguoiDungId);
}