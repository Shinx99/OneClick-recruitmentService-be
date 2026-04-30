ALTER TABLE notification DROP CONSTRAINT chk_notification_type;

ALTER TABLE notification ADD CONSTRAINT chk_notification_type
    CHECK (type IN (
        'NEW_APPLICATION',
        'STATUS_CHANGED',
        'INTERVIEW_SCHEDULED',
        'INTERVIEW_REMINDER',
        'APPLICATION_CANCELLED',
        'OFFER_SENT',
        'JOIN_REQUEST',
        'JOIN_REQUEST_APPROVED',
        'JOIN_REQUEST_REJECTED'
    ));
