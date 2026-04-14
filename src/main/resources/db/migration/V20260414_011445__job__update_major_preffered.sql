-- Migration: job - update major_preffered for existing jobs
-- Created: Mon Apr 14 2026

-- Update major_preffered cho 10 job sample data theo ngành

-- IT / Phần mềm
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%Java%Backend%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%Frontend%React%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%Backend%Golang%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%DevOps%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%Mobile%Flutter%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%Thực Tập Sinh%Java%';
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%System Admin%';

-- Data / Dữ liệu
UPDATE job SET major_preffered = 'Khoa học dữ liệu' WHERE title LIKE '%Data Engineer%';

-- QA / Kiểm thử
UPDATE job SET major_preffered = 'Công nghệ thông tin' WHERE title LIKE '%QA/QC%';

-- Business Analyst / Phân tích nghiệp vụ
UPDATE job SET major_preffered = 'Kinh doanh' WHERE title LIKE '%Business Analyst%';
