package com.dream.sanbong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaUpdater implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        capNhatCauTrucDatabase();
    }

    public synchronized void capNhatCauTrucDatabase() {
        try {
            // 1. Xóa tất cả các ràng buộc UNIQUE, CHECK, DEFAULT hoặc FOREIGN KEY liên quan đến cột so_dien_thoai
            jdbcTemplate.execute("""
                DECLARE @sql NVARCHAR(MAX) = N'';
                SELECT @sql += N'ALTER TABLE ' + QUOTENAME(s.name) + N'.' + QUOTENAME(t.name) 
                              + N' DROP CONSTRAINT ' + QUOTENAME(c.name) + N'; '
                FROM sys.key_constraints c
                JOIN sys.tables t ON c.parent_object_id = t.object_id
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                JOIN sys.index_columns ic ON ic.object_id = t.object_id AND ic.index_id = c.unique_index_id
                JOIN sys.columns col ON ic.object_id = col.object_id AND ic.column_id = col.column_id
                WHERE t.name = 'USERS' AND col.name = 'so_dien_thoai';

                IF @sql <> N'' EXEC sp_executesql @sql;
            """);

            // 2. Xóa các INDEX phụ thuộc trên cột so_dien_thoai (trừ filtered index chúng ta tự tạo)
            jdbcTemplate.execute("""
                DECLARE @sql NVARCHAR(MAX) = N'';
                SELECT @sql += N'DROP INDEX ' + QUOTENAME(i.name) + N' ON ' + QUOTENAME(s.name) + N'.' + QUOTENAME(t.name) + N'; '
                FROM sys.indexes i
                JOIN sys.tables t ON i.object_id = t.object_id
                JOIN sys.schemas s ON t.schema_id = s.schema_id
                JOIN sys.index_columns ic ON ic.object_id = t.object_id AND ic.index_id = i.index_id
                JOIN sys.columns col ON ic.object_id = col.object_id AND ic.column_id = col.column_id
                WHERE t.name = 'USERS' AND col.name = 'so_dien_thoai' 
                  AND i.name <> 'UQ_USERS_so_dien_thoai'
                  AND i.is_primary_key = 0 
                  AND i.is_unique_constraint = 0;

                IF @sql <> N'' EXEC sp_executesql @sql;
            """);

            // 3. Chuyển cột so_dien_thoai thành NULL
            jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN so_dien_thoai VARCHAR(15) NULL;");

            // 4. Tạo filtered unique index (chỉ kiểm tra trùng đối với các số điện thoại khác NULL)
            jdbcTemplate.execute("""
                IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ_USERS_so_dien_thoai' AND object_id = OBJECT_ID('USERS'))
                BEGIN
                    CREATE UNIQUE NONCLUSTERED INDEX UQ_USERS_so_dien_thoai
                    ON USERS(so_dien_thoai)
                    WHERE so_dien_thoai IS NOT NULL;
                END
            """);

            // 5. Thêm cột avatar NVARCHAR(MAX) nếu chưa có
            jdbcTemplate.execute("""
                IF NOT EXISTS (
                    SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                    WHERE TABLE_NAME = 'USERS' AND COLUMN_NAME = 'avatar'
                )
                BEGIN
                    ALTER TABLE USERS ADD avatar NVARCHAR(MAX) NULL;
                END
            """);

            System.out.println(">>> [DatabaseSchemaUpdater] Đã tự động cập nhật cấu trúc database USERS (so_dien_thoai NULL, filtered unique index, avatar) thành công!");
        } catch (Exception e) {
            System.err.println(">>> [DatabaseSchemaUpdater] Lỗi khi cập nhật cấu trúc: " + e.getMessage());
        }
    }
}