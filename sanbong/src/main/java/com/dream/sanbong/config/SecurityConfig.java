package com.dream.sanbong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            // 1. Cho phép preflight request (OPTIONS)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // 2. MỞ KHÓA API TĂNG VIEW (Bất kể có Token hay không, Role gì cũng cho qua)
            .requestMatchers(HttpMethod.PUT, "/api/posts/{id}/view").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/posts/*/view").permitAll()

            // 3. Mở khóa xem bài viết
            .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/danh-gia/san/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/san-bong/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/dat-san/da-dat").permitAll()
            // Thông báo voucher là dữ liệu công khai: khách chưa đăng nhập vẫn xem được
            .requestMatchers(HttpMethod.GET, "/api/voucher/dang-hoat-dong").permitAll()
            .requestMatchers("/api/xac-thuc/**").permitAll()
            .requestMatchers("/api/sepay/webhook").permitAll()

            // 4. Quản lý bài viết (Tạo, Sửa, Xóa) cho STAFF và ADMIN (Giống phần Staff đã fix)
            .requestMatchers(HttpMethod.POST, "/api/posts/**").hasAnyRole("STAFF", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("STAFF", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("STAFF", "ADMIN")

            // 5. Các request còn lại
            .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}