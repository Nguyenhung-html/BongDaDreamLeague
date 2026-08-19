package com.dream.sanbong.repository;

import com.dream.sanbong.entity.GiaHanGio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiaHanGioRepository extends JpaRepository<GiaHanGio, UUID> {
    List<GiaHanGio> findByDatSanIdOrderByNgayGiaHanAsc(UUID datSanId);
}