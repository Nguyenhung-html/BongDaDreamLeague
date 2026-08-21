package com.dream.sanbong.service;

import com.dream.sanbong.dto.MessageDTO;
import com.dream.sanbong.dto.TicketDTO;
import java.util.List;
import java.util.UUID;

public interface SupportService {
    TicketDTO getOrCreateActiveTicket(UUID userId, String name, String phone);
    List<MessageDTO> getMessagesByTicket(UUID ticketId);
    MessageDTO sendMessage(UUID ticketId, MessageDTO dto);
    void markAsRead(UUID ticketId, UUID currentUserId);
    List<TicketDTO> getAllTickets();
    TicketDTO tiepNhanTicket(UUID ticketId, UUID staffId);
    TicketDTO dongTicket(UUID ticketId);
}