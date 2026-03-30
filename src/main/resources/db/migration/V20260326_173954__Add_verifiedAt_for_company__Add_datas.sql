-- Migration: Add_verifiedAt_for_company - Add_datas
-- Created: Thu Mar 26 05:39:54 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
UPDATE company SET verified_at = '2026-03-20 09:00:00+07' WHERE company_name = 'FPT Software';
UPDATE company SET verified_at = '2026-03-20 09:10:00+07' WHERE company_name = 'VNG Corporation';
UPDATE company SET verified_at = '2026-03-20 09:20:00+07' WHERE company_name = 'Tiki Corporation';
UPDATE company SET verified_at = '2026-03-20 09:30:00+07' WHERE company_name = 'VinAI Research';
UPDATE company SET verified_at = '2026-03-20 09:40:00+07' WHERE company_name = 'CMC Global';
UPDATE company SET verified_at = '2026-03-20 09:50:00+07' WHERE company_name = 'Saigon Technology';
UPDATE company SET verified_at = '2026-03-20 10:00:00+07' WHERE company_name = 'KMS Technology';
UPDATE company SET verified_at = '2026-03-20 10:10:00+07' WHERE company_name = 'TMA Solutions';
UPDATE company SET verified_at = '2026-03-20 10:20:00+07' WHERE company_name = 'Axon Active';
UPDATE company SET verified_at = '2026-03-20 10:30:00+07' WHERE company_name = 'Rikkeisoft';

