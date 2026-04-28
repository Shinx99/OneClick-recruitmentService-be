-- Migration: resume - add_columns_matchScore
-- Created: Fri Apr 24 05:56:20 PM +07 2026
-- Author: mango

-- Add your SQL statements below:

ALTER TABLE job_application ADD COLUMN match_score DECIMAL(5,2) DEFAULT NULL;


