-- =========================================================
-- Drop NOT NULL constraint on employer.name and employer.surname
-- Reason: New onboarding flow creates employer skeleton before
--         user fills in personal info. Name/surname will be
--         updated later via the profile update endpoint.
-- =========================================================

ALTER TABLE employer
    ALTER COLUMN name DROP NOT NULL;

ALTER TABLE employer
    ALTER COLUMN surname DROP NOT NULL;
