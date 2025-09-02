-- Track-Folio: Applications table (job applications per user)

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS applications (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  company     VARCHAR(160) NOT NULL,
  role_title  VARCHAR(160) NOT NULL,
  source      VARCHAR(30)  NOT NULL,
  status      VARCHAR(30)  NOT NULL,
  applied_at  DATE         NOT NULL,
  location    VARCHAR(160),
  salary_text VARCHAR(120),
  job_link    TEXT,
  notes       TEXT,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at  TIMESTAMP NULL,
  -- make ownership mandatory
  owner_id    UUID NOT NULL REFERENCES users(id)
);

ALTER TABLE applications
  ADD CONSTRAINT chk_applications_status
  CHECK (status IN ('APPLIED','INTERVIEW','OFFER','REJECTED','WITHDRAWN'));

ALTER TABLE applications
  ADD CONSTRAINT chk_applications_source
  CHECK (source IN ('LINKEDIN','COMPANY','INDEED','REFERRAL','OTHER'));

CREATE INDEX idx_app_owner       ON applications(owner_id);
CREATE INDEX idx_app_status      ON applications(status);
CREATE INDEX idx_app_applied_at  ON applications(applied_at);
CREATE INDEX idx_app_company_ci  ON applications((lower(company)));
