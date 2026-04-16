-- Migration: recruitment_service - first_version
-- Created: Sat Mar 14 01:08:27 PM +07 2026
-- Author: hoangvuongbui

-- Add your SQL statements below:
-- =========================================================
-- EXTENSIONS
-- =========================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================================
-- 1. COMPANY
-- =========================================================
CREATE TABLE company (
    company_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name TEXT UNIQUE NOT NULL,
    tax_code VARCHAR(20) UNIQUE NOT NULL,
    business_license_url TEXT,
    business_rep_name VARCHAR(255),
    financial_proof_url TEXT,
    logo_url TEXT,
    website_url TEXT,
    province_code VARCHAR(10),
    industry VARCHAR(100),
    size_range VARCHAR(50) NOT NULL,
    overview TEXT NOT NULL,
    background_url TEXT,
    address TEXT,
    created_by UUID,
    updated_by UUID,
    verified_at TIMESTAMPTZ,
    verification_level VARCHAR(50) DEFAULT 'lv3',
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
-- 2. CANDIDATE
-- candidate_id chính là UUID từ auth service — không cần external_auth_id
-- =========================================================
CREATE TABLE candidate (
    candidate_id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(32),
    about TEXT,
    surname VARCHAR(50) NULL,
    name VARCHAR(100) NULL,
    birthday DATE,
    province VARCHAR(100),
    commune VARCHAR(255),
    gender BOOLEAN,
    avatar_url TEXT,
    background_url TEXT,
    reference_link TEXT,
    consent_data_at TIMESTAMPTZ DEFAULT NOW(),
    consent_version VARCHAR(50) NOT NULL,
    cccd VARCHAR(20) UNIQUE,
    cccd_verified_at TIMESTAMPTZ,
    verification_level VARCHAR(50) DEFAULT 'lv3',
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =========================================================
-- 3. CANDIDATE_EDUCATION
-- =========================================================
CREATE TABLE candidate_education (
    education_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
    school_name VARCHAR(255) NOT NULL,
    degree VARCHAR(100),
    field_of_study VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE,
    description TEXT,
    image_url TEXT,
    reference_link TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT  NOW()
);

CREATE INDEX idx_candidate_education_candidate_id ON candidate_education(candidate_id);

-- =========================================================
-- 4. CANDIDATE_CERTIFICATE
-- =========================================================
CREATE TABLE candidate_certificate (
    certificate_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
    certificate_name VARCHAR(255) NOT NULL,
    issuing_organization VARCHAR(255),
    issue_date DATE,
    expiry_date DATE,
    credential_id VARCHAR(255),
    credential_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_candidate_certificate_candidate_id ON candidate_certificate(candidate_id);


-- =========================================================
-- 5. EMPLOYER
-- employer_id chính là UUID từ auth service
-- =========================================================
CREATE TABLE employer (
    employer_id UUID PRIMARY KEY,  --UUID FROM AUTH SERVICE
    company_id UUID NOT NULL REFERENCES company(company_id),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(32),
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    about TEXT,
    birthday DATE,
    province VARCHAR(100),
    commune VARCHAR(255),
    gender BOOLEAN,
    industry VARCHAR(100),
    avatar_url TEXT,
    background_url TEXT,
    reference_link TEXT,
    level VARCHAR(100),
    experience_year NUMERIC(4,1),
    cccd VARCHAR(20) UNIQUE,
    cccd_verified_at TIMESTAMPTZ,
    verified_at TIMESTAMPTZ,
    verification_level VARCHAR(50) DEFAULT 'lv3',
    total_job_posted INTEGER DEFAULT 0,
    consent_data_at TIMESTAMPTZ DEFAULT NOW(),
    consent_version VARCHAR(50) DEFAULT 'v1.0',
    status VARCHAR(50) DEFAULT 'active',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_employer_company_id ON employer(company_id);

-- =========================================================
-- 6. JOB
-- =========================================================
CREATE TABLE job (
    job_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES company(company_id),
    title TEXT,
    description TEXT,
    requirement TEXT,
    img_url TEXT,
    major_preffered VARCHAR(255),
    level VARCHAR (100),
    job_type VARCHAR(100),
    province VARCHAR(100),
    commune VARCHAR(255),
    salary_min NUMERIC(12,2),
    salary_max NUMERIC(12,2),
    experience_min_year NUMERIC(4,1),
    application_deadline DATE,
    application_count INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'active',
    created_by UUID REFERENCES employer(employer_id),
    updated_by UUID REFERENCES employer(employer_id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_job_company_id ON job(company_id);
CREATE INDEX idx_job_created_by ON job(created_by);
CREATE INDEX idx_job_status ON job(status);


-- =========================================================
-- 7. JOB_EMPLOYER
-- =========================================================
CREATE TABLE job_employer (
    job_id UUID NOT NULL REFERENCES job(job_id) ON DELETE CASCADE,
    employer_id UUID NOT NULL REFERENCES employer(employer_id) ON DELETE CASCADE,
    access_role VARCHAR(50) DEFAULT 'editor' CHECK (access_role IN ('owner', 'editor', 'viewer')),
    granted_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY(job_id, employer_id)
);


-- =========================================================
-- 8. RESUME
-- =========================================================
CREATE TABLE resume(
    resume_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id),
    career_goal TEXT,
    major VARCHAR(255),
    experience_year NUMERIC(4,1),
    salary_expectation VARCHAR(100),
    resume_upload_url TEXT,
    img_url TEXT,
    view_count INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'active',
    find_job BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_resume_candidate_id ON resume(candidate_id);

-- =========================================================
-- 9. EXPERIENCE
-- =========================================================
CREATE TABLE experience(
    experience_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES company(company_id),
    headline VARCHAR(255),
    employment_type VARCHAR(100),
    start_date DATE,
    end_date DATE,
    description TEXT,
    location_type VARCHAR(100),
    employment_location VARCHAR(255),
    employment_industry VARCHAR(100),
    is_current BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);



-- =========================================================
-- 10. SKILLS
-- =========================================================
CREATE TABLE skills(
    skills_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skills_name VARCHAR(255) UNIQUE NOT NULL
);



-- =========================================================
-- 11. REPORT
-- =========================================================
CREATE TABLE report(
    report_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) CHECK (type IN ('daily', 'weekly', 'monthly', 'custom')),
    title TEXT,
    description TEXT,
    status VARCHAR(20) DEFAULT 'active',
    data JSONB,
    candidate_id UUID REFERENCES candidate(candidate_id),
    job_id UUID REFERENCES job(job_id),
    employer_id UUID REFERENCES employer(employer_id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT report_has_at_least_one_entity
        CHECK (
            candidate_id IS NOT NULL OR
            job_id IS NOT NULL OR
            employer_id IS NOT NULL
        )
);

CREATE INDEX idx_report_type ON report(type);
CREATE INDEX idx_report_created_at ON report(created_at);
CREATE INDEX idx_report_candidate_id ON report(candidate_id);
CREATE INDEX idx_report_job_id ON report(job_id);
CREATE INDEX idx_report_employer_id ON report(employer_id);


-- =========================================================
-- 12. CANDIDATE_STATISTIC_DAILY
-- =========================================================
CREATE TABLE candidate_statistic_daily (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id),
    date DATE NOT NULL,
    profile_view_count INTEGER DEFAULT 0,
    apply_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (candidate_id, date)
);

CREATE INDEX idx_csd_candidate_date ON candidate_statistic_daily(candidate_id, date);


-- =========================================================
-- 13. RESUME_STATISTIC_DAILY
-- =========================================================
CREATE TABLE resume_statistic_daily(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id UUID NOT NULL REFERENCES resume(resume_id),
    date DATE NOT NULL,
    view_count INTEGER DEFAULT 0,
    download_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (resume_id, date)
);


-- =========================================================
-- 14. JOB_STATISTIC_DAILY
-- =========================================================
CREATE TABLE job_statistic_daily(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES job(job_id),
    date DATE NOT NULL,
    view_count INTEGER DEFAULT 0,
    apply_count INTEGER DEFAULT 0,
    save_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(job_id, date)
);

-- =========================================================
-- 15. EMPLOYER_STATISTIC_DAILY
-- =========================================================
CREATE TABLE employer_statistic_daily (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employer_id UUID NOT NULL REFERENCES employer(employer_id),
    date DATE NOT NULL,
    view_count INTEGER DEFAULT 0,
    job_posted_count INTEGER DEFAULT 0,
    apply_received_count INTEGER DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(employer_id, date)
);


-- =========================================================
-- 16. JUNCTION TABLES
-- =========================================================

-- job_application
CREATE TABLE job_application (
    application_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES job(job_id) ON DELETE CASCADE,
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
    resume_id UUID REFERENCES resume(resume_id),
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN ('pending', 'reviewed', 'interview', 'accepted', 'rejected')),
    applied_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    note TEXT,
    UNIQUE(job_id, candidate_id)
);

CREATE INDEX idx_job_application_candidate_id ON job_application(candidate_id);
CREATE INDEX idx_job_application_job_id ON job_application(job_id);
CREATE INDEX idx_job_application_status ON job_application(status);


-- candidate <> experience
CREATE TABLE candidate_experience(
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
    experience_id UUID NOT NULL REFERENCES experience(experience_id) ON DELETE CASCADE,
    PRIMARY KEY (candidate_id, experience_id)
);

-- skills <> candidate
CREATE TABLE candidate_skills (
    candidate_id UUID NOT NULL REFERENCES candidate(candidate_id) ON DELETE CASCADE,
    skills_id UUID NOT NULL REFERENCES skills(skills_id) ON DELETE CASCADE,
    PRIMARY KEY(candidate_id, skills_id)
);

-- skills <> job
CREATE TABLE job_skills (
    job_id UUID NOT NULL REFERENCES job(job_id) ON DELETE CASCADE,
    skills_id UUID NOT NULL REFERENCES skills(skills_id) ON DELETE CASCADE,
    PRIMARY KEY(job_id, skills_id)
);

-- skills <> experience
CREATE TABLE experience_skills (
    experience_id UUID NOT NULL REFERENCES experience(experience_id) ON DELETE CASCADE,
    skills_id UUID NOT NULL REFERENCES skills(skills_id) ON DELETE CASCADE,
    PRIMARY KEY (experience_id, skills_id)
);


-- =========================================================
-- 17. ALTER TABLE — xử lý circular dependency
-- =========================================================
ALTER TABLE company
    ADD CONSTRAINT fk_company_created_by
        FOREIGN KEY (created_by) REFERENCES employer(employer_id),
    ADD CONSTRAINT fk_company_updated_by
        FOREIGN KEY (updated_by) REFERENCES employer(employer_id);



-- =========================================================
-- 18. INDEX FOR CANDIDATE_SKILL AND EXPERIENCE_COMPANY
-- =========================================================
CREATE INDEX idx_candidate_skills_skill ON candidate_skills(skills_id);
CREATE INDEX idx_experience_company_id ON experience(company_id);