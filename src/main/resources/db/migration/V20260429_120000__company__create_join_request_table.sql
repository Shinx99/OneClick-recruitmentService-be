CREATE TABLE company_join_request (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id      UUID NOT NULL REFERENCES company(company_id),
    employer_id     UUID NOT NULL REFERENCES employer(employer_id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message         TEXT,
    reviewed_by     UUID,
    reviewed_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Why: prevent duplicate PENDING requests from same employer to same company
CREATE UNIQUE INDEX uq_join_request_pending
    ON company_join_request (company_id, employer_id)
    WHERE status = 'PENDING';

CREATE INDEX idx_join_request_company_status
    ON company_join_request (company_id, status);

CREATE INDEX idx_join_request_employer
    ON company_join_request (employer_id);