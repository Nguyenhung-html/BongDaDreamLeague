package com.dream.sanbong.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void guiMaOtpQuenMatKhau(String toEmail, String hoTen, String maOtp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Tên hiển thị người gửi chuẩn thương hiệu DreamLeague (tránh bị lộ zodiac)
            helper.setFrom(fromEmail, "Hệ thống Quản lý Sân bóng DreamLeague");
            helper.setTo(toEmail);
            helper.setSubject("[DreamLeague] Mã xác thực đặt lại mật khẩu của bạn");

            String htmlContent = taoNoiDungEmailOtp(hoTen, maOtp);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("AuthenticationFailedException") || msg.contains("534-5.7.9") || msg.contains("535-5.7.8") || msg.contains("Authentication failed")) {
                throw new RuntimeException("Lỗi xác thực Gmail gửi mã: Mật khẩu ứng dụng (App Password) chưa đúng hoặc tài khoản Google chưa bật Xác minh 2 bước.");
            }
            throw new RuntimeException("Không thể gửi email xác thực: " + e.getMessage(), e);
        }
    }

    private String taoNoiDungEmailOtp(String hoTen, String maOtp) {
        String tenNguoiDung = (hoTen != null && !hoTen.trim().isEmpty()) ? hoTen : "Quý khách";
        String template = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        background-color: #f4f7f6;
                        margin: 0;
                        padding: 0;
                        color: #1e293b;
                    }
                    .container {
                        max-width: 580px;
                        margin: 30px auto;
                        background: #ffffff;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 10px 25px rgba(10, 37, 64, 0.08);
                        border: 1px solid #e2e8f0;
                    }
                    .header {
                        background: linear-gradient(135deg, #0a2540 0%, #163e6b 100%);
                        padding: 32px 24px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        color: #ffffff;
                        font-size: 24px;
                        letter-spacing: 0.5px;
                    }
                    .header h1 span {
                        color: #3fb454;
                    }
                    .header p {
                        margin: 6px 0 0 0;
                        color: #94a3b8;
                        font-size: 13px;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                    }
                    .body {
                        padding: 36px 32px;
                    }
                    .greeting {
                        font-size: 18px;
                        font-weight: 600;
                        color: #0a2540;
                        margin-bottom: 12px;
                    }
                    .message {
                        font-size: 15px;
                        line-height: 1.6;
                        color: #475569;
                        margin-bottom: 24px;
                    }
                    .otp-box {
                        background: #f0fdf4;
                        border: 2px dashed #3fb454;
                        border-radius: 12px;
                        padding: 24px;
                        text-align: center;
                        margin: 28px 0;
                    }
                    .otp-label {
                        font-size: 13px;
                        font-weight: 600;
                        color: #166534;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-bottom: 8px;
                    }
                    .otp-code {
                        font-family: 'Courier New', Courier, monospace;
                        font-size: 36px;
                        font-weight: 800;
                        letter-spacing: 10px;
                        color: #15803d;
                        margin: 8px 0;
                    }
                    .otp-expiry {
                        font-size: 12.5px;
                        color: #64748b;
                        margin-top: 6px;
                    }
                    .warning {
                        background-color: #fffbeb;
                        border-left: 4px solid #f59e0b;
                        padding: 12px 16px;
                        border-radius: 6px;
                        font-size: 13.5px;
                        color: #92400e;
                        margin-top: 24px;
                        line-height: 1.5;
                    }
                    .footer {
                        background-color: #f8fafc;
                        padding: 20px 32px;
                        text-align: center;
                        font-size: 12px;
                        color: #94a3b8;
                        border-top: 1px solid #f1f5f9;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⚽ <span>Dream</span>League</h1>
                        <p>Hệ thống Quản lý Sân bóng Chuyên nghiệp</p>
                    </div>
                    <div class="body">
                        <div class="greeting">Xin chào {{hoTen}},</div>
                        <div class="message">
                            Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản DreamLeague của bạn.
                            Vui lòng sử dụng mã xác thực bên dưới để hoàn tất việc cài đặt lại mật khẩu mới:
                        </div>
                        
                        <div class="otp-box">
                            <div class="otp-label">Mã xác thực của bạn là</div>
                            <div class="otp-code">{{maOtp}}</div>
                            <div class="otp-expiry">⏱️ Mã có hiệu lực trong vòng <strong>5 phút</strong></div>
                        </div>

                        <div class="warning">
                            🔒 <strong>Lưu ý bảo mật:</strong> Tuyệt đối không chia sẻ mã xác thực này cho bất kỳ ai. Nhân viên DreamLeague sẽ không bao giờ yêu cầu bạn cung cấp mã OTP.
                        </div>

                        <div class="message" style="margin-top: 20px; font-size: 13.5px; color: #64748b;">
                            Nếu bạn không gửi yêu cầu này, hãy bỏ qua email này để đảm bảo tài khoản của bạn vẫn an toàn.
                        </div>
                    </div>
                    <div class="footer">
                        © 2026 Hệ thống Quản lý Sân bóng DreamLeague. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """;

        return template
                .replace("{{hoTen}}", tenNguoiDung)
                .replace("{{maOtp}}", maOtp);
    }
}