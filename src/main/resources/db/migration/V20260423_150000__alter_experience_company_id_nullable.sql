-- Migration: Alter experience to allow custom company name
-- Created: Wed Apr 23 15:00:00 +07 2026

-- Bước 1: Cho phép company_id nullable
ALTER TABLE experience
    ALTER COLUMN company_id DROP NOT NULL;

-- Bước 2: Thêm cột lưu tên công ty tùy chỉnh (khi không có trong hệ thống)
ALTER TABLE experience
    ADD COLUMN custom_company_name VARCHAR(255);

-- Bước 3: Thêm check constraint để đảm bảo ít nhất một trong hai trường có giá trị
ALTER TABLE experience
    ADD CONSTRAINT check_company_info
        CHECK (company_id IS NOT NULL OR custom_company_name IS NOT NULL);