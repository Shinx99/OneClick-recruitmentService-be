-- Migration: employer - seed-datas
-- Created: Wed May  6 07:20:48 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
-- Migration: employer - seed remaining employers
-- Created: Wed May 06 2026
-- Author: hoangvuongbui
-- Purpose: Tạo employer cho 18 company còn lại, UUID khớp với auth_accounts

-- Employer 7: Saigon Technology
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e7e7e7e7-e7e7-e7e7-e7e7-e7e7e7e7e7e7',
    (SELECT company_id FROM company WHERE company_name = 'Saigon Technology' LIMIT 1),
    'saigontech@example.com', 'Khoa', 'Nguyen',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 8: TMA Solutions
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e8e8e8e8-e8e8-e8e8-e8e8-e8e8e8e8e8e8',
    (SELECT company_id FROM company WHERE company_name = 'TMA Solutions' LIMIT 1),
    'tma@example.com', 'Linh', 'Pham',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 9: Axon Active
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e9e9e9e9-e9e9-e9e9-e9e9-e9e9e9e9e9e9',
    (SELECT company_id FROM company WHERE company_name = 'Axon Active' LIMIT 1),
    'axonactive@example.com', 'Nam', 'Tran',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 10: Giao Hàng Tiết Kiệm
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'eaeaeaea-eaea-eaea-eaea-eaeaeaeaeaea',
    (SELECT company_id FROM company WHERE company_name = 'Giao Hàng Tiết Kiệm' LIMIT 1),
    'ghtk@example.com', 'Huong', 'Le',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 11: ELSA Corp
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'ebebebeb-ebeb-ebeb-ebeb-ebebebebebeb',
    (SELECT company_id FROM company WHERE company_name = 'ELSA Corp' LIMIT 1),
    'elsa@example.com', 'Mai', 'Vo',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 12: Rikkeisoft
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'ecececec-ecec-ecec-ecec-ecececececec',
    (SELECT company_id FROM company WHERE company_name = 'Rikkeisoft' LIMIT 1),
    'rikkeisoft@example.com', 'Tuan', 'Hoang',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 13: Viettel Group
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'edededed-eded-eded-eded-edededededed',
    (SELECT company_id FROM company WHERE company_name = 'Viettel Group' LIMIT 1),
    'viettel@example.com', 'Quan', 'Dinh',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 14: MoMo (M_Service)
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    (SELECT company_id FROM company WHERE company_name = 'MoMo (M_Service)' LIMIT 1),
    'momo@example.com', 'Thu', 'Nguyen',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 15: Shopee Vietnam
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'efefefef-efef-efef-efef-efefefefefef',
    (SELECT company_id FROM company WHERE company_name = 'Shopee Vietnam' LIMIT 1),
    'shopee@example.com', 'Bao', 'Phan',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 16: VNPay
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f0f0f0f0-f0f0-f0f0-f0f0-f0f0f0f0f0f0',
    (SELECT company_id FROM company WHERE company_name = 'VNPay' LIMIT 1),
    'vnpay@example.com', 'Vy', 'Dang',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 17: Amanotes
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1',
    (SELECT company_id FROM company WHERE company_name = 'Amanotes' LIMIT 1),
    'amanotes@example.com', 'Kien', 'Bui',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 18: NashTech Vietnam
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f2f2f2f2-f2f2-f2f2-f2f2-f2f2f2f2f2f2',
    (SELECT company_id FROM company WHERE company_name = 'NashTech Vietnam' LIMIT 1),
    'nashtech@example.com', 'Phuong', 'Ngo',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 19: Base.vn
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f3f3f3f3-f3f3-f3f3-f3f3-f3f3f3f3f3f3',
    (SELECT company_id FROM company WHERE company_name = 'Base.vn' LIMIT 1),
    'basevn@example.com', 'Dat', 'Truong',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 20: Cốc Cốc
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f4f4f4f4-f4f4-f4f4-f4f4-f4f4f4f4f4f4',
    (SELECT company_id FROM company WHERE company_name = 'Cốc Cốc' LIMIT 1),
    'coccoc@example.com', 'Long', 'Cao',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 21: Be Group
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f5f5f5f5-f5f5-f5f5-f5f5-f5f5f5f5f5f5',
    (SELECT company_id FROM company WHERE company_name = 'Be Group' LIMIT 1),
    'begroup@example.com', 'Anh', 'Do',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 22: Sendo
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6',
    (SELECT company_id FROM company WHERE company_name = 'Sendo' LIMIT 1),
    'sendo@example.com', 'Dung', 'Vu',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 23: Got It
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f7f7f7f7-f7f7-f7f7-f7f7-f7f7f7f7f7f7',
    (SELECT company_id FROM company WHERE company_name = 'Got It' LIMIT 1),
    'gotit@example.com', 'Phuc', 'Ly',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 24: Orient Software
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'f8f8f8f8-f8f8-f8f8-f8f8-f8f8f8f8f8f8',
    (SELECT company_id FROM company WHERE company_name = 'Orient Software' LIMIT 1),
    'orient@example.com', 'Giang', 'Ha',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;
