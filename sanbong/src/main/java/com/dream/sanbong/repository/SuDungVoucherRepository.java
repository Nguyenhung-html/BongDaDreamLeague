package com.dream.sanbong.repository;
import com.dream.sanbong.entity.SuDungVoucher; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Modifying; import org.springframework.data.jpa.repository.Query; import org.springframework.data.repository.query.Param; import java.util.*;
public interface SuDungVoucherRepository extends JpaRepository<SuDungVoucher, UUID> {
    long countByVoucherIdAndNguoiDungId(UUID voucherId, UUID nguoiDungId);
    Optional<SuDungVoucher> findByDatSanId(UUID datSanId);
    @Modifying @Query("delete from SuDungVoucher s where s.datSan.id = :datSanId") int deleteByDatSanId(@Param("datSanId") UUID datSanId);
}
