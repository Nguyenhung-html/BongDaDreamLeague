CREATE DATABASE QuanLySanBong;
GO

USE QuanLySanBong;
GO

--Bảng User-- 
CREATE TABLE USERS(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    ho_ten NVARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    dia_chi NVARCHAR(255),
    vai_tro VARCHAR(20) NOT NULL, -- USER/STAFF/ADMIN
    trang_thai BIT DEFAULT 1,
    ngay_tao DATETIME DEFAULT GETDATE()
);

--Bảng Sân Bóng--

CREATE TABLE SAN_BONG(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    ten_san NVARCHAR(100) NOT NULL,
    loai_san INT NOT NULL, -- 5 hoặc 7
    mo_ta NVARCHAR(MAX),
    dia_chi NVARCHAR(255) NOT NULL,
    vi_do DECIMAL(10,8),
    kinh_do DECIMAL(11,8),
    hinh_anh VARCHAR(500),
    trang_thai VARCHAR(20) DEFAULT 'HOAT_DONG'
);

--Bảng Giá sân--
CREATE TABLE GIA_SAN(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    gia_tien DECIMAL(10,0) NOT NULL,
    CONSTRAINT FK_GIA_SAN_SAN_BONG
        FOREIGN KEY(san_bong_id)
        REFERENCES SAN_BONG(id)
);

--Bảng Đặt sân--
CREATE TABLE DAT_SAN(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    ho_ten_dat NVARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL,
    ngay_da DATE NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    tong_tien DECIMAL(10,0) NOT NULL,
    tien_coc DECIMAL(10,0) DEFAULT 0,
    trang_thai VARCHAR(30) DEFAULT 'CHO_XAC_NHAN',
    ngay_dat DATETIME DEFAULT GETDATE(),
    ngay_huy DATETIME NULL,
    CONSTRAINT FK_DAT_SAN_USER
        FOREIGN KEY(nguoi_dung_id)
        REFERENCES USERS(id),
    CONSTRAINT FK_DAT_SAN_SAN_BONG
        FOREIGN KEY(san_bong_id)
        REFERENCES SAN_BONG(id)
);

--Bảng Thanh toán--
CREATE TABLE THANH_TOAN(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    dat_san_id UNIQUEIDENTIFIER NOT NULL,
    so_tien DECIMAL(10,0) NOT NULL,
    loai VARCHAR(20) NOT NULL,
    -- COC
    -- THANH_TOAN_CON_LAI
    phuong_thuc VARCHAR(20) NOT NULL,
    -- QR
    -- TIEN_MAT
    ma_giao_dich VARCHAR(100),
    ngay_thanh_toan DATETIME DEFAULT GETDATE(),
    trang_thai VARCHAR(30) DEFAULT 'THANH_CONG',
    CONSTRAINT FK_THANH_TOAN_DAT_SAN
        FOREIGN KEY(dat_san_id)
        REFERENCES DAT_SAN(id)
);

--Bảng Bảo trì sân--
CREATE TABLE BAO_TRI_SAN(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    ly_do NVARCHAR(255),
    trang_thai VARCHAR(20),
    CONSTRAINT FK_BAO_TRI_SAN
        FOREIGN KEY(san_bong_id)
        REFERENCES SAN_BONG(id)
);

--Bảng Đánh giá--
CREATE TABLE DANH_GIA(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    so_sao INT NOT NULL
        CHECK(so_sao BETWEEN 1 AND 5),
    noi_dung NVARCHAR(MAX),
    hien_thi BIT DEFAULT 1,
    ngay_danh_gia DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_DANH_GIA_USER
        FOREIGN KEY(nguoi_dung_id)
        REFERENCES USERS(id),
    CONSTRAINT FK_DANH_GIA_SAN
        FOREIGN KEY(san_bong_id)
        REFERENCES SAN_BONG(id)
);

--Bảng thông báo--
CREATE TABLE THONG_BAO(
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    tieu_de NVARCHAR(200) NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    loai VARCHAR(30),
    da_doc BIT DEFAULT 0,
    ngay_tao DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_THONG_BAO_USER
        FOREIGN KEY(nguoi_dung_id)
        REFERENCES USERS(id)
);


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
(@idA1, '06:00', '07:00', 25000),
(@idA1, '07:00', '08:00', 25000),
(@idA1, '08:00', '09:00', 28000),
(@idA1, '09:00', '10:00', 28000),
(@idA1, '10:00', '11:00', 28000),
(@idA1, '11:00', '12:00', 30000),
(@idA1, '12:00', '13:00', 30000),
(@idA1, '13:00', '14:00', 30000),
(@idA1, '14:00', '15:00', 32000),
(@idA1, '15:00', '16:00', 32000),
(@idA1, '16:00', '17:00', 33000),
(@idA1, '17:00', '18:00', 38000),
(@idA1, '18:00', '19:00', 40000),
(@idA1, '19:00', '20:00', 40000),
(@idA1, '20:00', '21:00', 38000),
(@idA1, '21:00', '22:00', 35000);

-- === Sân A2 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA2, '06:00', '07:00', 25000),
(@idA2, '07:00', '08:00', 25000),
(@idA2, '08:00', '09:00', 28000),
(@idA2, '09:00', '10:00', 28000),
(@idA2, '10:00', '11:00', 28000),
(@idA2, '11:00', '12:00', 30000),
(@idA2, '12:00', '13:00', 30000),
(@idA2, '13:00', '14:00', 30000),
(@idA2, '14:00', '15:00', 32000),
(@idA2, '15:00', '16:00', 32000),
(@idA2, '16:00', '17:00', 33000),
(@idA2, '17:00', '18:00', 37000),
(@idA2, '18:00', '19:00', 39000),
(@idA2, '19:00', '20:00', 39000),
(@idA2, '20:00', '21:00', 37000),
(@idA2, '21:00', '22:00', 34000);

-- === Sân A3 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA3, '06:00', '07:00', 22000),
(@idA3, '07:00', '08:00', 22000),
(@idA3, '08:00', '09:00', 25000),
(@idA3, '09:00', '10:00', 25000),
(@idA3, '10:00', '11:00', 25000),
(@idA3, '11:00', '12:00', 27000),
(@idA3, '12:00', '13:00', 27000),
(@idA3, '13:00', '14:00', 27000),
(@idA3, '14:00', '15:00', 29000),
(@idA3, '15:00', '16:00', 29000),
(@idA3, '16:00', '17:00', 30000),
(@idA3, '17:00', '18:00', 35000),
(@idA3, '18:00', '19:00', 37000),
(@idA3, '19:00', '20:00', 37000),
(@idA3, '20:00', '21:00', 35000),
(@idA3, '21:00', '22:00', 32000);

-- === Sân A4 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idA4, '06:00', '07:00', 26000),
(@idA4, '07:00', '08:00', 26000),
(@idA4, '08:00', '09:00', 29000),
(@idA4, '09:00', '10:00', 29000),
(@idA4, '10:00', '11:00', 29000),
(@idA4, '11:00', '12:00', 31000),
(@idA4, '12:00', '13:00', 31000),
(@idA4, '13:00', '14:00', 31000),
(@idA4, '14:00', '15:00', 33000),
(@idA4, '15:00', '16:00', 33000),
(@idA4, '16:00', '17:00', 34000),
(@idA4, '17:00', '18:00', 39000),
(@idA4, '18:00', '19:00', 41000),
(@idA4, '19:00', '20:00', 41000),
(@idA4, '20:00', '21:00', 39000),
(@idA4, '21:00', '22:00', 36000);

-- === Sân B1 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB1, '06:00', '07:00', 50000),
(@idB1, '07:00', '08:00', 50000),
(@idB1, '08:00', '09:00', 55000),
(@idB1, '09:00', '10:00', 55000),
(@idB1, '10:00', '11:00', 55000),
(@idB1, '11:00', '12:00', 58000),
(@idB1, '12:00', '13:00', 58000),
(@idB1, '13:00', '14:00', 58000),
(@idB1, '14:00', '15:00', 62000),
(@idB1, '15:00', '16:00', 62000),
(@idB1, '16:00', '17:00', 65000),
(@idB1, '17:00', '18:00', 75000),
(@idB1, '18:00', '19:00', 80000),
(@idB1, '19:00', '20:00', 80000),
(@idB1, '20:00', '21:00', 75000),
(@idB1, '21:00', '22:00', 70000);

-- === Sân B2 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB2, '06:00', '07:00', 48000),
(@idB2, '07:00', '08:00', 48000),
(@idB2, '08:00', '09:00', 53000),
(@idB2, '09:00', '10:00', 53000),
(@idB2, '10:00', '11:00', 53000),
(@idB2, '11:00', '12:00', 56000),
(@idB2, '12:00', '13:00', 56000),
(@idB2, '13:00', '14:00', 56000),
(@idB2, '14:00', '15:00', 60000),
(@idB2, '15:00', '16:00', 60000),
(@idB2, '16:00', '17:00', 63000),
(@idB2, '17:00', '18:00', 72000),
(@idB2, '18:00', '19:00', 77000),
(@idB2, '19:00', '20:00', 77000),
(@idB2, '20:00', '21:00', 72000),
(@idB2, '21:00', '22:00', 68000);

-- === Sân B3 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB3, '06:00', '07:00', 46000),
(@idB3, '07:00', '08:00', 46000),
(@idB3, '08:00', '09:00', 51000),
(@idB3, '09:00', '10:00', 51000),
(@idB3, '10:00', '11:00', 51000),
(@idB3, '11:00', '12:00', 54000),
(@idB3, '12:00', '13:00', 54000),
(@idB3, '13:00', '14:00', 54000),
(@idB3, '14:00', '15:00', 58000),
(@idB3, '15:00', '16:00', 58000),
(@idB3, '16:00', '17:00', 60000),
(@idB3, '17:00', '18:00', 69000),
(@idB3, '18:00', '19:00', 74000),
(@idB3, '19:00', '20:00', 74000),
(@idB3, '20:00', '21:00', 69000),
(@idB3, '21:00', '22:00', 65000);

-- === Sân B4 ===
INSERT INTO GIA_SAN (san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien) VALUES
(@idB4, '06:00', '07:00', 52000),
(@idB4, '07:00', '08:00', 52000),
(@idB4, '08:00', '09:00', 57000),
(@idB4, '09:00', '10:00', 57000),
(@idB4, '10:00', '11:00', 57000),
(@idB4, '11:00', '12:00', 60000),
(@idB4, '12:00', '13:00', 60000),
(@idB4, '13:00', '14:00', 60000),
(@idB4, '14:00', '15:00', 64000),
(@idB4, '15:00', '16:00', 64000),
(@idB4, '16:00', '17:00', 67000),
(@idB4, '17:00', '18:00', 77000),
(@idB4, '18:00', '19:00', 82000),
(@idB4, '19:00', '20:00', 82000),
(@idB4, '20:00', '21:00', 77000),
(@idB4, '21:00', '22:00', 72000);

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