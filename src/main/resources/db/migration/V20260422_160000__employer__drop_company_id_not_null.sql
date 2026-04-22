-- =========================================================
-- Drop NOT NULL constraint on employer.company_id
-- Reason: New onboarding flow allows recruiter to register
--         (create employer record) BEFORE having a company.
--         Company will be created later via POST /api/recruitment/company.
-- =========================================================

ALTER TABLE employer
    ALTER COLUMN company_id DROP NOT NULL;
