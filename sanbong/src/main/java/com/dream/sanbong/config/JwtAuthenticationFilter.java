package com.dream.sanbong.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
        // 1. Lấy chuỗi Authorization từ Header của request
        String authHeader = request.getHeader("Authorization");

        // 2. Kiểm tra xem Header có đúng chuẩn "Bearer <Token>" hay không
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Cắt bỏ chữ "Bearer " để lấy lõi Token

            // 3. Xác thực token xem có hợp lệ không
            if (jwtUtils.validateToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                String vaiTro = jwtUtils.getVaiTroFromToken(token);

                // 4. Nếu hợp lệ, tạo đối tượng xác thực để thông báo cho Spring Security cho phép đi qua
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + vaiTro);
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                
                // Lưu thông tin đăng nhập vào hệ thống chạy ngầm của luồng request này
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Cho phép request tiếp tục đi tới Controller
        filterChain.doFilter(request, response);
    }
}