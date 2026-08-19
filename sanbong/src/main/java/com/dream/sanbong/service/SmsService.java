package com.dream.sanbong.service;

public interface SmsService {
    /**
     * Gửi mã xác thực OTP qua SMS tới số điện thoại của người dùng.
     *
     * @param soDienThoai Số điện thoại nhận OTP (VD: 0987654321 hoặc +84987654321)
     * @param hoTen Tên người dùng nhận tin nhắn
     * @param maOtp Mã OTP 6 chữ số
     */
    void guiMaOtpQuenMatKhau(String soDienThoai, String hoTen, String maOtp);
}