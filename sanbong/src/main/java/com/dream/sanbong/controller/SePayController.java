package com.dream.sanbong.controller;

import com.dream.sanbong.dto.SePayTaoGiaoDichPhanHoi;
import com.dream.sanbong.dto.SePayTaoGiaoDichYeuCau;
import com.dream.sanbong.repository.ThanhToanRepository;
import com.dream.sanbong.service.SePayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/sepay")
@CrossOrigin(origins = "*")
public class SePayController {

    private final SePayService sePayService;
    private final ThanhToanRepository thanhToanRepo;

    @Value("${sepay.api-key}")
    private String sepayApiKey;

    public SePayController(SePayService sePayService, ThanhToanRepository thanhToanRepo) {
        this.sePayService = sePayService;
        this.thanhToanRepo = thanhToanRepo;
    }

    // Gọi trước khi hiện QR cho người dùng
    @PostMapping("/tao-giao-dich")
    public ResponseEntity<?> taoGiaoDich(@RequestBody SePayTaoGiaoDichYeuCau yeuCau) {
        try {
            SePayTaoGiaoDichPhanHoi ketQua = sePayService.taoGiaoDich(yeuCau.thanhToanId());
            return ResponseEntity.ok(ketQua);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Frontend polling để hỏi trạng thái
    @GetMapping("/trang-thai/{thanhToanId}")
    public ResponseEntity<?> kiemTraTrangThai(@PathVariable UUID thanhToanId) {
        return thanhToanRepo.findById(thanhToanId)
                .map(tt -> ResponseEntity.ok(Map.of("trangThai", tt.getTrangThai())))
                .orElse(ResponseEntity.notFound().build());
    }

    // SePay gọi vào đây khi có tiền vào MB Bank
    @PostMapping("/webhook")
    public ResponseEntity<?> nhanWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> payload) {

        System.out.println("=== SePay Webhook nhận được ===");
        System.out.println("Payload: " + payload);

        String apiKeyMongDoi = "Apikey " + sepayApiKey;
        if (!apiKeyMongDoi.equals(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Unauthorized"));
        }

        String noiDung = (String) payload.get("content");
        String maGiaoDich = trichMaGiaoDich(noiDung);

        if (maGiaoDich != null) {
            try {
                sePayService.xuLyReturn(maGiaoDich, "00");
                System.out.println("✅ Đã xác nhận thanh toán: " + maGiaoDich);
            } catch (Exception e) {
                System.out.println("❌ Lỗi xử lý: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Không tìm thấy mã giao dịch trong: " + noiDung);
        }

        return ResponseEntity.ok(Map.of("success", true));
    }

    private String trichMaGiaoDich(String noiDung) {
        if (noiDung == null) return null;
        Matcher m = Pattern.compile("VNP\\d+").matcher(noiDung);
        return m.find() ? m.group() : null;
    }
}