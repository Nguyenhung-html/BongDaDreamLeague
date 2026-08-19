package com.dream.sanbong.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);
    private static final String ESMS_API_URL = "https://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_post_json/";

    @Value("${esms.api-key:}")
    private String apiKey;

    @Value("${esms.secret-key:}")
    private String secretKey;

    @Value("${esms.sms-type:8}")
    private Integer smsType;

    @Value("${esms.brandname:}")
    private String brandname;

    @Value("${esms.sandbox:0}")
    private Integer sandbox;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void guiMaOtpQuenMatKhau(String soDienThoai, String hoTen, String maOtp) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new RuntimeException("Lỗi: Số điện thoại nhận OTP không được để trống!");
        }

        // 1. Chuẩn hóa số điện thoại nhận tin (VD: 0702685089)
        String normalizedPhone = chuanHoaSoDienThoai(soDienThoai.trim());

        // 2. Soạn nội dung tin nhắn OTP theo đúng chuẩn mẫu Brandname đã duyệt của eSMS
        String tenBrand = (brandname != null && !brandname.trim().isEmpty()) ? brandname.trim() : "Baotrixemay";
        String noiDungSms = String.format("%s la ma dat lai mat khau %s cua ban", maOtp, tenBrand);

        // 3. Kiểm tra ApiKey và SecretKey
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("your_esms_api_key")
                || secretKey == null || secretKey.trim().isEmpty() || secretKey.contains("your_esms_secret_key")) {
            throw new RuntimeException("Cấu hình eSMS chưa được thiết lập. Vui lòng điền 'esms.api-key' và 'esms.secret-key' trong file application.properties!");
        }

        // 4. Gửi yêu cầu qua eSMS.vn API
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("ApiKey", apiKey.trim());
            requestBody.put("SecretKey", secretKey.trim());
            requestBody.put("Phone", normalizedPhone);
            requestBody.put("Content", noiDungSms);
            requestBody.put("SmsType", smsType != null ? smsType : 8);
            requestBody.put("Brandname", (brandname != null && !brandname.trim().isEmpty()) ? brandname.trim() : "");
            requestBody.put("IsUnicode", 0);
            requestBody.put("Sandbox", sandbox != null ? sandbox : 0);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Đang gửi SMS OTP qua eSMS.vn tới số {}...", normalizedPhone);
            // Nhận kết quả dưới dạng String để tương thích hoàn toàn với response text/html của eSMS
            ResponseEntity<String> response = restTemplate.postForEntity(ESMS_API_URL, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String bodyStr = response.getBody();
                log.info("Phản hồi từ eSMS.vn: {}", bodyStr);

                String codeResult = trichXuatTruongJson(bodyStr, "CodeResult");
                String errorMessage = trichXuatTruongJson(bodyStr, "ErrorMessage");
                String smsId = trichXuatTruongJson(bodyStr, "SMSID");

                // CodeResult = "100" là thành công
                if ("100".equals(codeResult)) {
                    log.info("Đã gửi SMS OTP thành công qua eSMS.vn tới {}. SMSID: {}", normalizedPhone, smsId);
                    return;
                }

                // Xử lý các mã lỗi từ eSMS.vn
                log.error("eSMS.vn API trả về lỗi: CodeResult={}, ErrorMessage={}", codeResult, errorMessage);
                switch (codeResult) {
                    case "101":
                        throw new RuntimeException("Lỗi xác thực eSMS: ApiKey hoặc SecretKey không chính xác. Vui lòng kiểm tra lại cấu hình!");
                    case "102":
                        throw new RuntimeException("Tài khoản eSMS của bạn hiện đang bị khóa hoặc chưa được kích hoạt.");
                    case "103":
                        throw new RuntimeException("Tài khoản eSMS không đủ số dư để gửi tin nhắn OTP (Số dư hiện tại là 0 VNĐ). Vui lòng nạp tiền vào tài khoản trên eSMS.vn!");
                    case "104":
                        throw new RuntimeException("Brandname (" + brandname + ") chưa được duyệt hoặc không tồn tại trên tài khoản eSMS của bạn!");
                    case "118":
                        throw new RuntimeException("Số điện thoại nhận tin nhắn không đúng định dạng: " + normalizedPhone);
                    default:
                        throw new RuntimeException("Không thể gửi tin nhắn qua eSMS: " + (errorMessage.isEmpty() ? "Lỗi mã " + codeResult : errorMessage));
                }
            } else {
                throw new RuntimeException("Máy chủ eSMS.vn phản hồi lỗi HTTP " + response.getStatusCode());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi ngoại lệ khi gọi eSMS.vn API: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi kết nối cổng SMS (eSMS.vn): " + e.getMessage(), e);
        }
    }

    /**
     * Trích xuất giá trị trường JSON từ chuỗi phản hồi mà không phụ thuộc thư viện ngoài
     */
    private String trichXuatTruongJson(String json, String fieldName) {
        if (json == null || fieldName == null) return "";
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"?([^\",}]+)\"?");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * Chuẩn hóa số điện thoại nội địa Việt Nam:
     * +84702685089 -> 0702685089
     * 84702685089 -> 0702685089
     * 0702685089 -> 0702685089
     */
    private String chuanHoaSoDienThoai(String phone) {
        String cleaned = phone.replaceAll("[\\s\\-\\.\\(\\)]", "");
        if (cleaned.startsWith("+84")) {
            return "0" + cleaned.substring(3);
        }
        if (cleaned.startsWith("84")) {
            return "0" + cleaned.substring(2);
        }
        return cleaned;
    }
}