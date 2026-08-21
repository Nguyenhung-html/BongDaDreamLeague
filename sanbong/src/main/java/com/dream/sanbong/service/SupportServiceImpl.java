package com.dream.sanbong.service;

import com.dream.sanbong.dto.MessageDTO;
import com.dream.sanbong.dto.TicketDTO;
import com.dream.sanbong.entity.*;
import com.dream.sanbong.repository.MessageRepository;
import com.dream.sanbong.repository.TicketRepository;
import com.dream.sanbong.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupportServiceImpl implements SupportService {

    @Autowired private TicketRepository ticketRepo;
    @Autowired private MessageRepository messageRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Override
    public TicketDTO getOrCreateActiveTicket(UUID userId, String name, String phone) {
        Ticket ticket = ticketRepo.findByKhachHangIdAndTrangThaiNot(userId, Ticket.TrangThaiTicket.DA_DONG)
                .orElseGet(() -> {
                    Ticket t = new Ticket();
                    t.setMaTicket("TK-" + (1000 + new Random().nextInt(9000)));
                    t.setKhachHangId(userId);
                    t.setTenKhachHang(name);
                    t.setSoDienThoai(phone);
                    return ticketRepo.save(t);
                });
        return mapToTicketDTO(ticket);
    }

    @Override
    public List<MessageDTO> getMessagesByTicket(UUID ticketId) {
        return messageRepo.findByTicketIdOrderByThoiGianGuiAsc(ticketId)
                .stream().map(this::mapToMessageDTO).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public MessageDTO sendMessage(UUID ticketId, MessageDTO dto) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        
        Message msg = new Message();
        msg.setTicket(ticket);
        msg.setNguoiGuiId(dto.getNguoiGuiId());
        msg.setLoaiNguoiGui(dto.getLoaiNguoiGui());
        msg.setNoiDung(dto.getNoiDung());
        msg.setThoiGianGui(LocalDateTime.now());
        
        Message saved = messageRepo.save(msg);
        
        ticket.setNgayCapNhat(LocalDateTime.now());
        ticketRepo.save(ticket);

        MessageDTO result = mapToMessageDTO(saved);
        
        // Push Realtime qua WebSocket
        messagingTemplate.convertAndSend("/topic/ticket/" + ticketId, result);
        messagingTemplate.convertAndSend("/topic/staff/notifications", result);
        
        return result;
    }

    @Transactional
    @Override
    public void markAsRead(UUID ticketId, UUID currentUserId) {
        List<Message> unread = messageRepo.findByTicketIdAndDaDocFalseAndNguoiGuiIdNot(ticketId, currentUserId);
        for (Message m : unread) {
            m.setDaDoc(true);
            m.setThoiGianDoc(LocalDateTime.now());
        }
        messageRepo.saveAll(unread);
        messagingTemplate.convertAndSend("/topic/ticket/" + ticketId + "/seen", true);
    }

    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepo.findAllByOrderByNgayCapNhatDesc()
                .stream().map(this::mapToTicketDTO).collect(Collectors.toList());
    }

    @Override
    public TicketDTO tiepNhanTicket(UUID ticketId, UUID staffId) {
        Ticket t = ticketRepo.findById(ticketId).orElseThrow();
        t.setNhanVienId(staffId);
        t.setTrangThai(Ticket.TrangThaiTicket.DANG_XU_LY);
        return mapToTicketDTO(ticketRepo.save(t));
    }

    @Override
    public TicketDTO dongTicket(UUID ticketId) {
        Ticket t = ticketRepo.findById(ticketId).orElseThrow();
        t.setTrangThai(Ticket.TrangThaiTicket.DA_DONG);
        return mapToTicketDTO(ticketRepo.save(t));
    }

    private TicketDTO mapToTicketDTO(Ticket t) {
        TicketDTO dto = new TicketDTO();
        dto.setId(t.getId());
        dto.setMaTicket(t.getMaTicket());
        dto.setKhachHangId(t.getKhachHangId());
        dto.setTenKhachHang(t.getTenKhachHang());
        dto.setSoDienThoai(t.getSoDienThoai());
        dto.setNhanVienId(t.getNhanVienId());
        dto.setChuDe(t.getChuDe());
        dto.setTrangThai(t.getTrangThai());
        dto.setNgayTao(t.getNgayTao());
        dto.setNgayCapNhat(t.getNgayCapNhat());
        return dto;
    }

    private MessageDTO mapToMessageDTO(Message m) {
        MessageDTO dto = new MessageDTO();
        dto.setId(m.getId());
        dto.setTicketId(m.getTicket().getId());
        dto.setNguoiGuiId(m.getNguoiGuiId());
        dto.setLoaiNguoiGui(m.getLoaiNguoiGui());
        dto.setNoiDung(m.getNoiDung());
        dto.setThoiGianGui(m.getThoiGianGui());
        dto.setDaDoc(m.isDaDoc());
        dto.setThoiGianDoc(m.getThoiGianDoc());
        return dto;
    }
}