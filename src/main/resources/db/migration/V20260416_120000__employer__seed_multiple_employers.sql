-- Migration: employer - seed multiple employers with company info for testing
-- Created: Wed Apr 16 2026
-- Purpose: Tạo thêm employer cho các company khác nhau để test chức năng Job
-- UUID khớp với auth_accounts bên auth_service

-- Update employer cũ (e1e1...) cho khớp email với auth
UPDATE employer SET email = 'fpt@example.com'
WHERE employer_id = 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1'
  AND email != 'fpt@example.com';

-- Employer 2: VNG Corporation
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2',
    (SELECT company_id FROM company WHERE company_name = 'VNG Corporation' LIMIT 1),
    'vng@example.com',
    'Minh',
    'Nguyen',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 3: Tiki Corporation
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3',
    (SELECT company_id FROM company WHERE company_name = 'Tiki Corporation' LIMIT 1),
    'tiki@example.com',
    'Lan',
    'Tran',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 4: VinAI Research
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4',
    (SELECT company_id FROM company WHERE company_name = 'VinAI Research' LIMIT 1),
    'vinai@example.com',
    'Huy',
    'Le',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 5: CMC Global
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5',
    (SELECT company_id FROM company WHERE company_name = 'CMC Global' LIMIT 1),
    'cmc@example.com',
    'Thao',
    'Pham',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;

-- Employer 6: KMS Technology
INSERT INTO employer (employer_id, company_id, email, name, surname, status, verification_level, level)
VALUES (
    'e6e6e6e6-e6e6-e6e6-e6e6-e6e6e6e6e6e6',
    (SELECT company_id FROM company WHERE company_name = 'KMS Technology' LIMIT 1),
    'kms@example.com',
    'Duc',
    'Vo',
    'active', 'lv3', 'level1'
) ON CONFLICT (employer_id) DO NOTHING;
