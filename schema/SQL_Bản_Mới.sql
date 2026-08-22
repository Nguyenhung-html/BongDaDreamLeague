/* =====================================================================
   QUAN LY SAN BONG - DATABASE HOAN CHINH
   SQL Server / SSMS

   Muc tieu:
   - Chay file nay tren SQL Server de tao lai toan bo DB.
   - Chi co 8 san: A1-A4 (san 5), B1-B4 (san 7).
   - Gio dat san: 07:00 -> 22:00.
   - Khong tao trung SAN_BONG / GIA_SAN.
   - Constraint duoc dat ten ro rang, khong DROP constraint ten tu sinh.
   - Du lieu dashboard nam 2026 duoc tao sau cung.
   - Neu chay lai: file se xoa DB cu va tao lai tu dau.

   QUAN TRONG:
   - Stop Spring Boot/Java dang ket noi DB truoc khi chay file.
   - File nay dung DROP DATABASE neu DB QuanLySanBong da ton tai.
   ===================================================================== */
CREATE DATABASE [QuanLySanBong];
GO

USE [QuanLySanBong];
GO

/* =====================================================================
   PHAN B - CAU TRUC BANG
   ===================================================================== */

/* B1. USERS */
CREATE TABLE dbo.USERS(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_USERS PRIMARY KEY DEFAULT NEWID(),
    ho_ten NVARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL CONSTRAINT UQ_USERS_SDT UNIQUE,
    email VARCHAR(100) NOT NULL CONSTRAINT UQ_USERS_EMAIL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    dia_chi NVARCHAR(255) NULL,
    avatar NVARCHAR(MAX) NULL,
    vai_tro VARCHAR(20) NOT NULL,
    trang_thai BIT NOT NULL CONSTRAINT DF_USERS_TRANG_THAI DEFAULT 1,
    ngay_tao DATETIME NOT NULL CONSTRAINT DF_USERS_NGAY_TAO DEFAULT GETDATE(),
    CONSTRAINT CK_USERS_VAI_TRO CHECK (vai_tro IN ('USER','STAFF','ADMIN'))
);
GO

/* B2. SAN_BONG */
CREATE TABLE dbo.SAN_BONG(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_SAN_BONG PRIMARY KEY DEFAULT NEWID(),
    ten_san NVARCHAR(100) NOT NULL CONSTRAINT UQ_SAN_BONG_TEN UNIQUE,
    loai_san INT NOT NULL,
    mo_ta NVARCHAR(MAX) NULL,
    dia_chi NVARCHAR(255) NOT NULL,
    vi_do DECIMAL(10,8) NULL,
    kinh_do DECIMAL(11,8) NULL,
    hinh_anh VARCHAR(500) NULL,
    trang_thai VARCHAR(20) NOT NULL CONSTRAINT DF_SAN_BONG_TRANG_THAI DEFAULT 'HOAT_DONG',
    CONSTRAINT CK_SAN_BONG_LOAI CHECK (loai_san IN (5,7)),
    CONSTRAINT CK_SAN_BONG_TRANG_THAI CHECK (trang_thai IN ('HOAT_DONG','BAO_TRI','NGUNG_HOAT_DONG'))
);
GO

/* B3. GIA_SAN */
CREATE TABLE dbo.GIA_SAN(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_GIA_SAN PRIMARY KEY DEFAULT NEWID(),
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    gia_tien DECIMAL(10,0) NOT NULL,
    CONSTRAINT FK_GIA_SAN_SAN_BONG FOREIGN KEY(san_bong_id) REFERENCES dbo.SAN_BONG(id),
    CONSTRAINT CK_GIA_SAN_THOI_GIAN CHECK (gio_bat_dau < gio_ket_thuc),
    CONSTRAINT CK_GIA_SAN_GIO_HOAT_DONG CHECK (gio_bat_dau >= '07:00' AND gio_ket_thuc <= '22:00'),
    CONSTRAINT CK_GIA_SAN_GIA CHECK (gia_tien >= 0),
    CONSTRAINT UQ_GIA_SAN_KHUNG_GIO UNIQUE (san_bong_id, gio_bat_dau, gio_ket_thuc)
);
GO

/* B4. DAT_SAN */
CREATE TABLE dbo.DAT_SAN(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_DAT_SAN PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NULL,
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    ho_ten_dat NVARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(15) NOT NULL,
    ngay_da DATE NOT NULL,
    gio_bat_dau TIME NOT NULL,
    gio_ket_thuc TIME NOT NULL,
    tong_tien DECIMAL(18,2) NOT NULL,
    tien_coc DECIMAL(18,2) NOT NULL CONSTRAINT DF_DAT_SAN_TIEN_COC DEFAULT 0,
    trang_thai VARCHAR(30) NOT NULL CONSTRAINT DF_DAT_SAN_TRANG_THAI DEFAULT 'CHO_XAC_NHAN',
    ngay_dat DATETIME NOT NULL CONSTRAINT DF_DAT_SAN_NGAY_DAT DEFAULT GETDATE(),
    ngay_huy DATETIME NULL,
    kenh_dat VARCHAR(20) NOT NULL CONSTRAINT DF_DAT_SAN_KENH_DAT DEFAULT 'ONLINE',
    phuong_thuc_coc VARCHAR(20) NULL,
    ghi_chu NVARCHAR(500) NULL,
    expire_at DATETIME NULL,
    ip_address VARCHAR(45) NULL,
    voucher_id UNIQUEIDENTIFIER NULL,
    tien_giam_voucher DECIMAL(18,2) NOT NULL CONSTRAINT DF_DAT_SAN_TIEN_GIAM DEFAULT 0,

    CONSTRAINT FK_DAT_SAN_USER FOREIGN KEY(nguoi_dung_id) REFERENCES dbo.USERS(id),
    CONSTRAINT FK_DAT_SAN_SAN_BONG FOREIGN KEY(san_bong_id) REFERENCES dbo.SAN_BONG(id),
    CONSTRAINT CK_DAT_SAN_THOI_GIAN CHECK (
        gio_bat_dau < gio_ket_thuc
        AND gio_bat_dau >= '07:00'
        AND gio_ket_thuc <= '22:00'
    ),
    CONSTRAINT CK_DAT_SAN_TIEN CHECK (tong_tien >= 0 AND tien_coc >= 0 AND tien_giam_voucher >= 0),
    CONSTRAINT CK_DAT_SAN_KENH_DAT CHECK (kenh_dat IN ('ONLINE','TRUC_TIEP')),
    CONSTRAINT CK_DAT_SAN_TRANG_THAI CHECK (
        trang_thai IN ('CHO_XAC_NHAN','DA_COC','HOAN_THANH','DA_HUY','DANG_GIU_CHO')
    )
);
GO

/* B5. THANH_TOAN */
CREATE TABLE dbo.THANH_TOAN(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_THANH_TOAN PRIMARY KEY DEFAULT NEWID(),
    dat_san_id UNIQUEIDENTIFIER NOT NULL,
    so_tien DECIMAL(18,2) NOT NULL,
    loai VARCHAR(30) NOT NULL,
    phuong_thuc VARCHAR(20) NOT NULL,
    ma_giao_dich VARCHAR(100) NULL,
    ngay_thanh_toan DATETIME NOT NULL CONSTRAINT DF_THANH_TOAN_NGAY DEFAULT GETDATE(),
    trang_thai VARCHAR(30) NOT NULL CONSTRAINT DF_THANH_TOAN_TRANG_THAI DEFAULT 'DANG_CHO',
    ngay_hoan_tien DATETIME NULL,

    CONSTRAINT FK_THANH_TOAN_DAT_SAN FOREIGN KEY(dat_san_id) REFERENCES dbo.DAT_SAN(id),
    CONSTRAINT CK_THANH_TOAN_SO_TIEN CHECK (so_tien >= 0),
    CONSTRAINT CK_THANH_TOAN_LOAI CHECK (loai IN ('COC','THANH_TOAN_CON_LAI','HOAN_TIEN')),
    CONSTRAINT CK_THANH_TOAN_PHUONG_THUC CHECK (phuong_thuc IN ('TIEN_MAT','QR','VNPAY','CHUYEN_KHOAN')),
    CONSTRAINT CK_THANH_TOAN_TRANG_THAI CHECK (trang_thai IN ('DANG_CHO','THANH_CONG','THAT_BAI','DA_HOAN'))
);
GO

/* B6. BAO_TRI_SAN */
CREATE TABLE dbo.BAO_TRI_SAN(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_BAO_TRI_SAN PRIMARY KEY DEFAULT NEWID(),
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    ly_do NVARCHAR(255) NULL,
    trang_thai VARCHAR(20) NULL,
    CONSTRAINT FK_BAO_TRI_SAN_SAN FOREIGN KEY(san_bong_id) REFERENCES dbo.SAN_BONG(id),
    CONSTRAINT CK_BAO_TRI_THOI_GIAN CHECK (ngay_ket_thuc > ngay_bat_dau)
);
GO

/* B7. DANH_GIA */
CREATE TABLE dbo.DANH_GIA(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_DANH_GIA PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    san_bong_id UNIQUEIDENTIFIER NOT NULL,
    so_sao INT NOT NULL,
    noi_dung NVARCHAR(MAX) NULL,
    hien_thi BIT NOT NULL CONSTRAINT DF_DANH_GIA_HIEN_THI DEFAULT 1,
    ngay_danh_gia DATETIME NOT NULL CONSTRAINT DF_DANH_GIA_NGAY DEFAULT GETDATE(),
    CONSTRAINT FK_DANH_GIA_USER FOREIGN KEY(nguoi_dung_id) REFERENCES dbo.USERS(id),
    CONSTRAINT FK_DANH_GIA_SAN FOREIGN KEY(san_bong_id) REFERENCES dbo.SAN_BONG(id),
    CONSTRAINT CK_DANH_GIA_SO_SAO CHECK (so_sao BETWEEN 1 AND 5)
);
GO

/* B8. THONG_BAO */
CREATE TABLE dbo.THONG_BAO(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_THONG_BAO PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    tieu_de NVARCHAR(200) NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    loai VARCHAR(30) NULL,
    da_doc BIT NOT NULL CONSTRAINT DF_THONG_BAO_DA_DOC DEFAULT 0,
    ngay_tao DATETIME NOT NULL CONSTRAINT DF_THONG_BAO_NGAY DEFAULT GETDATE(),
    CONSTRAINT FK_THONG_BAO_USER FOREIGN KEY(nguoi_dung_id) REFERENCES dbo.USERS(id)
);
GO

/* B9. GIA_HAN_GIO */
CREATE TABLE dbo.GIA_HAN_GIO(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_GIA_HAN_GIO PRIMARY KEY DEFAULT NEWID(),
    dat_san_id UNIQUEIDENTIFIER NOT NULL,
    gio_ket_thuc_cu TIME NOT NULL,
    gio_ket_thuc_moi TIME NOT NULL,
    so_tien_them DECIMAL(18,2) NOT NULL,
    ngay_gia_han DATETIME NOT NULL CONSTRAINT DF_GIA_HAN_GIO_NGAY DEFAULT GETDATE(),
    CONSTRAINT FK_GIA_HAN_GIO_DAT_SAN FOREIGN KEY(dat_san_id) REFERENCES dbo.DAT_SAN(id),
    CONSTRAINT CK_GIA_HAN_GIO CHECK (gio_ket_thuc_moi > gio_ket_thuc_cu AND gio_ket_thuc_moi <= '22:00')
);
GO

/* B10. Posts */
CREATE TABLE dbo.Posts(
    id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Posts PRIMARY KEY,
    author_id UNIQUEIDENTIFIER NOT NULL,
    title NVARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL CONSTRAINT UQ_Posts_Slug UNIQUE,
    summary NVARCHAR(500) NULL,
    content NVARCHAR(MAX) NOT NULL,
    thumbnail_url VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL CONSTRAINT DF_Posts_Status DEFAULT 'draft',
    views_count INT NOT NULL CONSTRAINT DF_Posts_Views DEFAULT 0,
    created_at DATETIME2 NOT NULL CONSTRAINT DF_Posts_Created DEFAULT GETDATE(),
    updated_at DATETIME2 NOT NULL CONSTRAINT DF_Posts_Updated DEFAULT GETDATE(),
    CONSTRAINT FK_Posts_Users FOREIGN KEY(author_id) REFERENCES dbo.USERS(id),
    CONSTRAINT CK_Posts_Status CHECK (status IN ('draft','published','archived'))
);
GO
CREATE INDEX IX_Posts_Status_CreatedAt ON dbo.Posts(status, created_at DESC);
GO

/* B11. SAN_PHAM */
CREATE TABLE dbo.SAN_PHAM(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_SAN_PHAM PRIMARY KEY DEFAULT NEWID(),
    ten_san_pham NVARCHAR(150) NOT NULL,
    gia DECIMAL(18,2) NOT NULL,
    hinh_anh VARCHAR(500) NULL,
    mo_ta NVARCHAR(MAX) NULL,
    loai VARCHAR(30) NOT NULL,
    con_ban BIT NOT NULL CONSTRAINT DF_SAN_PHAM_CON_BAN DEFAULT 1,
    ngay_tao DATETIME NOT NULL CONSTRAINT DF_SAN_PHAM_NGAY DEFAULT GETDATE(),
    CONSTRAINT CK_SAN_PHAM_GIA CHECK (gia >= 0),
    CONSTRAINT CK_SAN_PHAM_LOAI CHECK (loai IN ('DO_UONG','DICH_VU'))
);
GO

/* B12. DICH_VU_DAT_SAN */
CREATE TABLE dbo.DICH_VU_DAT_SAN(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_DICH_VU_DAT_SAN PRIMARY KEY DEFAULT NEWID(),
    dat_san_id UNIQUEIDENTIFIER NOT NULL,
    san_pham_id UNIQUEIDENTIFIER NOT NULL,
    so_luong INT NOT NULL,
    gia DECIMAL(18,2) NOT NULL,
    ngay_dat DATETIME NOT NULL CONSTRAINT DF_DICH_VU_DAT_SAN_NGAY DEFAULT GETDATE(),
    CONSTRAINT FK_DICH_VU_DAT_SAN FOREIGN KEY(dat_san_id) REFERENCES dbo.DAT_SAN(id),
    CONSTRAINT FK_DICH_VU_SAN_PHAM FOREIGN KEY(san_pham_id) REFERENCES dbo.SAN_PHAM(id),
    CONSTRAINT CK_DICH_VU_SO_LUONG CHECK (so_luong > 0),
    CONSTRAINT CK_DICH_VU_GIA CHECK (gia >= 0)
);
GO

/* B13. VOUCHER */
CREATE TABLE dbo.VOUCHER(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_VOUCHER PRIMARY KEY DEFAULT NEWID(),
    ma VARCHAR(50) NOT NULL CONSTRAINT UQ_VOUCHER_MA UNIQUE,
    ten NVARCHAR(200) NOT NULL,
    mo_ta NVARCHAR(500) NULL,
    loai_giam VARCHAR(20) NOT NULL,
    gia_tri_giam DECIMAL(18,2) NOT NULL,
    giam_toi_da DECIMAL(18,2) NULL,
    don_toi_thieu DECIMAL(18,2) NULL,
    doi_tuong_khach_hang VARCHAR(20) NOT NULL CONSTRAINT DF_VOUCHER_DOI_TUONG DEFAULT 'ALL',
    so_luong INT NOT NULL,
    da_su_dung INT NOT NULL CONSTRAINT DF_VOUCHER_DA_SU_DUNG DEFAULT 0,
    gioi_han_moi_khach INT NOT NULL CONSTRAINT DF_VOUCHER_GIOI_HAN DEFAULT 1,
    ngay_bat_dau DATETIME NOT NULL,
    ngay_ket_thuc DATETIME NOT NULL,
    trang_thai VARCHAR(20) NOT NULL CONSTRAINT DF_VOUCHER_TRANG_THAI DEFAULT 'HOAT_DONG',

    CONSTRAINT CK_VOUCHER_LOAI CHECK (loai_giam IN ('PHAN_TRAM','CO_DINH')),
    CONSTRAINT CK_VOUCHER_GIA CHECK (gia_tri_giam > 0),
    CONSTRAINT CK_VOUCHER_GIAM_MAX CHECK (giam_toi_da IS NULL OR giam_toi_da > 0),
    CONSTRAINT CK_VOUCHER_DON_MIN CHECK (don_toi_thieu IS NULL OR don_toi_thieu >= 0),
    CONSTRAINT CK_VOUCHER_DOI_TUONG CHECK (doi_tuong_khach_hang IN ('ALL','NEW_USER','SILVER','GOLD','DIAMOND')),
    CONSTRAINT CK_VOUCHER_SO_LUONG CHECK (so_luong > 0 AND da_su_dung >= 0 AND da_su_dung <= so_luong),
    CONSTRAINT CK_VOUCHER_GIOI_HAN CHECK (gioi_han_moi_khach > 0),
    CONSTRAINT CK_VOUCHER_THOI_GIAN CHECK (ngay_ket_thuc > ngay_bat_dau),
    CONSTRAINT CK_VOUCHER_TRANG_THAI CHECK (trang_thai IN ('HOAT_DONG','TAM_DUNG','HET_HAN')),
    CONSTRAINT CK_VOUCHER_PHAN_TRAM CHECK (loai_giam <> 'PHAN_TRAM' OR gia_tri_giam <= 100)
);
GO

ALTER TABLE dbo.DAT_SAN
ADD CONSTRAINT FK_DAT_SAN_VOUCHER FOREIGN KEY(voucher_id) REFERENCES dbo.VOUCHER(id);
GO

/* B14. SU_DUNG_VOUCHER */
CREATE TABLE dbo.SU_DUNG_VOUCHER(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_SU_DUNG_VOUCHER PRIMARY KEY DEFAULT NEWID(),
    voucher_id UNIQUEIDENTIFIER NOT NULL,
    nguoi_dung_id UNIQUEIDENTIFIER NOT NULL,
    dat_san_id UNIQUEIDENTIFIER NULL,
    so_tien_giam DECIMAL(18,2) NOT NULL,
    ngay_su_dung DATETIME NOT NULL CONSTRAINT DF_SDV_NGAY DEFAULT GETDATE(),
    CONSTRAINT FK_SDV_VOUCHER FOREIGN KEY(voucher_id) REFERENCES dbo.VOUCHER(id),
    CONSTRAINT FK_SDV_USER FOREIGN KEY(nguoi_dung_id) REFERENCES dbo.USERS(id),
    CONSTRAINT FK_SDV_DAT_SAN FOREIGN KEY(dat_san_id) REFERENCES dbo.DAT_SAN(id),
    CONSTRAINT CK_SDV_SO_TIEN CHECK (so_tien_giam >= 0)
);
GO

/* B15. DANH_GIA_HE_THONG */
CREATE TABLE dbo.DANH_GIA_HE_THONG(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_DANH_GIA_HE_THONG PRIMARY KEY DEFAULT NEWID(),
    nguoi_dung_id UNIQUEIDENTIFIER NULL,
    ten_nguoi_dung NVARCHAR(255) NOT NULL,
    vai_tro VARCHAR(20) NOT NULL,
    so_sao INT NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    hien_thi BIT NOT NULL CONSTRAINT DF_DGHT_HIEN_THI DEFAULT 1,
    ngay_danh_gia DATETIME NOT NULL CONSTRAINT DF_DGHT_NGAY DEFAULT GETDATE(),
    phan_hoi NVARCHAR(MAX) NULL,
    ngay_phan_hoi DATETIME NULL,
    nguoi_phan_hoi_id UNIQUEIDENTIFIER NULL,
    CONSTRAINT FK_DGHT_USER FOREIGN KEY(nguoi_dung_id) REFERENCES dbo.USERS(id),
    CONSTRAINT FK_DGHT_NGUOI_PHAN_HOI FOREIGN KEY(nguoi_phan_hoi_id) REFERENCES dbo.USERS(id),
    CONSTRAINT CK_DGHT_SO_SAO CHECK (so_sao BETWEEN 1 AND 5),
    CONSTRAINT CK_DGHT_VAI_TRO CHECK (vai_tro IN ('USER','STAFF','ADMIN'))
);
GO

/* B16. Tickets */
CREATE TABLE dbo.Tickets(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_Tickets PRIMARY KEY DEFAULT NEWID(),
    ma_ticket VARCHAR(20) NOT NULL CONSTRAINT UQ_Tickets_Ma UNIQUE,
    khach_hang_id UNIQUEIDENTIFIER NULL,
    ten_khach_hang NVARCHAR(100) NULL,
    so_dien_thoai VARCHAR(15) NULL,
    nhan_vien_id UNIQUEIDENTIFIER NULL,
    chu_de NVARCHAR(255) NOT NULL CONSTRAINT DF_Tickets_ChuDe DEFAULT N'Hỗ trợ đặt sân',
    trang_thai VARCHAR(20) NOT NULL CONSTRAINT DF_Tickets_TrangThai DEFAULT 'CHUA_TIEP_NHAN',
    ngay_tao DATETIME2 NOT NULL CONSTRAINT DF_Tickets_NgayTao DEFAULT GETDATE(),
    ngay_cap_nhat DATETIME2 NOT NULL CONSTRAINT DF_Tickets_NgayCapNhat DEFAULT GETDATE(),
    CONSTRAINT FK_Tickets_KhachHang FOREIGN KEY(khach_hang_id) REFERENCES dbo.USERS(id),
    CONSTRAINT FK_Tickets_NhanVien FOREIGN KEY(nhan_vien_id) REFERENCES dbo.USERS(id),
    CONSTRAINT CK_Tickets_TrangThai CHECK (trang_thai IN ('CHUA_TIEP_NHAN','DANG_XU_LY','DA_DONG'))
);
GO

/* B17. Messages */
CREATE TABLE dbo.Messages(
    id UNIQUEIDENTIFIER NOT NULL CONSTRAINT PK_Messages PRIMARY KEY DEFAULT NEWID(),
    ticket_id UNIQUEIDENTIFIER NOT NULL,
    nguoi_gui_id UNIQUEIDENTIFIER NULL,
    loai_nguoi_gui VARCHAR(20) NOT NULL,
    noi_dung NVARCHAR(MAX) NOT NULL,
    thoi_gian_gui DATETIME2 NOT NULL CONSTRAINT DF_Messages_ThoiGian DEFAULT GETDATE(),
    da_doc BIT NOT NULL CONSTRAINT DF_Messages_DaDoc DEFAULT 0,
    thoi_gian_doc DATETIME2 NULL,
    CONSTRAINT FK_Messages_Tickets FOREIGN KEY(ticket_id) REFERENCES dbo.Tickets(id) ON DELETE CASCADE,
    CONSTRAINT FK_Messages_NguoiGui FOREIGN KEY(nguoi_gui_id) REFERENCES dbo.USERS(id),
    CONSTRAINT CK_Messages_LoaiNguoiGui CHECK (loai_nguoi_gui IN ('KHACH_HANG','STAFF','ADMIN','BOT'))
);
GO

/* INDEX */
CREATE INDEX IX_DAT_SAN_USER_STATUS_DATE ON dbo.DAT_SAN(nguoi_dung_id, trang_thai, ngay_dat);
CREATE INDEX IX_DAT_SAN_SAN_DATE_TIME ON dbo.DAT_SAN(san_bong_id, ngay_da, gio_bat_dau, gio_ket_thuc);
CREATE INDEX IX_THANH_TOAN_DAT_SAN ON dbo.THANH_TOAN(dat_san_id, trang_thai, ngay_thanh_toan);
CREATE INDEX IX_VOUCHER_STATUS_TIME ON dbo.VOUCHER(trang_thai, ngay_bat_dau, ngay_ket_thuc);
CREATE INDEX IX_SDV_USER_VOUCHER ON dbo.SU_DUNG_VOUCHER(nguoi_dung_id, voucher_id, ngay_su_dung);
CREATE INDEX IX_MESSAGES_TICKET ON dbo.Messages(ticket_id, thoi_gian_gui);
CREATE INDEX IX_TICKETS_STATUS ON dbo.Tickets(trang_thai, ngay_cap_nhat);
GO

/* =====================================================================
   PHAN C - DU LIEU
   ===================================================================== */

/* C1. USERS - tai khoan seed */
INSERT INTO dbo.USERS(ho_ten, so_dien_thoai, email, mat_khau, dia_chi, vai_tro, trang_thai)
VALUES
(N'Nguyễn Tiến Hùng', '0901234598', 'admin@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hồ Chí Minh', 'ADMIN', 1),
(N'Trần Nhân Viên', '0912345678', 'staff@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Đà Nẵng', 'STAFF', 1),
(N'Lê Khách Hàng', '0923456789', 'user@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e', N'Hồ Chí Minh', 'USER', 1);
GO

/* C2. USERS - 8 khách mẫu */
INSERT INTO dbo.USERS(ho_ten, so_dien_thoai, email, mat_khau, dia_chi, vai_tro, trang_thai)
VALUES
(N'Phạm Minh Quân','0931000001','quan.pham@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Quận 1, TP.HCM','USER',1),
(N'Đỗ Thu Hà','0931000002','ha.do@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Cầu Giấy, Hà Nội','USER',1),
(N'Vũ Đình Nam','0931000003','nam.vu@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Hải Châu, Đà Nẵng','USER',1),
(N'Bùi Thanh Tùng','0931000004','tung.bui@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Thanh Xuân, Hà Nội','USER',1),
(N'Ngô Gia Bảo','0931000005','bao.ngo@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Quận 7, TP.HCM','USER',1),
(N'Trịnh Khánh Linh','0931000006','linh.trinh@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Ninh Kiều, Cần Thơ','USER',1),
(N'Hoàng Việt Anh','0931000007','anh.hoang@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Lê Chân, Hải Phòng','USER',1),
(N'Đặng Tuấn Kiệt','0931000008','kiet.dang@gmail.com','$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',N'Thủ Đức, TP.HCM','USER',1);
GO

/* C3. SAN_BONG - 8 sân duy nhất */
INSERT INTO dbo.SAN_BONG(id, ten_san, loai_san, mo_ta, dia_chi, vi_do, kinh_do, hinh_anh, trang_thai)
VALUES
('A1000000-0000-0000-0000-000000000001',N'Sân A1 – 5 người',5,N'Sân cỏ nhân tạo thế hệ 3, hệ thống đèn LED.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86872000,106.62510000,'https://images.unsplash.com/photo-1574629810360-7efbbe195018','HOAT_DONG'),
('A1000000-0000-0000-0000-000000000002',N'Sân A2 – 5 người',5,N'Sân cỏ nhân tạo chuẩn FIFA, có phòng thay đồ.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86875000,106.62515000,'https://images.unsplash.com/photo-1551854838-212c9a5a049a','HOAT_DONG'),
('A1000000-0000-0000-0000-000000000003',N'Sân A3 – 5 người',5,N'Sân cỏ nhân tạo cao cấp, mặt sân phẳng.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86878000,106.62520000,'https://images.unsplash.com/photo-1553778263-73a83bab9b0c','HOAT_DONG'),
('A1000000-0000-0000-0000-000000000004',N'Sân A4 – 5 người',5,N'Sân cỏ nhân tạo cao cấp, có phòng thay đồ riêng.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86881000,106.62525000,'https://images.unsplash.com/photo-1529900748604-07564a03e7a6','HOAT_DONG'),
('B1000000-0000-0000-0000-000000000001',N'Sân B1 – 7 người',7,N'Sân cỏ nhân tạo 7 người, đèn LED.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86860000,106.62490000,'https://images.unsplash.com/photo-1540747913346-19e32dc3e97e','HOAT_DONG'),
('B1000000-0000-0000-0000-000000000002',N'Sân B2 – 7 người',7,N'Sân cỏ nhân tạo tiêu chuẩn thi đấu.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86857000,106.62485000,'https://images.unsplash.com/photo-1459865264687-595d652de67e','HOAT_DONG'),
('B1000000-0000-0000-0000-000000000003',N'Sân B3 – 7 người',7,N'Sân cỏ nhân tạo thế hệ mới.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86854000,106.62480000,'https://images.unsplash.com/photo-1522778119026-d647f0596c20','HOAT_DONG'),
('B1000000-0000-0000-0000-000000000004',N'Sân B4 – 7 người',7,N'Sân 7 người cao cấp.',N'Hẻm 104 Tân Sơn, P. Tân Sơn, Q.12, TP.HCM',10.86851000,106.62475000,'https://images.unsplash.com/photo-1431324155629-1a6dda1505c9','HOAT_DONG');
GO

/* C4. GIA_SAN - 07:00 -> 22:00
   Don vi tien giu theo file moi: 2500 = 250.000d neu he thong cua ban
   dang quy uoc 1 don vi = 100 dong. Neu Java luu VND truc tiep,
   doi cac gia truoc khi chay. */
INSERT INTO dbo.GIA_SAN(san_bong_id, gio_bat_dau, gio_ket_thuc, gia_tien)
SELECT s.id, g.gio_bat_dau, g.gio_ket_thuc, g.gia_tien
FROM dbo.SAN_BONG s
CROSS APPLY (
    VALUES
    (CAST('07:00' AS TIME),CAST('08:00' AS TIME), CASE WHEN s.loai_san=5 THEN 2500 ELSE 5000 END),
    (CAST('08:00' AS TIME),CAST('09:00' AS TIME), CASE WHEN s.loai_san=5 THEN 2800 ELSE 5500 END),
    (CAST('09:00' AS TIME),CAST('10:00' AS TIME), CASE WHEN s.loai_san=5 THEN 2800 ELSE 5500 END),
    (CAST('10:00' AS TIME),CAST('11:00' AS TIME), CASE WHEN s.loai_san=5 THEN 2800 ELSE 5500 END),
    (CAST('11:00' AS TIME),CAST('12:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3000 ELSE 5800 END),
    (CAST('12:00' AS TIME),CAST('13:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3000 ELSE 5800 END),
    (CAST('13:00' AS TIME),CAST('14:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3000 ELSE 5800 END),
    (CAST('14:00' AS TIME),CAST('15:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3200 ELSE 6200 END),
    (CAST('15:00' AS TIME),CAST('16:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3200 ELSE 6200 END),
    (CAST('16:00' AS TIME),CAST('17:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3300 ELSE 6500 END),
    (CAST('17:00' AS TIME),CAST('18:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3800 ELSE 7500 END),
    (CAST('18:00' AS TIME),CAST('19:00' AS TIME), CASE WHEN s.loai_san=5 THEN 4000 ELSE 8000 END),
    (CAST('19:00' AS TIME),CAST('20:00' AS TIME), CASE WHEN s.loai_san=5 THEN 4000 ELSE 8000 END),
    (CAST('20:00' AS TIME),CAST('21:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3800 ELSE 7500 END),
    (CAST('21:00' AS TIME),CAST('22:00' AS TIME), CASE WHEN s.loai_san=5 THEN 3500 ELSE 7000 END)
) g(gio_bat_dau,gio_ket_thuc,gia_tien);
GO

/* C5. SAN_PHAM */
INSERT INTO dbo.SAN_PHAM(ten_san_pham,gia,loai,con_ban)
VALUES
(N'Nước suối Lavie',10000,'DO_UONG',1),
(N'Sting dâu',15000,'DO_UONG',1),
(N'Redbull',15000,'DO_UONG',1),
(N'Thuê áo bib',20000,'DICH_VU',1),
(N'Thuê bóng đá',50000,'DICH_VU',1);
GO

/* C6. VOUCHER */
INSERT INTO dbo.VOUCHER
(ma,ten,mo_ta,loai_giam,gia_tri_giam,giam_toi_da,don_toi_thieu,doi_tuong_khach_hang,
 so_luong,da_su_dung,gioi_han_moi_khach,ngay_bat_dau,ngay_ket_thuc,trang_thai)
VALUES
('WELCOME20',N'Ưu đãi khách mới',N'Giảm 20.000đ cho khách mới','CO_DINH',20000,NULL,100000,'NEW_USER',100,0,1,GETDATE(),DATEADD(DAY,30,GETDATE()),'HOAT_DONG'),
('SILVER30',N'Ưu đãi hạng Bạc',N'Giảm 30.000đ cho khách đạt từ 3 lượt đặt trong 30 ngày','CO_DINH',30000,NULL,150000,'SILVER',100,0,1,GETDATE(),DATEADD(DAY,30,GETDATE()),'HOAT_DONG'),
('GOLD50',N'Ưu đãi hạng Vàng',N'Giảm 50.000đ cho khách đạt từ 6 lượt đặt trong 30 ngày','CO_DINH',50000,NULL,200000,'GOLD',100,0,1,GETDATE(),DATEADD(DAY,30,GETDATE()),'HOAT_DONG'),
('DIAMOND100',N'Ưu đãi hạng Kim Cương',N'Giảm 100.000đ cho khách đạt từ 10 lượt đặt trong 30 ngày','CO_DINH',100000,NULL,300000,'DIAMOND',50,0,1,GETDATE(),DATEADD(DAY,30,GETDATE()),'HOAT_DONG');
GO

/* C7. POSTS mau */
INSERT INTO dbo.Posts(author_id,title,slug,summary,content,status)
SELECT TOP (1) id,
       N'Khai trương hệ thống Dream League',
       'khai-truong-dream-league',
       N'Thông tin khai trương sân bóng.',
       N'Dream League chào mừng khách hàng đến đặt sân và sử dụng dịch vụ.',
       'published'
FROM dbo.USERS
WHERE vai_tro='ADMIN';
GO

/* C8. DASHBOARD - DAT_SAN
   116 don nam 2026.
   Khong dung 06:00 nua vi database quy dinh 07:00 -> 22:00.
*/
;WITH So AS (
    SELECT TOP (400) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.all_objects a CROSS JOIN sys.all_objects b
),
San AS (
    SELECT id,
           ROW_NUMBER() OVER (ORDER BY ten_san)-1 AS idx,
           COUNT(*) OVER() AS tong
    FROM dbo.SAN_BONG
),
Khach AS (
    SELECT id,ho_ten,so_dien_thoai,
           ROW_NUMBER() OVER (ORDER BY ngay_tao,email)-1 AS idx,
           COUNT(*) OVER() AS tong
    FROM dbo.USERS
    WHERE vai_tro='USER'
),
Thang AS (
    SELECT * FROM (VALUES
        (1,6),(2,7),(3,9),(4,11),(5,14),(6,16),
        (7,15),(8,12),(9,8),(10,6),(11,5),(12,7)
    ) t(thang,so_don)
),
Don AS (
    SELECT t.thang,s.n AS k,
           2+((s.n*3)%26) AS ngay,
           CHOOSE(1+(s.n%4),8,16,18,20) AS gio_bd
    FROM Thang t
    JOIN So s ON s.n<=t.so_don
)
INSERT INTO dbo.DAT_SAN
(nguoi_dung_id,san_bong_id,ho_ten_dat,so_dien_thoai,ngay_da,gio_bat_dau,gio_ket_thuc,
 tong_tien,tien_coc,trang_thai,ngay_dat,kenh_dat,phuong_thuc_coc)
SELECT kh.id,sb.id,kh.ho_ten,kh.so_dien_thoai,
       DATEFROMPARTS(2026,d.thang,d.ngay),
       TIMEFROMPARTS(d.gio_bd,0,0,0,0),
       TIMEFROMPARTS(d.gio_bd+2,0,0,0,0),
       gia.tong,
       ROUND(gia.tong/2.0,0),
       CASE
         WHEN DATEFROMPARTS(2026,d.thang,d.ngay)<CAST(GETDATE() AS DATE)
              THEN CASE WHEN d.k%7=0 THEN 'DA_HUY' ELSE 'HOAN_THANH' END
         ELSE CASE WHEN d.k%3=0 THEN 'CHO_XAC_NHAN' ELSE 'DA_COC' END
       END,
       DATEADD(DAY,-3,DATETIME2FROMPARTS(2026,d.thang,d.ngay,9,0,0,0,0)),
       'ONLINE',
       'QR'
FROM Don d
JOIN San sb ON sb.idx=d.k%sb.tong
JOIN Khach kh ON kh.idx=(d.k+d.thang)%kh.tong
CROSS APPLY (
    SELECT SUM(g.gia_tien) AS tong
    FROM dbo.GIA_SAN g
    WHERE g.san_bong_id=sb.id
      AND g.gio_bat_dau<TIMEFROMPARTS(d.gio_bd+2,0,0,0,0)
      AND g.gio_ket_thuc>TIMEFROMPARTS(d.gio_bd,0,0,0,0)
) gia
WHERE gia.tong IS NOT NULL;
GO

/* C9. THANH_TOAN - tien coc */
INSERT INTO dbo.THANH_TOAN
(dat_san_id,so_tien,loai,phuong_thuc,ma_giao_dich,ngay_thanh_toan,trang_thai)
SELECT ds.id,ds.tien_coc,'COC',
       CASE WHEN DATEPART(DAY,ds.ngay_da)%2=0 THEN 'TIEN_MAT' ELSE 'QR' END,
       'VNP'+RIGHT('00000000000'+CAST(ABS(CHECKSUM(ds.id)) AS VARCHAR(20)),11),
       ds.ngay_dat,
       CASE WHEN ds.trang_thai='CHO_XAC_NHAN' THEN 'DANG_CHO' ELSE 'THANH_CONG' END
FROM dbo.DAT_SAN ds
WHERE ds.trang_thai<>'DA_HUY';
GO

/* C10. THANH_TOAN - phan con lai */
INSERT INTO dbo.THANH_TOAN
(dat_san_id,so_tien,loai,phuong_thuc,ma_giao_dich,ngay_thanh_toan,trang_thai)
SELECT ds.id,ds.tong_tien-ds.tien_coc,'THANH_TOAN_CON_LAI','TIEN_MAT',
       'VNP'+RIGHT('00000000000'+CAST(ABS(CHECKSUM(ds.id,'con-lai')) AS VARCHAR(20)),11),
       DATEADD(HOUR,DATEPART(HOUR,ds.gio_ket_thuc),CAST(ds.ngay_da AS DATETIME)),
       'THANH_CONG'
FROM dbo.DAT_SAN ds
WHERE ds.trang_thai='HOAN_THANH';
GO

/* C11. 3 don HOAN_THANH gan nhat ve ngay hien tai.
   Cap nhat ca DAT_SAN va THANH_TOAN de dashboard doanh thu/ngay
   khong bi lech ngay. */
;WITH HomNay AS (
    SELECT TOP (3) id
    FROM dbo.DAT_SAN
    WHERE trang_thai='HOAN_THANH'
    ORDER BY ngay_da DESC,id
)
UPDATE ds
SET ds.ngay_da=CAST(GETDATE() AS DATE)
FROM dbo.DAT_SAN ds
JOIN HomNay h ON h.id=ds.id;
GO

UPDATE tt
SET tt.ngay_thanh_toan=CAST(GETDATE() AS DATETIME)
FROM dbo.THANH_TOAN tt
JOIN dbo.DAT_SAN ds ON ds.id=tt.dat_san_id
WHERE ds.ngay_da=CAST(GETDATE() AS DATE)
  AND ds.trang_thai='HOAN_THANH'
  AND tt.trang_thai='THANH_CONG';
GO

/* C12. DU LIEU TEST TICKET/CHAT */
DECLARE @ticket UNIQUEIDENTIFIER = NEWID();
DECLARE @userId UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM dbo.USERS WHERE email='user@gmail.com');
DECLARE @staffId UNIQUEIDENTIFIER = (SELECT TOP 1 id FROM dbo.USERS WHERE email='staff@gmail.com');

INSERT INTO dbo.Tickets(id,ma_ticket,khach_hang_id,ten_khach_hang,so_dien_thoai,nhan_vien_id,chu_de,trang_thai)
VALUES(@ticket,'TK-1001',@userId,N'Lê Khách Hàng','0923456789',@staffId,N'Hỗ trợ đặt sân','DA_DONG');

INSERT INTO dbo.Messages(ticket_id,nguoi_gui_id,loai_nguoi_gui,noi_dung,da_doc,thoi_gian_doc)
VALUES
(@ticket,@userId,'KHACH_HANG',N'Xin hỗ trợ đặt sân 18h hôm nay.',1,GETDATE()),
(@ticket,@staffId,'STAFF',N'Dạ, em đã kiểm tra và hỗ trợ anh.',1,GETDATE());
GO

/* =====================================================================
   PHAN D - KIEM TRA
   ===================================================================== */
SELECT N'USERS' AS bang, COUNT(*) AS so_luong FROM dbo.USERS
UNION ALL SELECT N'SAN_BONG',COUNT(*) FROM dbo.SAN_BONG
UNION ALL SELECT N'GIA_SAN',COUNT(*) FROM dbo.GIA_SAN
UNION ALL SELECT N'DAT_SAN',COUNT(*) FROM dbo.DAT_SAN
UNION ALL SELECT N'THANH_TOAN',COUNT(*) FROM dbo.THANH_TOAN
UNION ALL SELECT N'SAN_PHAM',COUNT(*) FROM dbo.SAN_PHAM
UNION ALL SELECT N'VOUCHER',COUNT(*) FROM dbo.VOUCHER
UNION ALL SELECT N'SU_DUNG_VOUCHER',COUNT(*) FROM dbo.SU_DUNG_VOUCHER
UNION ALL SELECT N'Tickets',COUNT(*) FROM dbo.Tickets
UNION ALL SELECT N'Messages',COUNT(*) FROM dbo.Messages;
GO

SELECT
    N'Trạng thái đặt sân' AS thong_ke,
    trang_thai,
    COUNT(*) AS so_luong
FROM dbo.DAT_SAN
GROUP BY trang_thai
ORDER BY trang_thai;
GO

SELECT
    N'Loại sân' AS thong_ke,
    loai_san,
    COUNT(*) AS so_luong
FROM dbo.SAN_BONG
GROUP BY loai_san
ORDER BY loai_san;
GO

SELECT
    N'Doanh thu theo tháng' AS thong_ke,
    YEAR(tt.ngay_thanh_toan) AS nam,
    MONTH(tt.ngay_thanh_toan) AS thang,
    SUM(tt.so_tien) AS doanh_thu
FROM dbo.THANH_TOAN tt
WHERE tt.trang_thai='THANH_CONG'
GROUP BY YEAR(tt.ngay_thanh_toan),MONTH(tt.ngay_thanh_toan)
ORDER BY nam,thang;
GO

/* =====================================================================
   KET THUC
   ===================================================================== */
