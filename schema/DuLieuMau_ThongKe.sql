/* ============================================================
   DỮ LIỆU MẪU CHO BIỂU ĐỒ / THỐNG KÊ
   ------------------------------------------------------------
   Chạy SAU khi đã chạy DB_QliSanBong.sql.
   Script này CHỈ đụng vào USERS (thêm khách mẫu), DAT_SAN và
   THANH_TOAN. Không đụng SAN_BONG / GIA_SAN / SAN_PHAM.

   Chạy lại nhiều lần được: mỗi lần chạy sẽ xoá sạch đơn đặt sân
   và thanh toán cũ rồi sinh lại từ đầu, nên không bị nhân đôi.

   Tiền được tính ĐÚNG theo cách hệ thống tính (DatSanServiceImpl):
   tong_tien = tổng gia_tien của mọi khung giờ GIA_SAN giao với
   khoảng đặt; tien_coc = 50% làm tròn.
   ============================================================ */

USE QuanLySanBong;
GO

/* ---------- 0. Dọn dữ liệu giao dịch cũ ----------
   Phải xoá bảng con trước, nếu không khoá ngoại sẽ chặn:
   DICH_VU_DAT_SAN và GIA_HAN_GIO đều trỏ tới DAT_SAN.        */
DELETE FROM DICH_VU_DAT_SAN;
DELETE FROM GIA_HAN_GIO;
DELETE FROM THANH_TOAN;
DELETE FROM DAT_SAN;
GO

/* ---------- 1. Thêm khách hàng mẫu ----------
   Mật khẩu giống seed gốc: 123456                            */
INSERT INTO USERS (ho_ten, so_dien_thoai, email, mat_khau, dia_chi, vai_tro, trang_thai)
SELECT v.ho_ten, v.sdt, v.email,
       '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNahGWqX2Q.ZGE1T9Z9sK.e',
       v.dia_chi, 'USER', 1
FROM (VALUES
    (N'Phạm Minh Quân',   '0931000001', 'quan.pham@gmail.com',   N'Quận 1, TP.HCM'),
    (N'Đỗ Thu Hà',        '0931000002', 'ha.do@gmail.com',       N'Cầu Giấy, Hà Nội'),
    (N'Vũ Đình Nam',      '0931000003', 'nam.vu@gmail.com',      N'Hải Châu, Đà Nẵng'),
    (N'Bùi Thanh Tùng',   '0931000004', 'tung.bui@gmail.com',    N'Thanh Xuân, Hà Nội'),
    (N'Ngô Gia Bảo',      '0931000005', 'bao.ngo@gmail.com',     N'Quận 7, TP.HCM'),
    (N'Trịnh Khánh Linh', '0931000006', 'linh.trinh@gmail.com',  N'Ninh Kiều, Cần Thơ'),
    (N'Hoàng Việt Anh',   '0931000007', 'anh.hoang@gmail.com',   N'Lê Chân, Hải Phòng'),
    (N'Đặng Tuấn Kiệt',   '0931000008', 'kiet.dang@gmail.com',   N'Thủ Đức, TP.HCM')
) AS v(ho_ten, sdt, email, dia_chi)
WHERE NOT EXISTS (SELECT 1 FROM USERS u WHERE u.email = v.email);
GO

/* ---------- 2. Sinh đơn đặt sân ----------
   Ý tưởng: đánh số n = 1..N, từ n suy ra sân / ngày / khung giờ.
   Vì sân và ngày đều đổi theo n nên không có 2 đơn nào trùng
   (cùng sân + cùng ngày + cùng giờ) -> tôn trọng luật chống
   trùng lịch của hệ thống.                                    */

;WITH So AS (          -- bảng số 1..400
    SELECT TOP (400) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.all_objects a CROSS JOIN sys.all_objects b
),
San AS (               -- đánh số sân 0..12
    SELECT id, ten_san,
           ROW_NUMBER() OVER (ORDER BY ten_san) - 1 AS idx,
           COUNT(*) OVER () AS tong
    FROM SAN_BONG
),
Khach AS (             -- đánh số khách 0..n
    SELECT id, ho_ten, so_dien_thoai,
           ROW_NUMBER() OVER (ORDER BY ngay_tao, email) - 1 AS idx,
           COUNT(*) OVER () AS tong
    FROM USERS WHERE vai_tro = 'USER'
),
Thang AS (             -- số đơn mỗi tháng: hè đông khách hơn
    SELECT * FROM (VALUES
        (1, 6), (2, 7), (3, 9), (4, 11), (5, 14), (6, 16),
        (7, 15), (8, 12), (9, 8), (10, 6), (11, 5), (12, 7)
    ) AS t(thang, so_don)
),
Don AS (
    SELECT
        t.thang,
        s.n AS k,
        -- ngày trong tháng: rải đều, không vượt 28 cho an toàn
        (2 + ((s.n * 3) % 26)) AS ngay,
        -- khung giờ bắt đầu: xoay vòng 5 mốc
        CHOOSE(1 + (s.n % 5), 6, 8, 16, 18, 20) AS gio_bd
    FROM Thang t
    JOIN So s ON s.n <= t.so_don
)
INSERT INTO DAT_SAN (nguoi_dung_id, san_bong_id, ho_ten_dat, so_dien_thoai,
                     ngay_da, gio_bat_dau, gio_ket_thuc,
                     tong_tien, tien_coc, trang_thai, ngay_dat)
SELECT
    kh.id,
    sb.id,
    kh.ho_ten,
    kh.so_dien_thoai,
    DATEFROMPARTS(2026, d.thang, d.ngay),
    TIMEFROMPARTS(d.gio_bd, 0, 0, 0, 0),
    TIMEFROMPARTS(d.gio_bd + 2, 0, 0, 0, 0),
    gia.tong,
    CAST(ROUND(gia.tong / 2.0, 0) AS DECIMAL(10,0)),
    /* Tháng đã qua -> phần lớn HOÀN THÀNH, rải vài đơn huỷ.
       Từ hôm nay trở đi -> đơn đang chờ / đã cọc.            */
    CASE
        WHEN DATEFROMPARTS(2026, d.thang, d.ngay) < CAST(GETDATE() AS DATE)
             THEN CASE WHEN d.k % 7 = 0 THEN 'DA_HUY' ELSE 'HOAN_THANH' END
        ELSE CASE WHEN d.k % 3 = 0 THEN 'CHO_XAC_NHAN' ELSE 'DA_COC' END
    END,
    DATEADD(DAY, -3, DATETIME2FROMPARTS(2026, d.thang, d.ngay, 9, 0, 0, 0, 0))
FROM Don d
JOIN San   sb ON sb.idx = d.k % sb.tong
JOIN Khach kh ON kh.idx = (d.k + d.thang) % kh.tong
CROSS APPLY (
    /* Đúng công thức tinh tong tien cua he thong:
       cộng mọi khung giờ GIA_SAN giao với [gio_bd, gio_bd+2)  */
    SELECT SUM(g.gia_tien) AS tong
    FROM GIA_SAN g
    WHERE g.san_bong_id = sb.id
      AND g.gio_bat_dau  < TIMEFROMPARTS(d.gio_bd + 2, 0, 0, 0, 0)
      AND g.gio_ket_thuc > TIMEFROMPARTS(d.gio_bd,     0, 0, 0, 0)
) gia
WHERE gia.tong IS NOT NULL;
GO

/* ---------- 3. Sinh bản ghi thanh toán ----------
   - Mọi đơn chưa huỷ đều có 1 giao dịch CỌC.
   - Đơn HOÀN THÀNH có thêm giao dịch THANH TOÁN CÒN LẠI.
   - Đơn CHỜ XÁC NHẬN thì cọc vẫn ĐANG CHỜ.                   */

-- 3a. Tiền cọc
INSERT INTO THANH_TOAN (dat_san_id, so_tien, loai, phuong_thuc, ma_giao_dich,
                        ngay_thanh_toan, trang_thai)
SELECT
    ds.id,
    ds.tien_coc,
    'COC',
    CASE WHEN DATEPART(DAY, ds.ngay_da) % 2 = 0 THEN 'TIEN_MAT' ELSE 'QR' END,
    'VNP' + RIGHT('00000000000' + CAST(ABS(CHECKSUM(ds.id)) AS VARCHAR(20)), 11),
    ds.ngay_dat,
    CASE WHEN ds.trang_thai = 'CHO_XAC_NHAN' THEN 'DANG_CHO' ELSE 'THANH_CONG' END
FROM DAT_SAN ds
WHERE ds.trang_thai <> 'DA_HUY';
GO

-- 3b. Phần còn lại của đơn đã hoàn thành
INSERT INTO THANH_TOAN (dat_san_id, so_tien, loai, phuong_thuc, ma_giao_dich,
                        ngay_thanh_toan, trang_thai)
SELECT
    ds.id,
    ds.tong_tien - ds.tien_coc,
    'THANH_TOAN_CON_LAI',
    'TIEN_MAT',
    'VNP' + RIGHT('00000000000' + CAST(ABS(CHECKSUM(ds.id, 'con-lai')) AS VARCHAR(20)), 11),
    DATETIME2FROMPARTS(YEAR(ds.ngay_da), MONTH(ds.ngay_da), DAY(ds.ngay_da),
                       DATEPART(HOUR, ds.gio_ket_thuc), 0, 0, 0, 0),
    'THANH_CONG'
FROM DAT_SAN ds
WHERE ds.trang_thai = 'HOAN_THANH';
GO

/* ---------- 4. Đảm bảo hôm nay có số liệu ----------
   Dashboard có 2 thẻ đọc theo đúng ngày hiện tại, nên ép vài
   đơn về hôm nay để 2 thẻ đó không bị 0.                     */
;WITH HomNay AS (
    SELECT TOP (3) id FROM DAT_SAN
    WHERE trang_thai = 'HOAN_THANH' ORDER BY ngay_da DESC
)
UPDATE DAT_SAN SET ngay_da = CAST(GETDATE() AS DATE)
WHERE id IN (SELECT id FROM HomNay);
GO

/* ---------- 5. Kết quả ---------- */
SELECT 'Khach hang (USER)' AS muc, COUNT(*) AS so_luong FROM USERS WHERE vai_tro='USER'
UNION ALL SELECT 'Tong don dat san', COUNT(*) FROM DAT_SAN
UNION ALL SELECT '  - Hoan thanh',   COUNT(*) FROM DAT_SAN WHERE trang_thai='HOAN_THANH'
UNION ALL SELECT '  - Da coc',       COUNT(*) FROM DAT_SAN WHERE trang_thai='DA_COC'
UNION ALL SELECT '  - Cho xac nhan', COUNT(*) FROM DAT_SAN WHERE trang_thai='CHO_XAC_NHAN'
UNION ALL SELECT '  - Da huy',       COUNT(*) FROM DAT_SAN WHERE trang_thai='DA_HUY'
UNION ALL SELECT 'Giao dich',        COUNT(*) FROM THANH_TOAN;
GO
