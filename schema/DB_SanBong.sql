-- Tất cả 3 tài khoản đều có chung mật khẩu đăng nhập là: 123456
-- Chuỗi '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e' chính là 123456 đã được mã hóa BCrypt

INSERT INTO USERS (ho_ten, so_dien_thoai, email, mat_khau, dia_chi, vai_tro, trang_thai)
VALUES 
(N'Nguyễn Tiến Hùng ', '0901234598', 'admin@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hà Nội', 'ADMIN', 1),
(N'Trần Nhân Viên', '0912345678', 'staff@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Đà Nẵng', 'STAFF', 1),
(N'Lê Khách Hàng', '0923456789', 'user@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hồ Chí Minh', 'USER', 1);

select * from Users; 
select * from SAN_BONG

ALTER TABLE DAT_SAN

ALTER COLUMN nguoi_dung_id UNIQUEIDENTIFIER NULL;

ALTER TABLE DAT_SAN
ADD kenh_dat VARCHAR(20) DEFAULT 'ONLINE';

ALTER TABLE DAT_SAN
ADD phuong_thuc_coc VARCHAR(20) NULL;

ALTER TABLE DAT_SAN
ADD ghi_chu NVARCHAR(500) NULL;

INSERT INTO SAN_BONG (id, ten_san, loai_san, mo_ta, dia_chi, vi_do, kinh_do, hinh_anh, trang_thai)
VALUES
-- Sân 5 người
(
    '11111111-1111-1111-1111-111111111111',
    N'Sân A1',
    5,
    N'Sân cỏ nhân tạo thế hệ mới, hệ thống đèn LED chiếu sáng ban đêm, có mái che chống mưa.',
    N'Hẻm 104 Tân Sơn, Phường 15, Quận Tân Bình, TP.HCM',
    10.80123456,
    106.65234567,
    'san_a1.jpg',
    'HOAT_DONG'
),
(
    '22222222-2222-2222-2222-222222222222',
    N'Sân A2',
    5,
    N'Sân cỏ nhân tạo, khu vực gửi xe rộng rãi, có phòng thay đồ.',
    N'Hẻm 104 Tân Sơn, Phường 15, Quận Tân Bình, TP.HCM',
    10.80134567,
    106.65245678,
    'san_a2.jpg',
    'HOAT_DONG'
),
(
    '33333333-3333-3333-3333-333333333333',
    N'Sân A3',
    5,
    N'Sân cỏ nhân tạo cao cấp, view đẹp, thoáng mát.',
    N'Hẻm 104 Tân Sơn, Phường 15, Quận Tân Bình, TP.HCM',
    10.80145678,
    106.65256789,
    'san_a3.jpg',
    'BAO_TRI'
),

-- Sân 7 người
(
    '44444444-4444-4444-4444-444444444444',
    N'Sân B1',
    7,
    N'Sân 7 người tiêu chuẩn FIFA, cỏ nhân tạo 50mm, hệ thống tưới tự động.',
    N'Hẻm 104 Tân Sơn, Phường 15, Quận Tân Bình, TP.HCM',
    10.80156789,
    106.65267890,
    'san_b1.jpg',
    'HOAT_DONG'
),
(
    '55555555-5555-5555-5555-555555555555',
    N'Sân B2',
    7,
    N'Sân 7 người rộng rãi, có khán đài mini, phù hợp tổ chức giải đấu.',
    N'Hẻm 104 Tân Sơn, Phường 15, Quận Tân Bình, TP.HCM',
    10.80167890,
    106.65278901,
    'san_b2.jpg',
    'HOAT_DONG'
);
GO

-- ===== GIÁ SÂN =====
-- Sân 5 người: sáng sớm 250k, chiều 280k, tối 300k
-- Sân 7 người: sáng sớm 500k, chiều 550k, tối 600k

INSERT INTO GIA_SAN (id, san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien)
VALUES
-- Sân A1 (san5)
(NEWID(), '11111111-1111-1111-1111-111111111111', '06:00', '07:00', 250000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '07:00', '08:00', 250000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '08:00', '09:00', 250000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '16:00', '17:00', 280000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '17:00', '18:00', 280000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '18:00', '19:00', 300000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '19:00', '20:00', 300000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '20:00', '21:00', 300000),
(NEWID(), '11111111-1111-1111-1111-111111111111', '21:00', '22:00', 300000),

-- Sân A2 (san5)
(NEWID(), '22222222-2222-2222-2222-222222222222', '06:00', '07:00', 250000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '07:00', '08:00', 250000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '08:00', '09:00', 250000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '16:00', '17:00', 280000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '17:00', '18:00', 280000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '18:00', '19:00', 300000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '19:00', '20:00', 300000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '20:00', '21:00', 300000),
(NEWID(), '22222222-2222-2222-2222-222222222222', '21:00', '22:00', 300000),

-- Sân A3 (san5 - đang bảo trì, vẫn có giá sẵn)
(NEWID(), '33333333-3333-3333-3333-333333333333', '06:00', '07:00', 250000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '07:00', '08:00', 250000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '08:00', '09:00', 250000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '16:00', '17:00', 280000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '17:00', '18:00', 280000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '18:00', '19:00', 300000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '19:00', '20:00', 300000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '20:00', '21:00', 300000),
(NEWID(), '33333333-3333-3333-3333-333333333333', '21:00', '22:00', 300000),

-- Sân B1 (san7)
(NEWID(), '44444444-4444-4444-4444-444444444444', '06:00', '07:00', 500000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '07:00', '08:00', 500000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '08:00', '09:00', 500000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '16:00', '17:00', 550000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '17:00', '18:00', 550000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '18:00', '19:00', 600000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '19:00', '20:00', 600000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '20:00', '21:00', 600000),
(NEWID(), '44444444-4444-4444-4444-444444444444', '21:00', '22:00', 600000),

-- Sân B2 (san7)
(NEWID(), '55555555-5555-5555-5555-555555555555', '06:00', '07:00', 500000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '07:00', '08:00', 500000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '08:00', '09:00', 500000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '16:00', '17:00', 550000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '17:00', '18:00', 550000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '18:00', '19:00', 600000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '19:00', '20:00', 600000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '20:00', '21:00', 600000),
(NEWID(), '55555555-5555-5555-5555-555555555555', '21:00', '22:00', 600000);
GO
-- Tất cả 3 tài khoản đều có chung mật khẩu đăng nhập là: 123456
-- Chuỗi '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e' chính là 123456 đã được mã hóa BCrypt


-- ================================================================
-- DỮ LIỆU MẪU SÂN BÓNG
-- Hẻm 104 Tân Sơn, Phường Tân Sơn, Quận 12, TP. Hồ Chí Minh
-- ================================================================

-- Dùng biến để lưu ID các sân → gán vào GIA_SAN
DECLARE @idA1 UNIQUEIDENTIFIER = NEWID();
DECLARE @idA2 UNIQUEIDENTIFIER = NEWID();
DECLARE @idA3 UNIQUEIDENTIFIER = NEWID();
DECLARE @idA4 UNIQUEIDENTIFIER = NEWID();
DECLARE @idB1 UNIQUEIDENTIFIER = NEWID();
DECLARE @idB2 UNIQUEIDENTIFIER = NEWID();
DECLARE @idB3 UNIQUEIDENTIFIER = NEWID();
DECLARE @idB4 UNIQUEIDENTIFIER = NEWID();

-- ============ SAN_BONG ============
INSERT INTO SAN_BONG (id, ten_san, loai_san, mo_ta, dia_chi, vi_do, kinh_do, hinh_anh, trang_thai)
VALUES
-- Sân 5 người (A1 - A4)
(
  @idA1,
  N'Sân A1 – 5 người',
  5,
  N'Sân cỏ nhân tạo thế hệ 3, hệ thống đèn LED cao áp chiếu sáng toàn bộ mặt sân. Diện tích 25×16m, phù hợp thi đấu 5 người.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86872000, 106.62510000,
  'https://images.unsplash.com/photo-1574629810360-7efbbe195018',
  'HOAT_DONG'
),
(
  @idA2,
  N'Sân A2 – 5 người',
  5,
  N'Sân cỏ nhân tạo chuẩn FIFA, khung thành inox chắc chắn, hệ thống chiếu sáng hiện đại, không gian thoáng mát.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86875000, 106.62515000,
  'https://images.unsplash.com/photo-1551854838-212c9a5a049a',
  'HOAT_DONG'
),
(
  @idA3,
  N'Sân A3 – 5 người',
  5,
  N'Sân cỏ nhân tạo chuyên dụng, mặt sân phẳng mịn, hệ thống thoát nước tốt, có mái che một phần bảo vệ khỏi nắng.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86878000, 106.62520000,
  'https://images.unsplash.com/photo-1553778263-73a83bab9b0c',
  'HOAT_DONG'
),
(
  @idA4,
  N'Sân A4 – 5 người',
  5,
  N'Sân cỏ nhân tạo cao cấp, có phòng thay đồ riêng, hệ thống âm thanh trong sân, bãi giữ xe rộng rãi miễn phí.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86881000, 106.62525000,
  'https://images.unsplash.com/photo-1529900748604-07564a03e7a6',
  'HOAT_DONG'
),
-- Sân 7 người (B1 - B4)
(
  @idB1,
  N'Sân B1 – 7 người',
  7,
  N'Sân cỏ nhân tạo 7 người rộng 50×30m, đèn LED công suất cao, khán đài nhỏ cho người xem, phù hợp tổ chức giải đấu.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86860000, 106.62490000,
  'https://images.unsplash.com/photo-1540747913346-19e32dc3e97e',
  'HOAT_DONG'
),
(
  @idB2,
  N'Sân B2 – 7 người',
  7,
  N'Sân cỏ nhân tạo tiêu chuẩn thi đấu 7 người, hệ thống đèn chiếu sáng đêm 4 cột, phòng thay đồ đầy đủ.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86857000, 106.62485000,
  'https://images.unsplash.com/photo-1459865264687-595d652de67e',
  'HOAT_DONG'
),
(
  @idB3,
  N'Sân B3 – 7 người',
  7,
  N'Sân cỏ nhân tạo thế hệ mới, cỏ dài 50mm cảm giác tự nhiên, hệ thống tưới nước tự động, giá thành hợp lý.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86854000, 106.62480000,
  'https://images.unsplash.com/photo-1522778119026-d647f0596c20',
  'HOAT_DONG'
),
(
  @idB4,
  N'Sân B4 – 7 người',
  7,
  N'Sân 7 người cao cấp nhất khu phức hợp, có bảng điện tử tính giờ, âm thanh hiện đại, khu VIP khán đài có mái che.',
  N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',
  10.86851000, 106.62475000,
  'https://images.unsplash.com/photo-1431324155629-1a6dda1505c9',
  'HOAT_DONG'
);

-- ============ GIA_SAN ============
-- Khung giờ sáng (06:00 - 11:00): giá thấp hơn
-- Khung giờ chiều (11:00 - 17:00): giá trung bình
-- Khung giờ tối  (17:00 - 22:00): giá cao nhất (giờ vàng)

-- === Sân A1 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA1, '06:00', '07:00', 250000),
(@idA1, '07:00', '08:00', 250000),
(@idA1, '08:00', '09:00', 280000),
(@idA1, '09:00', '10:00', 280000),
(@idA1, '10:00', '11:00', 280000),
(@idA1, '11:00', '12:00', 300000),
(@idA1, '12:00', '13:00', 300000),
(@idA1, '13:00', '14:00', 300000),
(@idA1, '14:00', '15:00', 320000),
(@idA1, '15:00', '16:00', 320000),
(@idA1, '16:00', '17:00', 330000),
(@idA1, '17:00', '18:00', 380000),
(@idA1, '18:00', '19:00', 400000),
(@idA1, '19:00', '20:00', 400000),
(@idA1, '20:00', '21:00', 380000),
(@idA1, '21:00', '22:00', 350000);

-- === Sân A2 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA2, '06:00', '07:00', 250000),
(@idA2, '07:00', '08:00', 250000),
(@idA2, '08:00', '09:00', 280000),
(@idA2, '09:00', '10:00', 280000),
(@idA2, '10:00', '11:00', 280000),
(@idA2, '11:00', '12:00', 300000),
(@idA2, '12:00', '13:00', 300000),
(@idA2, '13:00', '14:00', 300000),
(@idA2, '14:00', '15:00', 320000),
(@idA2, '15:00', '16:00', 320000),
(@idA2, '16:00', '17:00', 330000),
(@idA2, '17:00', '18:00', 370000),
(@idA2, '18:00', '19:00', 390000),
(@idA2, '19:00', '20:00', 390000),
(@idA2, '20:00', '21:00', 370000),
(@idA2, '21:00', '22:00', 340000);

-- === Sân A3 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA3, '06:00', '07:00', 220000),
(@idA3, '07:00', '08:00', 220000),
(@idA3, '08:00', '09:00', 250000),
(@idA3, '09:00', '10:00', 250000),
(@idA3, '10:00', '11:00', 250000),
(@idA3, '11:00', '12:00', 270000),
(@idA3, '12:00', '13:00', 270000),
(@idA3, '13:00', '14:00', 270000),
(@idA3, '14:00', '15:00', 290000),
(@idA3, '15:00', '16:00', 290000),
(@idA3, '16:00', '17:00', 300000),
(@idA3, '17:00', '18:00', 350000),
(@idA3, '18:00', '19:00', 370000),
(@idA3, '19:00', '20:00', 370000),
(@idA3, '20:00', '21:00', 350000),
(@idA3, '21:00', '22:00', 320000);

-- === Sân A4 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA4, '06:00', '07:00', 260000),
(@idA4, '07:00', '08:00', 260000),
(@idA4, '08:00', '09:00', 290000),
(@idA4, '09:00', '10:00', 290000),
(@idA4, '10:00', '11:00', 290000),
(@idA4, '11:00', '12:00', 310000),
(@idA4, '12:00', '13:00', 310000),
(@idA4, '13:00', '14:00', 310000),
(@idA4, '14:00', '15:00', 330000),
(@idA4, '15:00', '16:00', 330000),
(@idA4, '16:00', '17:00', 340000),
(@idA4, '17:00', '18:00', 390000),
(@idA4, '18:00', '19:00', 410000),
(@idA4, '19:00', '20:00', 410000),
(@idA4, '20:00', '21:00', 390000),
(@idA4, '21:00', '22:00', 360000);

-- === Sân B1 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB1, '06:00', '07:00', 500000),
(@idB1, '07:00', '08:00', 500000),
(@idB1, '08:00', '09:00', 550000),
(@idB1, '09:00', '10:00', 550000),
(@idB1, '10:00', '11:00', 550000),
(@idB1, '11:00', '12:00', 580000),
(@idB1, '12:00', '13:00', 580000),
(@idB1, '13:00', '14:00', 580000),
(@idB1, '14:00', '15:00', 620000),
(@idB1, '15:00', '16:00', 620000),
(@idB1, '16:00', '17:00', 650000),
(@idB1, '17:00', '18:00', 750000),
(@idB1, '18:00', '19:00', 800000),
(@idB1, '19:00', '20:00', 800000),
(@idB1, '20:00', '21:00', 750000),
(@idB1, '21:00', '22:00', 700000);

-- === Sân B2 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB2, '06:00', '07:00', 480000),
(@idB2, '07:00', '08:00', 480000),
(@idB2, '08:00', '09:00', 530000),
(@idB2, '09:00', '10:00', 530000),
(@idB2, '10:00', '11:00', 530000),
(@idB2, '11:00', '12:00', 560000),
(@idB2, '12:00', '13:00', 560000),
(@idB2, '13:00', '14:00', 560000),
(@idB2, '14:00', '15:00', 600000),
(@idB2, '15:00', '16:00', 600000),
(@idB2, '16:00', '17:00', 630000),
(@idB2, '17:00', '18:00', 720000),
(@idB2, '18:00', '19:00', 770000),
(@idB2, '19:00', '20:00', 770000),
(@idB2, '20:00', '21:00', 720000),
(@idB2, '21:00', '22:00', 680000);

-- === Sân B3 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB3, '06:00', '07:00', 460000),
(@idB3, '07:00', '08:00', 460000),
(@idB3, '08:00', '09:00', 510000),
(@idB3, '09:00', '10:00', 510000),
(@idB3, '10:00', '11:00', 510000),
(@idB3, '11:00', '12:00', 540000),
(@idB3, '12:00', '13:00', 540000),
(@idB3, '13:00', '14:00', 540000),
(@idB3, '14:00', '15:00', 580000),
(@idB3, '15:00', '16:00', 580000),
(@idB3, '16:00', '17:00', 600000),
(@idB3, '17:00', '18:00', 690000),
(@idB3, '18:00', '19:00', 740000),
(@idB3, '19:00', '20:00', 740000),
(@idB3, '20:00', '21:00', 690000),
(@idB3, '21:00', '22:00', 650000);

-- === Sân B4 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB4, '06:00', '07:00', 520000),
(@idB4, '07:00', '08:00', 520000),
(@idB4, '08:00', '09:00', 570000),
(@idB4, '09:00', '10:00', 570000),
(@idB4, '10:00', '11:00', 570000),
(@idB4, '11:00', '12:00', 600000),
(@idB4, '12:00', '13:00', 600000),
(@idB4, '13:00', '14:00', 600000),
(@idB4, '14:00', '15:00', 640000),
(@idB4, '15:00', '16:00', 640000),
(@idB4, '16:00', '17:00', 670000),
(@idB4, '17:00', '18:00', 770000),
(@idB4, '18:00', '19:00', 820000),
(@idB4, '19:00', '20:00', 820000),
(@idB4, '20:00', '21:00', 770000),
(@idB4, '21:00', '22:00', 720000);

ALTER TABLE THANH_TOAN
ADD ngay_hoan_tien DATETIME NULL;

-- 1. Thêm cột ngày hoàn tiền
ALTER TABLE THANH_TOAN
ADD ngay_hoan_tien DATETIME NULL;
-- 2. Xóa constraint mặc định cũ
ALTER TABLE THANH_TOAN
DROP CONSTRAINT DF__THANH_TOA__trang__4F7CD00D;
-- 3. Tạo lại constraint mặc định mới
ALTER TABLE THANH_TOAN
ADD CONSTRAINT DF_THANH_TOAN_TRANG_THAI
DEFAULT 'DANG_CHO' FOR trang_thai;

select * from GIA_SAN
select * from SAN_BONG;

-- Đổi tên sân 5 người thành A1, A2, A3, A4... theo thứ tự tạo
WITH SanA AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS stt
    FROM SAN_BONG
    WHERE loai_san = 5
)
UPDATE sb
SET sb.ten_san = N'Sân A' + CAST(a.stt AS VARCHAR) + N' – 5 người'
FROM SAN_BONG sb
JOIN SanA a ON sb.id = a.id;

-- Đổi tên sân 7 người thành B1, B2, B3, B4... theo thứ tự tạo
WITH SanB AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS stt
    FROM SAN_BONG
    WHERE loai_san = 7
)
UPDATE sb
SET sb.ten_san = N'Sân B' + CAST(b.stt AS VARCHAR) + N' – 7 người'
FROM SAN_BONG sb
JOIN SanB b ON sb.id = b.id;

-- Khung sáng (06:00 - 11:00): rẻ nhất
UPDATE GIA_SAN
SET gia_tien = 6000
WHERE gio_bat_dau >= '06:00' AND gio_bat_dau < '11:00';

-- Khung chiều (11:00 - 17:00): trung bình
UPDATE GIA_SAN
SET gia_tien = 9000
WHERE gio_bat_dau >= '11:00' AND gio_bat_dau < '17:00';

-- Khung tối (17:00 - 22:00): giờ vàng, đắt nhất nhưng vẫn dưới 15k
UPDATE GIA_SAN
SET gia_tien = 13000
WHERE gio_bat_dau >= '17:00' AND gio_bat_dau <= '22:00';