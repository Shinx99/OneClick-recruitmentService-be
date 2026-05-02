-- Migration: resume - add_colume_search_vector
-- Created: Thu Apr 23 04:57:28 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
ALTER TABLE resume
ADD COLUMN search_vector tsvector;

-- GIN index cho fulltext search
CREATE INDEX idx_resume_search_vector ON resume USING GIN(search_vector);

-- Function calculate search_vector (including JSON parsedData)
CREATE OR REPLACE FUNCTION update_resume_search_vector()
RETURNS trigger AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('simple',
            immutable_unaccent(COALESCE(NEW.major, '')) || ' ' ||
            immutable_unaccent(COALESCE(NEW.career_goal, ''))
        ), 'A')
        ||
        setweight(to_tsvector('simple',
            immutable_unaccent(COALESCE(NEW.salary_expectation, '')) || ' ' ||
            COALESCE(CAST(NEW.experience_year AS TEXT), '')
        ), 'B')
        ||
        setweight(
            jsonb_to_tsvector('simple',
            COALESCE(NEW.parsed_data, '{}'::jsonb),
            '["string"]'),
        'C');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger run automatically when INSERT or UPDATE
CREATE TRIGGER resume_search_vector_update
BEFORE INSERT OR UPDATE ON resume
FOR EACH ROW EXECUTE FUNCTION update_resume_search_vector();

-- Backfill
UPDATE resume SET updated_at = updated_at;


