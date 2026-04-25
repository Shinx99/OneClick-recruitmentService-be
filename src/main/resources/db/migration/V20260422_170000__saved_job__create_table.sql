-- =========================================================
-- SAVED_JOB (Candidate lưu Job)
-- =========================================================
CREATE TABLE saved_job (
                           candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
                           job_id UUID NOT NULL REFERENCES job(job_id) ON DELETE CASCADE,
                           saved_at TIMESTAMPTZ DEFAULT NOW(),
                           PRIMARY KEY (candidate_id, job_id)
);

CREATE INDEX idx_saved_job_candidate_id ON saved_job(candidate_id);

-- =========================================================
-- Bổ sung cột save_count vào bảng job để theo dõi tổng lượt lưu
-- =========================================================

ALTER TABLE job
    ADD COLUMN save_count INTEGER DEFAULT 0;
