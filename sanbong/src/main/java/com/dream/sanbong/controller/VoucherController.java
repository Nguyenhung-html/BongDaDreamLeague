package com.dream.sanbong.controller;
import com.dream.sanbong.config.JwtUtils; import com.dream.sanbong.dto.*; import com.dream.sanbong.service.VoucherService; import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/voucher") @CrossOrigin(origins="*")
public class VoucherController {
 private final VoucherService service; private final JwtUtils jwt;
 public VoucherController(VoucherService service,JwtUtils jwt){this.service=service;this.jwt=jwt;}
 private String email(HttpServletRequest r){String h=r.getHeader("Authorization");if(h==null||!h.startsWith("Bearer "))throw new RuntimeException("Vui lòng đăng nhập!");return jwt.getEmailFromToken(h.substring(7));}
 private void admin(HttpServletRequest r){String h=r.getHeader("Authorization");if(h==null||!h.startsWith("Bearer ")||!"ADMIN".equals(jwt.getVaiTroFromToken(h.substring(7))))throw new RuntimeException("Bạn không có quyền Admin!");}
 @GetMapping("/admin") public ResponseEntity<?> all(HttpServletRequest r){try{admin(r);return ResponseEntity.ok(service.layTatCa());}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @GetMapping("/dang-hoat-dong") public ResponseEntity<?> dangHoatDong(){
  try{return ResponseEntity.ok(service.layVoucherDangHoatDong());}
  catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
 }
 @PostMapping("/admin") public ResponseEntity<?> add(@RequestBody VoucherYeuCau y,HttpServletRequest r){try{admin(r);return ResponseEntity.ok(service.them(y));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @PutMapping("/admin/{id}") public ResponseEntity<?> edit(@PathVariable UUID id,@RequestBody VoucherYeuCau y,HttpServletRequest r){try{admin(r);return ResponseEntity.ok(service.sua(id,y));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @DeleteMapping("/admin/{id}") public ResponseEntity<?> del(@PathVariable UUID id,HttpServletRequest r){try{admin(r);service.xoa(id);return ResponseEntity.ok(Map.of("message","Đã xoá voucher!"));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
 @PostMapping("/kiem-tra") public ResponseEntity<?> check(@RequestBody VoucherKiemTraYeuCau y,HttpServletRequest r){try{return ResponseEntity.ok(service.kiemTra(y.getMa(),y.getDonHang(),email(r)));}catch(Exception e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}}
}
