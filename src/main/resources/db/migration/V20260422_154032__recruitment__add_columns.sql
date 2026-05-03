-- Migration: recruitment - add_columns
-- Created: Wed Apr 22 03:40:32 PM +07 2026
-- Author: mango

-- Add your SQL statements below:

-- =========================================================
-- 1. Bảng lịch sử thay đổi trạng thái đơn ứng tuyển
-- =========================================================
CREATE TABLE application_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by UUID NOT NULL,
    note TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_history_application
        FOREIGN KEY (application_id)
        REFERENCES job_application(application_id) ON DELETE CASCADE,

    CONSTRAINT chk_history_status
        CHECK (new_status IN ('pending', 'reviewed', 'interview', 'accepted', 'rejected', 'cancelled'))
);

CREATE INDEX idx_history_application_id ON application_status_history(application_id);
CREATE INDEX idx_history_changed_by ON application_status_history(changed_by);
CREATE INDEX idx_history_created_at ON application_status_history(created_at);

COMMENT ON TABLE application_status_history IS 'Lịch sử thay đổi trạng thái của đơn ứng tuyển';
COMMENT ON COLUMN application_status_history.old_status IS 'Trạng thái cũ';
COMMENT ON COLUMN application_status_history.new_status IS 'Trạng thái mới';
COMMENT ON COLUMN application_status_history.changed_by IS 'Người thay đổi (candidate_id hoặc employer_id)';
COMMENT ON COLUMN application_status_history.note IS 'Ghi chú lý do thay đổi';


-- =========================================================
-- 2. Bảng thông báo
-- =========================================================
CREATE TABLE notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    related_application_id UUID,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    metadata JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_notification_application
        FOREIGN KEY (related_application_id)
        REFERENCES job_application(application_id) ON DELETE SET NULL,

    CONSTRAINT chk_notification_type
        CHECK (type IN ('NEW_APPLICATION', 'STATUS_CHANGED', 'INTERVIEW_SCHEDULED',
                        'INTERVIEW_REMINDER', 'APPLICATION_CANCELLED', 'OFFER_SENT',
                        'JOIN_REQUEST', 'JOIN_REQUEST_APPROVED', 'JOIN_REQUEST_REJECTED',
                        'COMPANY_APPROVED', 'COMPANY_REJECTED'))
);

CREATE INDEX idx_notification_user_id ON notification(user_id);
CREATE INDEX idx_notification_user_unread ON notification(user_id, is_read);
CREATE INDEX idx_notification_type ON notification(type);
CREATE INDEX idx_notification_created_at ON notification(created_at);
CREATE INDEX idx_notification_related_application ON notification(related_application_id);

COMMENT ON TABLE notification IS 'Thông báo cho candidate và recruiter';
COMMENT ON COLUMN notification.user_id IS 'Người nhận thông báo (candidate_id hoặc employer_id)';
COMMENT ON COLUMN notification.type IS 'Loại thông báo';
COMMENT ON COLUMN notification.is_read IS 'Đã đọc hay chưa';
COMMENT ON COLUMN notification.metadata IS 'Dữ liệu bổ sung dạng JSON';


-- =========================================================
-- 3. Bảng lịch phỏng vấn
-- =========================================================
CREATE TABLE interview_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL,
    scheduled_time TIMESTAMPTZ NOT NULL,
    duration_minutes INTEGER DEFAULT 60,
    meeting_link TEXT,
    meeting_password VARCHAR(50),
    location VARCHAR(255),
    interview_type VARCHAR(50) DEFAULT 'TECHNICAL',
    interviewer_name VARCHAR(255),
    interviewer_email VARCHAR(255),
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    notes TEXT,
    feedback TEXT,
    result VARCHAR(50),
    cancelled_by UUID,
    cancelled_reason TEXT,
    rescheduled_from UUID,
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT fk_interview_application
        FOREIGN KEY (application_id)
        REFERENCES job_application(application_id) ON DELETE CASCADE,

    CONSTRAINT fk_interview_rescheduled
        FOREIGN KEY (rescheduled_from)
        REFERENCES interview_schedule(id) ON DELETE SET NULL,

    CONSTRAINT chk_interview_type
        CHECK (interview_type IN ('TECHNICAL', 'HR', 'MANAGER', 'PRESENTATION', 'TEST')),

    CONSTRAINT chk_interview_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED')),

    CONSTRAINT chk_interview_result
        CHECK (result IN ('PASS', 'FAIL', 'PENDING'))
);

CREATE INDEX idx_interview_application_id ON interview_schedule(application_id);
CREATE INDEX idx_interview_scheduled_time ON interview_schedule(scheduled_time);
CREATE INDEX idx_interview_status ON interview_schedule(status);
CREATE INDEX idx_interview_created_by ON interview_schedule(created_by);

COMMENT ON TABLE interview_schedule IS 'Lịch phỏng vấn của ứng viên';
COMMENT ON COLUMN interview_schedule.scheduled_time IS 'Thời gian phỏng vấn dự kiến';
COMMENT ON COLUMN interview_schedule.duration_minutes IS 'Thời lượng phỏng vấn (phút)';
COMMENT ON COLUMN interview_schedule.meeting_link IS 'Link phỏng vấn online (Google Meet/Zoom)';
COMMENT ON COLUMN interview_schedule.interview_type IS 'Loại phỏng vấn';
COMMENT ON COLUMN interview_schedule.status IS 'Trạng thái lịch phỏng vấn';
COMMENT ON COLUMN interview_schedule.feedback IS 'Feedback sau phỏng vấn';
COMMENT ON COLUMN interview_schedule.result IS 'Kết quả phỏng vấn (PASS/FAIL)';


-- =========================================================
-- 4. Cập nhật job_application thêm ràng buộc status
-- =========================================================
ALTER TABLE job_application
    DROP CONSTRAINT IF EXISTS job_application_status_check;

ALTER TABLE job_application
    ADD CONSTRAINT job_application_status_check
    CHECK (status IN ('pending', 'reviewed', 'interview', 'accepted', 'rejected', 'cancelled'));

COMMENT ON COLUMN job_application.status IS 'Trạng thái: pending, reviewed, interview, accepted, rejected, cancelled';


-- =========================================================
-- 5. Trigger tự động cập nhật updated_at cho interview_schedule
-- =========================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_interview_schedule_updated_at
    BEFORE UPDATE ON interview_schedule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


-- =========================================================
-- 6. Trigger tự động tạo notification khi có thay đổi status
-- =========================================================
CREATE OR REPLACE FUNCTION notify_status_change()
RETURNS TRIGGER AS $$
BEGIN
    -- Chỉ tạo notification khi status thực sự thay đổi
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        -- Thêm vào bảng notification
        INSERT INTO notification (user_id, type, title, content, related_application_id)
        SELECT
            NEW.candidate_id,
            'STATUS_CHANGED',
            'Cập nhật trạng thái hồ sơ',
            CASE NEW.status
                WHEN 'reviewed' THEN 'Hồ sơ của bạn đã được nhà tuyển dụng xem'
                WHEN 'interview' THEN 'Bạn đã được mời phỏng vấn'
                WHEN 'accepted' THEN 'Chúc mừng! Bạn đã được nhận vào vị trí này'
                WHEN 'rejected' THEN 'Rất tiếc, hồ sơ của bạn không phù hợp'
                ELSE 'Trạng thái hồ sơ của bạn đã được cập nhật'
            END,
            NEW.application_id;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_application_status_notification
    AFTER UPDATE OF status ON job_application
    FOR EACH ROW
    EXECUTE FUNCTION notify_status_change();