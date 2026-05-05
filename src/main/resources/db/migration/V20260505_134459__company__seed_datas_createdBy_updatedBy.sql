-- Migration: company - seed_datas_createdBy_updatedBy
-- Created: Tue May  5 01:44:59 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:


-- =========================================================
-- EMPLOYER 1; FPT SOFTWARE
-- =========================================================
UPDATE company SET
    created_by = 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1',
    updated_by = 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1'
WHERE company_name = 'FPT Software';

-- =========================================================
-- EMPLOYER 2: VNG Corporation
-- =========================================================
UPDATE company SET
    created_by = 'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2',
    updated_by = 'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2'
WHERE company_name = 'VNG Corporation';

-- =========================================================
-- EMPLOYER 3: Tiki Corporation
-- =========================================================
UPDATE company SET
    created_by = 'e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3',
    updated_by = 'e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3'
WHERE company_name = 'Tiki Corporation';

-- =========================================================
-- EMPLOYER 4: VinAI Research
-- =========================================================
UPDATE company SET
    created_by = 'e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4',
    updated_by = 'e4e4e4e4-e4e4-e4e4-e4e4-e4e4e4e4e4e4'
WHERE company_name = 'VinAI Research';

-- =========================================================
-- EMPLOYER 5: CMC Global
-- =========================================================
UPDATE company SET
    created_by = 'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5',
    updated_by = 'e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5'
WHERE company_name = 'CMC Global';

-- =========================================================
-- EMPLOYER 6: KMS Technology
-- =========================================================
UPDATE company SET
    created_by = 'e6e6e6e6-e6e6-e6e6-e6e6-e6e6e6e6e6e6',
    updated_by = 'e6e6e6e6-e6e6-e6e6-e6e6-e6e6e6e6e6e6'
WHERE company_name = 'KMS Technology';

