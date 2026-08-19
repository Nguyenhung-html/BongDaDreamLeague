package com.dream.sanbong.controller;

import com.dream.sanbong.dto.DangKyYeuCau;
import com.dream.sanbong.dto.DangNhapYeuCau;
import com.dream.sanbong.dto.XacThucPhanHoi;
import com.dream.sanbong.service.XacThucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xac-thuc")
@CrossOrigin(origins = "*") 
public class XacThucController {

    private final XacThucService xacThucService;

    public XacThucController(XacThucService xacThucService) {
        this.xacThucService = xacThucService;
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyTaiKhoan(@RequestBody DangKyYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.xuLyDangKy(yeuCau);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhapTaiKhoan(@RequestBody DangNhapYeuCau yeuCau) {
        try {
            XacThucPhanHoi ketQua = xacThucService.xuLyDangNhap(yeuCau);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}