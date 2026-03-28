-- Migration: cvProfile - add_columns_resume_deleted_at
-- Created: Fri Mar 27 02:18:58 PM +07 2026
-- Author: mango

-- Add your SQL statements below:
-- Add deleted_at column
ALTER TABLE resume
ADD COLUMN deleted_at TIMESTAMPTZ
NULL;

-- Optional (nếu cần, không bắt buộc)
COMMENT ON COLUMN resume.deleted_at IS 'Thời điểm CV bị xoá mềm (soft delete)';
