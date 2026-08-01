package com.dream.sanbong;

import com.dream.sanbong.entity.NguoiDung;
import com.dream.sanbong.repository.NguoiDungRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class SanbongApplication {

    public static void main(String[] args) {
        SpringApplication.run(SanbongApplication.class, args);
    }

    // ĐOẠN CODE NÀY SẼ CHẠY NGẦM 1 LẦN KHI BẬT SERVER ĐỂ FIX MẬT KHẨU
    @Bean
    public CommandLineRunner fixMatKhauChoTaiKhoanMau(NguoiDungRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // Băm mật khẩu 123456 bằng chuẩn BCrypt của Spring Boot
            String passChuan = encoder.encode("123456");

            // Danh sách 3 tài khoản bạn đã insert trong SQL
            String[] danhSachEmail = {"admin@gmail.com", "staff@gmail.com", "user@gmail.com"};
            
            for (String email : danhSachEmail) {
                Optional<NguoiDung> opt = repo.findByEmail(email);
                if (opt.isPresent()) {
                    NguoiDung nd = opt.get();
                    nd.setMatKhau(passChuan); // Cập nhật mật khẩu chuẩn
                    repo.save(nd); // Lưu đè xuống database
                    System.out.println("======> Welcome: " + email);
                } else {
                    System.out.println("======> KHÔNG TÌM THẤY TÀI KHOẢN: " + email + " (Hãy check lại SQL Server xem đã insert chưa)");
                }
            }
        };
    }
}