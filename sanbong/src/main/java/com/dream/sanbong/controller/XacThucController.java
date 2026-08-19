package com.dream.sanbong.controller;

import com.dream.sanbong.dto.*;
import com.dream.sanbong.service.XacThucService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/xac-thuc")
@CrossOrigin(origins = "*")
public class XacThucController {

    private final XacThucService xacThucService;

    public XacThucController(XacThucService xacThucService) {
        this.xacThucService = xacThucService;
    }

    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyTaiKhoan(@RequestBody @Valid DangKyYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.xuLyDangKy(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhapTaiKhoan(@RequestBody @Valid DangNhapYeuCau yeuCau) {
        try {
            XacThucPhanHoi ketQua = xacThucService.xuLyDangNhap(yeuCau);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> dangNhapGoogle(@RequestBody @Valid GoogleLoginYeuCau yeuCau) {
        try {
            XacThucPhanHoi ketQua = xacThucService.xuLyDangNhapGoogle(yeuCau);
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── CÁC API QUÊN MẬT KHẨU QUA EMAIL ──────────────────────────────────────────

    @PostMapping("/quen-mat-khau/gui-ma")
    public ResponseEntity<?> guiMaOtpQuenMatKhau(@RequestBody @Valid GuiMaOtpYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.guiMaOtpQuenMatKhau(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/quen-mat-khau/xac-thuc-ma")
    public ResponseEntity<?> kiemTraMaOtp(@RequestBody @Valid XacThucOtpYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.kiemTraMaOtp(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/quen-mat-khau/dat-lai-mat-khau")
    public ResponseEntity<?> datLaiMatKhau(@RequestBody @Valid DatLaiMatKhauYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.datLaiMatKhau(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── CÁC API QUÊN MẬT KHẨU QUA SMS ────────────────────────────────────────────

    @PostMapping("/quen-mat-khau/gui-ma-sms")
    public ResponseEntity<?> guiMaOtpSmsQuenMatKhau(@RequestBody @Valid GuiMaOtpSmsYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.guiMaOtpSmsQuenMatKhau(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/quen-mat-khau/xac-thuc-ma-sms")
    public ResponseEntity<?> kiemTraMaOtpSms(@RequestBody @Valid XacThucOtpSmsYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.kiemTraMaOtpSms(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/quen-mat-khau/dat-lai-mat-khau-sms")
    public ResponseEntity<?> datLaiMatKhauSms(@RequestBody @Valid DatLaiMatKhauSmsYeuCau yeuCau) {
        try {
            String ketQua = xacThucService.datLaiMatKhauSms(yeuCau);
            return ResponseEntity.ok(Map.of("message", ketQua));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}