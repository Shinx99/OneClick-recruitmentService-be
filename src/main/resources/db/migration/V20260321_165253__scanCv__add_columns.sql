-- Migration: scanCv - add_columns
-- Created: Sat Mar 21 04:52:53 PM +07 2026
-- Author: mango

-- Add your SQL statements below:

-- DB: recruitment → resume table
ALTER TABLE resume ADD COLUMN parsed_data JSONB;
--CREATE INDEX CONCURRENTLY idx_resume_parsed_data ON resume USING GIN (parsed_data);
