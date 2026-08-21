package com.dream.sanbong.controller;

import com.dream.sanbong.dto.MessageDTO;
import com.dream.sanbong.dto.TicketDTO;
import com.dream.sanbong.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "*")
public class SupportController {

    @Autowired private SupportService supportService;

    @GetMapping("/ticket/active")
    public ResponseEntity<TicketDTO> getActiveTicket(
            @RequestParam UUID userId, 
            @RequestParam String name, 
            @RequestParam(required = false) String phone) {
        return ResponseEntity.ok(supportService.getOrCreateActiveTicket(userId, name, phone));
    }

    @GetMapping("/ticket/{ticketId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(supportService.getMessagesByTicket(ticketId));
    }

    @PostMapping("/ticket/{ticketId}/messages")
    public ResponseEntity<MessageDTO> sendMessage(@PathVariable UUID ticketId, @RequestBody MessageDTO dto) {
        return ResponseEntity.ok(supportService.sendMessage(ticketId, dto));
    }

    @PutMapping("/ticket/{ticketId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID ticketId, @RequestParam UUID userId) {
        supportService.markAsRead(ticketId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/tickets")
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(supportService.getAllTickets());
    }

    @PutMapping("/admin/ticket/{ticketId}/tiep-nhan")
    public ResponseEntity<TicketDTO> tiepNhanTicket(@PathVariable UUID ticketId, @RequestParam UUID staffId) {
        return ResponseEntity.ok(supportService.tiepNhanTicket(ticketId, staffId));
    }

    @PutMapping("/admin/ticket/{ticketId}/dong")
    public ResponseEntity<TicketDTO> dongTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(supportService.dongTicket(ticketId));
    }
}