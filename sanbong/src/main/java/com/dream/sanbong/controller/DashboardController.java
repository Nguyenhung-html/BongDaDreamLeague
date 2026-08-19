package com.dream.sanbong.controller;

import com.dream.sanbong.dto.DashboardTongQuanPhanHoi;
import com.dream.sanbong.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/thong-ke")
    public ResponseEntity<DashboardTongQuanPhanHoi> getThongKe(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        
        return ResponseEntity.ok(dashboardService.getThongKeTheoNgay(ngay));
    }
}