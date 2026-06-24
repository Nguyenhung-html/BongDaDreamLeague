package com.dream.sanbong.repository;

import com.dream.sanbong.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {
    
    Optional<NguoiDung> findByEmail(String email);
    
    
    Optional<NguoiDung> findByEmailOrSoDienThoai(String email, String soDienThoai);
    
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
}