package com.dream.sanbong.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Chuỗi bí mật dùng để mã hóa (phải dài tối thiểu 256-bit hay 32 ký tự)
    private final String SECRET_KEY = "ChuoiBiMatSieuCapVipProCuaDuAnQuanLySanBongChieuDaiPhaiDu";
    private final long EXPIRATION_TIME = 86400000; // Token có hiệu lực trong 1 ngày (tính bằng mili-giây)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Hàm tạo Token khi User đăng nhập thành công
    public String generateToken(String email, String vaiTro) {
        return Jwts.builder()
                .setSubject(email)
                .claim("vaiTro", vaiTro) // Nhét vai trò vào token để frontend/backend kiểm tra
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Hàm lấy Email từ chuỗi Token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Hàm lấy Vai Trò từ chuỗi Token
    public String getVaiTroFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("vaiTro", String.class);
    }

    // Hàm kiểm tra xem Token hợp lệ hay đã hết hạn
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
