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