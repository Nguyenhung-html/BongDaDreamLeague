package com.dream.sanbong.controller;

import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.NguoiDungRepository;
import com.dream.sanbong.service.ThongBaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/thong-bao")
@CrossOrigin(origins = "*")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepo;

    public ThongBaoController(ThongBaoService thongBaoService, NguoiDungRepository nguoiDungRepo) {
        this.thongBaoService = thongBaoService;
        this.nguoiDungRepo = nguoiDungRepo;
    }

    private NguoiDung layNguoiDungHienTai(Authentication authentication) {
        String email = authentication.getName();
        return nguoiDungRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng!"));
    }

    @GetMapping
    public ResponseEntity<?> layDanhSach(Authentication authentication) {
        NguoiDung nd = layNguoiDungHienTai(authentication);
        return ResponseEntity.ok(thongBaoService.layDanhSach(nd.getId()));
    }

    @GetMapping("/chua-doc")
    public ResponseEntity<?> demChuaDoc(Authentication authentication) {
        NguoiDung nd = layNguoiDungHienTai(authentication);
        long soLuong = thongBaoService.demChuaDoc(nd.getId());
        return ResponseEntity.ok(Map.of("soLuong", soLuong));
    }

    @PutMapping("/{id}/da-doc")
    public ResponseEntity<?> danhDauDaDoc(@PathVariable UUID id) {
        thongBaoService.danhDauDaDoc(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/da-doc-tat-ca")
    public ResponseEntity<?> danhDauTatCaDaDoc(Authentication authentication) {
        NguoiDung nd = layNguoiDungHienTai(authentication);
        thongBaoService.danhDauTatCaDaDoc(nd.getId());
        return ResponseEntity.ok().build();
    }
}