package com.dream.sanbong.repository;

import com.dream.sanbong.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Optional<Voucher> findByMaIgnoreCase(String ma);

    List<Voucher> findAllByOrderByNgayBatDauDesc();

    @Query("select v from Voucher v where v.trangThai = :trangThai " +
           "and v.ngayBatDau <= :now " +
           "and v.ngayKetThuc >= :now " +
           "and v.daSuDung < v.soLuong " +
           "order by v.ngayKetThuc asc")
    List<Voucher> findVoucherDangHoatDong(
            @Param("trangThai") String trangThai,
            @Param("now") LocalDateTime now);
}
