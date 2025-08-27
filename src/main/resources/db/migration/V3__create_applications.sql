-- Track-Folio: Applications table (job applications per user)

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE applications (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    company          VARCHAR(160) NOT NULL,
    role_title       VARCHAR(160) NOT NULL,
    source           VARCHAR(40)  NOT NULL DEFAULT 'LINKEDIN', -- LINKEDIN | COMPANY | INDEED | REFERRAL | OTHER
    status           VARCHAR(32)  NOT NULL DEFAULT 'APPLIED',  -- APPLIED | INTERVIEW | OFFER | REJECTED | WITHDRAWN
    applied_at       DATE         NOT NULL DEFAULT CURRENT_DATE,
    location         VARCHAR(160),
    salary_text      VARCHAR(120),     -- free-text like "120k base + 15% bonus"
    job_link         TEXT,             -- URL to job posting
    notes            TEXT,

    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP NULL
);

ALTER TABLE applications
  ADD CONSTRAINT chk_applications_status
  CHECK (status IN ('APPLIED','INTERVIEW','OFFER','REJECTED','WITHDRAWN'));

ALTER TABLE applications
  ADD CONSTRAINT chk_applications_source
  CHECK (source IN ('LINKEDIN','COMPANY','INDEED','REFERRAL','OTHER'));

CREATE INDEX idx_app_owner       ON applications(owner_user_id);
CREATE INDEX idx_app_status      ON applications(status);
CREATE INDEX idx_app_applied_at  ON applications(applied_at);
CREATE INDEX idx_app_company_ci  ON applications((lower(company)));
