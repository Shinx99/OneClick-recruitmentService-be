-- Migration: resume - add_columns_is_default
-- Created: Fri Mar 27 05:42:47 PM +07 2026
-- Author: mango

-- Add your SQL statements below:
-- Add isDefault column
ALTER TABLE resume ADD COLUMN is_default BOOLEAN DEFAULT FALSE;


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
