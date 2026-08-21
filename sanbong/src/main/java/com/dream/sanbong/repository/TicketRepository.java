package com.dream.sanbong.repository;

import com.dream.sanbong.entity.Ticket;
import com.dream.sanbong.entity.Ticket.TrangThaiTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByKhachHangIdAndTrangThaiNot(UUID khachHangId, TrangThaiTicket trangThai);
    List<Ticket> findByKhachHangIdOrderByNgayCapNhatDesc(UUID khachHangId);
    List<Ticket> findAllByOrderByNgayCapNhatDesc();
}