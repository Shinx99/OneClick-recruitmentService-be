-- Migration: job - add_column_search_vector
-- Created: Fri May  1 04:52:55 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:

ALTER TABLE job
ADD COLUMN IF NOT EXISTS search_vector tsvector;

CREATE INDEX IF NOT EXISTS idx_job_search_vector
ON job USING GIN(search_vector);


-- Function calculate search_vector
CREATE OR REPLACE FUNCTION update_job_search_vector()
RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('simple',
            immutable_unaccent(COALESCE(NEW.title, '')) || ' '
        ), 'A')
        ||
        setweight(to_tsvector('simple',
            immutable_unaccent(COALESCE(NEW.description, '')) || ' ' ||
            immutable_unaccent(COALESCE(NEW.requirement, ''))
        ), 'B');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Trigger run automatically when INSERT or UPDATE
CREATE TRIGGER job_search_vector_update_job_search_vector
BEFORE INSERT OR UPDATE ON job
FOR EACH ROW EXECUTE FUNCTION update_job_search_vector();

-- Backfill
UPDATE job SET updated_at = updated_at;


-- B-Tree for Filter
CREATE INDEX idx_job_province ON job(province);
CREATE INDEX idx_job_level ON job(level);
CREATE INDEX idx_job_job_type ON job(job_type);
CREATE INDEX idx_job_salary ON job(salary_min, salary_max);
CREATE INDEX idx_job_experience ON job(experience_min_year);
