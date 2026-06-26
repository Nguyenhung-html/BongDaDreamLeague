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
(N'Nguyễn Văn Sơn', '0901234567', 'admin@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hà Nội', 'ADMIN', 1),
(N'Trần Nhân Viên', '0912345678', 'staff@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Đà Nẵng', 'STAFF', 1),
(N'Lê Khách Hàng', '0923456789', 'user@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hồ Chí Minh', 'USER', 1);

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