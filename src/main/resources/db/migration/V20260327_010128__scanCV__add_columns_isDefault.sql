-- Migration: scanCV - add_columns_isDefault
-- Created: Fri Mar 27 01:01:28 AM +07 2026
-- Author: mango

-- Add your SQL statements below:

-- Add isDefault column
ALTER TABLE resume ADD COLUMN is_default BOOLEAN DEFAULT FALSE;

-- 2. Add unique constraint (candidate_id, is_default)
ALTER TABLE resume
ADD CONSTRAINT uk_candidate_default
UNIQUE (candidate_id, is_default);

-- 3. Set first CV default
UPDATE resume
SET is_default = TRUE
WHERE resume_id IN (
    SELECT resume_id
    FROM (
        SELECT resume_id,
               ROW_NUMBER() OVER (PARTITION BY candidate_id ORDER BY created_at ASC) as rn
        FROM resume
        WHERE is_default IS NULL OR is_default = FALSE
    ) numbered
    WHERE rn = 1
);