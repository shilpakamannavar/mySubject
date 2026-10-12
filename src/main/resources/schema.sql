-- ══════════════════════════════════════════════════════════════════════
-- CLINICAL TRIALS / STUDIES MANAGEMENT SYSTEM — DATABASE SCHEMA
-- H2 (dev/test) compatible.
-- Production (PostgreSQL) differences noted inline:
--   RANDOM_UUID()           → gen_random_uuid()
--   TIMESTAMP WITH TIME ZONE→ TIMESTAMPTZ
--   CLOB                    → JSONB
--   VARCHAR(45) ip_address  → INET
--   BIGINT GENERATED ...    → BIGSERIAL
--   Partial indexes (WHERE) → supported in PostgreSQL
--   Triggers / functions    → see docs/postgres-triggers.sql
-- ══════════════════════════════════════════════════════════════════════


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 1. MASTER PROVIDER AGREEMENT (MPA)                              │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id              UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    mpa_number          VARCHAR(50)              NOT NULL UNIQUE,
    mpa_name            VARCHAR(200)             NOT NULL,
    organization_name   VARCHAR(200)             NOT NULL,
    contract_type       VARCHAR(30)              NOT NULL
                        CHECK (contract_type IN ('MASTER','SITE_SPECIFIC','AMENDMENT')),
    effective_date      DATE                     NOT NULL,
    expiration_date     DATE,
    status              VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('DRAFT','ACTIVE','EXPIRED','TERMINATED')),
    contact_name        VARCHAR(150),
    contact_email       VARCHAR(200),
    contact_phone       VARCHAR(30),
    notes               TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_mpa_status ON mpa(status);
CREATE INDEX IF NOT EXISTS idx_mpa_number ON mpa(mpa_number);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 2. MEDICAL CENTERS                                              │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS medical_centers (
    center_id           UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    mpa_id              UUID                     NOT NULL REFERENCES mpa(mpa_id),
    center_code         VARCHAR(30)              NOT NULL UNIQUE,
    center_name         VARCHAR(250)             NOT NULL,
    center_type         VARCHAR(30)              NOT NULL
                        CHECK (center_type IN (
                            'HOSPITAL','CLINIC','RESEARCH_INSTITUTE',
                            'UNIVERSITY','PRIVATE_PRACTICE','OTHER'
                        )),
    address_line1       VARCHAR(200),
    address_line2       VARCHAR(200),
    city                VARCHAR(100),
    state               VARCHAR(50),
    zip_code            VARCHAR(20),
    country             VARCHAR(60)              DEFAULT 'US',
    phone               VARCHAR(30),
    fax                 VARCHAR(30),
    email               VARCHAR(200),
    irb_name            VARCHAR(200),
    irb_approval_number VARCHAR(100),
    irb_approval_date   DATE,
    irb_expiry_date     DATE,
    status              VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','CLOSED')),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_center_mpa    ON medical_centers(mpa_id);
CREATE INDEX IF NOT EXISTS idx_center_status ON medical_centers(status);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 3. PRIMARY INVESTIGATORS                                        │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS primary_investigators (
    pi_id               UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    pi_number           VARCHAR(30)              NOT NULL UNIQUE,
    first_name          VARCHAR(100)             NOT NULL,
    last_name           VARCHAR(100)             NOT NULL,
    title               VARCHAR(20),
    credentials         VARCHAR(50),
    specialty           VARCHAR(100),
    email               VARCHAR(200),
    phone               VARCHAR(30)              UNIQUE,
    npi_number          VARCHAR(20),
    license_number      VARCHAR(50),
    license_state       VARCHAR(30),
    cv_document_url     VARCHAR(500),
    status              VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','BLACKLISTED')),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_pi_status ON primary_investigators(status);
CREATE INDEX IF NOT EXISTS idx_pi_name   ON primary_investigators(last_name, first_name);
CREATE INDEX IF NOT EXISTS idx_pi_phone  ON primary_investigators(phone);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 4. PI ↔ MEDICAL CENTER (Many-to-Many)                           │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS pi_center_assignments (
    assignment_id       UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    pi_id               UUID                     NOT NULL REFERENCES primary_investigators(pi_id),
    center_id           UUID                     NOT NULL REFERENCES medical_centers(center_id),
    role                VARCHAR(30)              NOT NULL DEFAULT 'PRIMARY_INVESTIGATOR'
                        CHECK (role IN (
                            'PRIMARY_INVESTIGATOR','CO_INVESTIGATOR',
                            'SUB_INVESTIGATOR','SITE_COORDINATOR'
                        )),
    is_primary_site     BOOLEAN                  DEFAULT FALSE,
    assignment_date     DATE                     NOT NULL DEFAULT CURRENT_DATE,
    end_date            DATE,
    status              VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','INACTIVE','TRANSFERRED')),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pi_center_role UNIQUE (pi_id, center_id, role)
);

CREATE INDEX IF NOT EXISTS idx_pi_center_pi     ON pi_center_assignments(pi_id);
CREATE INDEX IF NOT EXISTS idx_pi_center_center ON pi_center_assignments(center_id);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 5. STUDIES                                                      │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS studies (
    study_id                         UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_number                     VARCHAR(50)              NOT NULL UNIQUE,
    study_name                       VARCHAR(300)             NOT NULL,
    title                            VARCHAR(300),
    phase                            VARCHAR(20)
                                     CHECK (phase IN ('PHASE_I','PHASE_II','PHASE_III','PHASE_IV','OBSERVATIONAL')),
    therapeutic_area                 VARCHAR(100),
    no_of_patients_to_enroll         INTEGER                  NOT NULL DEFAULT 0,
    total_number_of_visits           INTEGER                  NOT NULL DEFAULT 0,
    date_first_patient_last_visit    DATE,
    date_last_patient_first_visit    DATE,
    calculated_rate_per_service      DECIMAL(12,2),
    total_cost                       DECIMAL(12,2),
    status                           VARCHAR(20)              NOT NULL DEFAULT 'INACTIVE'
                                     CHECK (status IN (
                                         'DRAFT','INACTIVE','ACTIVE','COMPLETED',
                                         'SUSPENDED','TERMINATED','DISABLED'
                                     )),
    activated_at                     TIMESTAMP WITH TIME ZONE,
    disabled_at                      TIMESTAMP WITH TIME ZONE,
    disabled_reason                  TEXT,
    created_by_pi_id                 UUID                     NOT NULL REFERENCES primary_investigators(pi_id),
    created_at                       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_study_name ON studies(study_name);
CREATE INDEX IF NOT EXISTS idx_study_status     ON studies(status);
CREATE INDEX IF NOT EXISTS idx_study_pi         ON studies(created_by_pi_id);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 6. STUDY ↔ SITE ASSIGNMENTS                                     │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_site_assignments (
    study_site_id       UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id            UUID                     NOT NULL REFERENCES studies(study_id),
    center_id           UUID                     NOT NULL REFERENCES medical_centers(center_id),
    pi_id               UUID                     NOT NULL REFERENCES primary_investigators(pi_id),
    site_number         VARCHAR(30),
    target_enrollment   INTEGER                  DEFAULT 0,
    actual_enrollment   INTEGER                  DEFAULT 0,
    site_status         VARCHAR(20)              NOT NULL DEFAULT 'PENDING'
                        CHECK (site_status IN (
                            'PENDING','ACTIVATED','ENROLLING','CLOSED','SUSPENDED'
                        )),
    activation_date     DATE,
    closure_date        DATE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_study_site UNIQUE (study_id, center_id, pi_id)
);

CREATE INDEX IF NOT EXISTS idx_study_site_study  ON study_site_assignments(study_id);
CREATE INDEX IF NOT EXISTS idx_study_site_center ON study_site_assignments(center_id);
CREATE INDEX IF NOT EXISTS idx_study_site_pi     ON study_site_assignments(pi_id);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 7. STUDY PLAN (Visit Schedule Template)                         │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_plan_visits (
    plan_visit_id                    UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id                         UUID                     NOT NULL REFERENCES studies(study_id),
    visit_number                     INTEGER                  NOT NULL,
    visit_name                       VARCHAR(100)             NOT NULL,
    visit_type                       VARCHAR(30)              NOT NULL
                                     CHECK (visit_type IN (
                                         'SCREENING','BASELINE','RANDOMIZATION','TREATMENT','FOLLOW_UP',
                                         'END_OF_STUDY','UNSCHEDULED','EARLY_TERMINATION'
                                     )),
    type_of_visit_or_dose            VARCHAR(100),
    duration_days                    INTEGER,
    duration_display                 VARCHAR(50),
    window_period_before             INTEGER                  DEFAULT 0,
    window_period_after              INTEGER                  DEFAULT 0,
    window_period_display            VARCHAR(50),
    day_visit_specific_comp_pattern  VARCHAR(200),
    target_day_offset                INTEGER                  DEFAULT 0,
    is_mandatory                     BOOLEAN                  DEFAULT TRUE,
    requires_fasting                 BOOLEAN                  DEFAULT FALSE,
    procedures                       CLOB                     DEFAULT '[]',  -- JSONB in PostgreSQL
    visit_cost                       DECIMAL(10,2)            DEFAULT 0,
    sort_order                       INTEGER                  NOT NULL DEFAULT 0,
    created_at                       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_study_visit_number UNIQUE (study_id, visit_number)
);

CREATE INDEX IF NOT EXISTS idx_plan_visit_study ON study_plan_visits(study_id);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 8. PATIENTS                                                     │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS patients (
    patient_id          UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    patient_number      VARCHAR(30)              NOT NULL UNIQUE,
    first_name          VARCHAR(100),
    last_name           VARCHAR(100),
    initials            VARCHAR(10),
    date_of_birth       DATE,
    gender              VARCHAR(15)
                        CHECK (gender IN ('MALE','FEMALE','OTHER','UNKNOWN')),
    race                VARCHAR(50),
    ethnicity           VARCHAR(50),
    phone               VARCHAR(30),
    email               VARCHAR(200),
    emergency_contact   VARCHAR(200),
    emergency_phone     VARCHAR(30),
    status              VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                        CHECK (status IN ('ACTIVE','INACTIVE','WITHDRAWN','DECEASED')),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_patient_number ON patients(patient_number);
CREATE INDEX IF NOT EXISTS idx_patient_status ON patients(status);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 9. STUDY ENROLLMENTS (Patient ↔ Study)                          │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_enrollments (
    enrollment_id        UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id             UUID                     NOT NULL REFERENCES studies(study_id),
    patient_id           UUID                     NOT NULL REFERENCES patients(patient_id),
    study_site_id        UUID                     REFERENCES study_site_assignments(study_site_id),
    subject_number       VARCHAR(30),
    screening_date       DATE,
    enrollment_date      DATE,
    randomization_date   DATE,
    randomization_number VARCHAR(50),
    completion_date      DATE,
    withdrawal_date      DATE,
    withdrawal_reason    TEXT,
    informed_consent_date DATE,
    consent_version      VARCHAR(20),
    consent_document_url VARCHAR(500),
    enrollment_status    VARCHAR(25)              NOT NULL DEFAULT 'SCREENING'
                         CHECK (enrollment_status IN (
                             'SCREENING','SCREEN_FAILED','ENROLLED',
                             'RANDOMIZED','ACTIVE','COMPLETED',
                             'WITHDRAWN','LOST_TO_FOLLOWUP','DISCONTINUED'
                         )),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_study_patient UNIQUE (study_id, patient_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollment_study   ON study_enrollments(study_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_patient ON study_enrollments(patient_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_site    ON study_enrollments(study_site_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_status  ON study_enrollments(enrollment_status);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 10. PATIENT VISITS                                              │
-- │     Color codes (UI): COMPLETED=Green, MISSED=Red,             │
-- │     within 7 days=Yellow, future=White                         │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS patient_visits (
    visit_id            UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    enrollment_id       UUID                     NOT NULL REFERENCES study_enrollments(enrollment_id),
    plan_visit_id       UUID                     NOT NULL REFERENCES study_plan_visits(plan_visit_id),
    scheduled_date      DATE                     NOT NULL,
    actual_date         DATE,
    window_open_date    DATE,
    window_close_date   DATE,
    check_in_time       TIMESTAMP WITH TIME ZONE,
    check_out_time      TIMESTAMP WITH TIME ZONE,
    duration_minutes    INTEGER,
    visit_status        VARCHAR(25)              NOT NULL DEFAULT 'SCHEDULED'
                        CHECK (visit_status IN (
                            'SCHEDULED','UPCOMING','IN_PROGRESS','COMPLETED',
                            'MISSED','CANCELLED','RESCHEDULED'
                        )),
    vitals_recorded     BOOLEAN                  DEFAULT FALSE,
    adverse_events      BOOLEAN                  DEFAULT FALSE,
    lab_samples_taken   BOOLEAN                  DEFAULT FALSE,
    procedures_done     CLOB                     DEFAULT '[]',  -- JSONB in PostgreSQL
    investigator_notes  TEXT,
    coordinator_notes   TEXT,
    patient_reported    TEXT,
    performed_by_pi_id  UUID                     REFERENCES primary_investigators(pi_id),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_enrollment_visit UNIQUE (enrollment_id, plan_visit_id)
);

CREATE INDEX IF NOT EXISTS idx_visit_enrollment ON patient_visits(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_visit_status     ON patient_visits(visit_status);
CREATE INDEX IF NOT EXISTS idx_visit_scheduled  ON patient_visits(scheduled_date);
CREATE INDEX IF NOT EXISTS idx_visit_actual     ON patient_visits(actual_date);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 11. PAYMENTS                                                    │
-- │     Study becomes ACTIVE only after STUDY_ACTIVATION payment   │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS payments (
    payment_id          UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id            UUID                     NOT NULL REFERENCES studies(study_id),
    payment_type        VARCHAR(30)              NOT NULL
                        CHECK (payment_type IN (
                            'STUDY_ACTIVATION','VISIT_PAYMENT',
                            'MILESTONE_PAYMENT','AMENDMENT_FEE','CLOSE_OUT'
                        )),
    amount              DECIMAL(12,2)            NOT NULL,
    currency            VARCHAR(3)               DEFAULT 'USD',
    tax_amount          DECIMAL(10,2)            DEFAULT 0,
    total_amount        DECIMAL(12,2)            NOT NULL,
    rate_per_service    DECIMAL(10,2),
    gst_percentage      DECIMAL(5,2)             DEFAULT 0,
    payment_method      VARCHAR(30)
                        CHECK (payment_method IN ('CREDIT_CARD','ACH','WIRE','CHECK','INVOICE')),
    payment_gateway_ref VARCHAR(100),
    invoice_number      VARCHAR(50),
    payment_status      VARCHAR(20)              NOT NULL DEFAULT 'PENDING'
                        CHECK (payment_status IN (
                            'PENDING','PROCESSING','COMPLETED','FAILED',
                            'REFUNDED','CANCELLED'
                        )),
    payment_url         VARCHAR(500),
    paid_at             TIMESTAMP WITH TIME ZONE,
    due_date            DATE,
    visit_id            UUID                     REFERENCES patient_visits(visit_id),
    enrollment_id       UUID                     REFERENCES study_enrollments(enrollment_id),
    notes               TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_payment_study  ON payments(study_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_type   ON payments(payment_type);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 12. STUDY ACCESS CONTROL (RBAC)                                 │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_access (
    access_id           UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id            UUID                     NOT NULL REFERENCES studies(study_id),
    user_id             UUID                     NOT NULL,
    user_role           VARCHAR(30)              NOT NULL
                        CHECK (user_role IN (
                            'PRIMARY_INVESTIGATOR','CO_INVESTIGATOR',
                            'SUB_INVESTIGATOR','CRC','STUDY_COORDINATOR',
                            'DATA_MANAGER','MONITOR','SPONSOR_REP','READ_ONLY'
                        )),
    access_level        VARCHAR(20)              NOT NULL DEFAULT 'READ_WRITE'
                        CHECK (access_level IN ('READ_ONLY','READ_WRITE','ADMIN')),
    granted_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at          TIMESTAMP WITH TIME ZONE,
    is_active           BOOLEAN                  DEFAULT TRUE,
    CONSTRAINT uq_study_user_role UNIQUE (study_id, user_id, user_role)
);

CREATE INDEX IF NOT EXISTS idx_access_study  ON study_access(study_id);
CREATE INDEX IF NOT EXISTS idx_access_user   ON study_access(user_id);
CREATE INDEX IF NOT EXISTS idx_access_active ON study_access(is_active);
-- PostgreSQL: CREATE INDEX idx_access_active ON study_access(is_active) WHERE is_active = true;


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 13. STUDY DOCUMENTS                                             │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_documents (
    document_id         UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_id            UUID                     NOT NULL REFERENCES studies(study_id),
    document_type       VARCHAR(30)              NOT NULL
                        CHECK (document_type IN (
                            'PROTOCOL','CONSENT_FORM','AMENDMENT',
                            'INVESTIGATOR_BROCHURE','CASE_REPORT_FORM',
                            'IRB_APPROVAL','BUDGET','CONTRACT','OTHER'
                        )),
    document_name       VARCHAR(300)             NOT NULL,
    version             VARCHAR(20),
    file_url            VARCHAR(500)             NOT NULL,
    file_size_bytes     BIGINT,
    uploaded_by         UUID,
    uploaded_at         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at         TIMESTAMP WITH TIME ZONE,
    is_current          BOOLEAN                  DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_doc_study ON study_documents(study_id);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 14. AUDIT LOG (FDA 21 CFR Part 11)                              │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS audit_log (
    audit_id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,  -- BIGSERIAL in PostgreSQL
    entity_type          VARCHAR(50)              NOT NULL,
    entity_id            UUID                     NOT NULL,
    action               VARCHAR(30)              NOT NULL,
    old_values           CLOB,                    -- JSONB in PostgreSQL
    new_values           CLOB,                    -- JSONB in PostgreSQL
    changed_fields       CLOB,                    -- JSONB in PostgreSQL
    performed_by         UUID                     NOT NULL,
    performed_by_role    VARCHAR(30),
    ip_address           VARCHAR(45),             -- INET in PostgreSQL
    user_agent           VARCHAR(500),
    reason               TEXT,
    electronic_signature BOOLEAN                  DEFAULT FALSE,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_time   ON audit_log(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_user   ON audit_log(performed_by);


-- ══════════════════════════════════════════════════════════════════════
-- VIEWS
-- ══════════════════════════════════════════════════════════════════════


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ VIEW: My Studies Dashboard                                      │
-- └─────────────────────────────────────────────────────────────────┘

CREATE OR REPLACE VIEW vw_my_studies AS
SELECT
    s.study_id,
    s.study_number,
    s.study_name,
    s.title,
    s.phase,
    s.status                          AS study_status,
    s.no_of_patients_to_enroll,
    s.total_number_of_visits,
    s.date_first_patient_last_visit,
    s.calculated_rate_per_service,
    s.total_cost,
    s.created_by_pi_id,
    pi.first_name || ' ' || pi.last_name AS pi_name,
    pi.credentials                    AS pi_credentials,
    mc.center_name,
    mc.center_code,
    (SELECT COUNT(*)
     FROM study_enrollments se
     WHERE se.study_id = s.study_id
       AND se.enrollment_status NOT IN ('SCREEN_FAILED','WITHDRAWN','DISCONTINUED')
    )                                 AS enrolled_patients,
    (SELECT COUNT(*)
     FROM patient_visits pv
     JOIN study_enrollments se ON se.enrollment_id = pv.enrollment_id
     WHERE se.study_id = s.study_id
       AND pv.visit_status = 'COMPLETED'
    )                                 AS completed_visits,
    (SELECT payment_status
     FROM payments p
     WHERE p.study_id = s.study_id
       AND p.payment_type = 'STUDY_ACTIVATION'
     ORDER BY p.created_at DESC
     LIMIT 1
    )                                 AS activation_payment_status,
    s.created_at,
    s.updated_at
FROM studies s
JOIN primary_investigators pi
     ON pi.pi_id = s.created_by_pi_id
LEFT JOIN study_site_assignments ssa
     ON ssa.study_id = s.study_id AND ssa.pi_id = s.created_by_pi_id
LEFT JOIN medical_centers mc
     ON mc.center_id = ssa.center_id;


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ VIEW: Patient Visit Schedule with Color Coding                  │
-- │  WHITE  = Scheduled > 1 week away                              │
-- │  YELLOW = Scheduled within 1 week                              │
-- │  GREEN  = Completed (✓ watermark)                              │
-- │  RED    = Missed                                               │
-- └─────────────────────────────────────────────────────────────────┘

CREATE OR REPLACE VIEW vw_patient_visit_schedule AS
SELECT
    pv.visit_id,
    pv.enrollment_id,
    se.subject_number,
    p.patient_number,
    p.initials                       AS patient_initials,
    s.study_number,
    s.study_name,
    spv.visit_number,
    spv.visit_name,
    spv.visit_type,
    spv.type_of_visit_or_dose,
    spv.duration_days,
    spv.window_period_before,
    spv.window_period_after,
    pv.scheduled_date,
    pv.actual_date,
    pv.window_open_date,
    pv.window_close_date,
    pv.visit_status,
    CASE
        WHEN pv.visit_status = 'COMPLETED'                                          THEN 'GREEN'
        WHEN pv.visit_status = 'MISSED'                                             THEN 'RED'
        WHEN pv.visit_status = 'CANCELLED'                                          THEN 'GREY'
        WHEN pv.visit_status IN ('SCHEDULED','UPCOMING')
             AND pv.scheduled_date <= DATEADD('DAY', 7, CURRENT_DATE)
             AND pv.scheduled_date >= CURRENT_DATE                                  THEN 'YELLOW'
        WHEN pv.visit_status IN ('SCHEDULED','UPCOMING')
             AND pv.scheduled_date < CURRENT_DATE                                   THEN 'RED'
        ELSE 'WHITE'
    END                              AS display_color,
    CASE
        WHEN pv.visit_status = 'COMPLETED' THEN '✓'
        WHEN pv.visit_status = 'MISSED'    THEN '✗'
        ELSE ''
    END                              AS watermark,
    pv.investigator_notes,
    pv.created_at,
    pv.updated_at
FROM patient_visits pv
JOIN study_enrollments se  ON se.enrollment_id  = pv.enrollment_id
JOIN patients p            ON p.patient_id      = se.patient_id
JOIN studies s             ON s.study_id        = se.study_id
JOIN study_plan_visits spv ON spv.plan_visit_id = pv.plan_visit_id
ORDER BY se.subject_number, spv.visit_number;


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ VIEW: Study Financial Summary                                   │
-- └─────────────────────────────────────────────────────────────────┘

CREATE OR REPLACE VIEW vw_study_financials AS
SELECT
    s.study_id,
    s.study_number,
    s.study_name,
    s.calculated_rate_per_service,
    s.total_cost,
    COUNT(DISTINCT se.enrollment_id)                                                    AS total_enrollments,
    COUNT(DISTINCT CASE WHEN pv.visit_status = 'COMPLETED' THEN pv.visit_id END)       AS completed_visits,
    COALESCE(SUM(CASE WHEN pay.payment_status = 'COMPLETED' THEN pay.total_amount END), 0) AS total_paid,
    COALESCE(SUM(CASE WHEN pay.payment_status = 'PENDING'   THEN pay.total_amount END), 0) AS total_pending,
    s.total_cost - COALESCE(SUM(CASE WHEN pay.payment_status = 'COMPLETED' THEN pay.total_amount END), 0) AS remaining_balance
FROM studies s
LEFT JOIN study_enrollments se ON se.study_id        = s.study_id
LEFT JOIN patient_visits pv    ON pv.enrollment_id   = se.enrollment_id
LEFT JOIN payments pay         ON pay.study_id        = s.study_id
GROUP BY s.study_id, s.study_number, s.study_name,
         s.calculated_rate_per_service, s.total_cost;


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ VIEW: PI Dashboard — Studies Across Centers                     │
-- └─────────────────────────────────────────────────────────────────┘

CREATE OR REPLACE VIEW vw_pi_dashboard AS
SELECT
    pi.pi_id,
    pi.first_name || ' ' || pi.last_name AS pi_name,
    pi.credentials,
    mc.center_id,
    mc.center_name,
    mc.center_code,
    m.mpa_number,
    m.mpa_name,
    COUNT(DISTINCT s.study_id)                                                         AS total_studies,
    COUNT(DISTINCT CASE WHEN s.status = 'ACTIVE' THEN s.study_id END)                 AS active_studies,
    COUNT(DISTINCT se.patient_id)                                                      AS total_patients,
    COUNT(DISTINCT CASE WHEN pv.visit_status = 'SCHEDULED'
          AND pv.scheduled_date BETWEEN CURRENT_DATE AND DATEADD('DAY', 7, CURRENT_DATE)
          THEN pv.visit_id END)                                                        AS upcoming_visits_this_week
FROM primary_investigators pi
JOIN pi_center_assignments pca ON pca.pi_id = pi.pi_id AND pca.status = 'ACTIVE'
JOIN medical_centers mc        ON mc.center_id  = pca.center_id
JOIN mpa m                     ON m.mpa_id      = mc.mpa_id
LEFT JOIN study_site_assignments ssa ON ssa.pi_id = pi.pi_id AND ssa.center_id = mc.center_id
LEFT JOIN studies s            ON s.study_id     = ssa.study_id
LEFT JOIN study_enrollments se ON se.study_id    = s.study_id
LEFT JOIN patient_visits pv    ON pv.enrollment_id = se.enrollment_id
GROUP BY pi.pi_id, pi.first_name, pi.last_name, pi.credentials,
         mc.center_id, mc.center_name, mc.center_code,
         m.mpa_number, m.mpa_name;


-- ══════════════════════════════════════════════════════════════════════
-- NOTE: Triggers and stored functions (auto-activate study on payment,
-- revoke access on disable, generate patient visits on enrollment,
-- updated_at auto-update) require PostgreSQL PL/pgSQL and are not
-- included here. See docs/postgres-triggers.sql for the full scripts.
-- ══════════════════════════════════════════════════════════════════════


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ LEGACY ENTITIES (Customer / Study / Invoice / StudyVisit)       │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS customer (
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL,
    email       VARCHAR(255)  NOT NULL UNIQUE,
    phone       VARCHAR(30),
    created_at  DATE
);

CREATE TABLE IF NOT EXISTS study (
    id                              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    study_id                        VARCHAR(255) NOT NULL UNIQUE,
    title                           VARCHAR(300),
    number_of_patients              INTEGER      NOT NULL,
    total_visits_per_patient        INTEGER      NOT NULL,
    created_by_phone                VARCHAR(30),
    status                          VARCHAR(30)  CHECK (status IN
                                        ('DRAFT','INACTIVE','PAYMENT_PENDING','ACTIVE','DISABLED')),
    customer_id                     BIGINT REFERENCES customer(id)
);

CREATE TABLE IF NOT EXISTS study_visit (
    id                            BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    visit_number                  INTEGER,
    visit_type                    VARCHAR(30),
    duration_type                 VARCHAR(10),
    duration_value                DOUBLE PRECISION,
    duration_from_last_visit_days INTEGER,
    window_minus_days             INTEGER,
    window_plus_days              INTEGER,
    patient_instructions          VARCHAR(1000),
    study_id                      BIGINT NOT NULL REFERENCES study(id)
);

CREATE TABLE IF NOT EXISTS invoice (
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    base_fee          DECIMAL(10,2) NOT NULL,
    gst               DECIMAL(10,2) NOT NULL,
    discount_amount   DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount      DECIMAL(10,2) NOT NULL,
    promo_code_used   VARCHAR(50),
    payment_reference VARCHAR(255),
    status            VARCHAR(30),
    study_id          BIGINT NOT NULL REFERENCES study(id)
);

CREATE TABLE IF NOT EXISTS study_patient (
    id                              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    patient_number                  VARCHAR(20)  NOT NULL UNIQUE,
    patient_initial                 VARCHAR(3)   NOT NULL,
    mobile_number                   VARCHAR(10),
    screening_date                  DATE,
    enrollment_randomization_date   DATE,
    status                          VARCHAR(20)  NOT NULL DEFAULT 'INACTIVE',
    study_id                        BIGINT NOT NULL REFERENCES study(id),
    created_at                      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS promo_code (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    code            VARCHAR(50)   NOT NULL UNIQUE,
    discount_type   VARCHAR(20)   NOT NULL CHECK (discount_type IN ('FULL','PERCENTAGE','FLAT')),
    discount_value  DECIMAL(10,2),
    expiry_date     DATE,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    max_usages      INTEGER,
    used_count      INTEGER       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS promo_code_assignment (
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    promo_code_id   BIGINT        NOT NULL REFERENCES promo_code(id),
    customer_id     BIGINT        REFERENCES customer(id),
    assigned_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_assignment UNIQUE (promo_code_id, customer_id)
);

CREATE TABLE IF NOT EXISTS promo_code_usage_log (
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    promo_code          VARCHAR(50)   NOT NULL,
    customer_id         BIGINT        REFERENCES customer(id),
    customer_name       VARCHAR(255),
    study_business_id   VARCHAR(255)  NOT NULL,
    study_db_id         BIGINT        NOT NULL,
    discount_amount     DECIMAL(10,2) NOT NULL,
    used_at             TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ 15. PATIENT STUDY LOG                                           │
-- │     Auto-generated per-visit tracking grid (CRC dashboard).    │
-- │     One row per visit per enrolled patient. Rule C: isReadOnly  │
-- │     is set to TRUE when the linked study is DISABLED.           │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS patient_study_log (
    log_id                      VARCHAR(50)              NOT NULL PRIMARY KEY,
    patient_number              VARCHAR(20)              NOT NULL,
    enrollment_id               UUID                     NOT NULL
                                REFERENCES study_enrollments(enrollment_id),
    study_id                    UUID                     NOT NULL
                                REFERENCES studies(study_id),
    plan_visit_id               UUID
                                REFERENCES study_plan_visits(plan_visit_id),
    visit_number                INTEGER                  NOT NULL,
    visit_type                  VARCHAR(50),
    target_scheduled_date       DATE,
    window_open_date            DATE,
    window_close_date           DATE,
    actual_visit_date           DATE,
    visit_specific_instructions TEXT,
    visit_status                VARCHAR(20)              NOT NULL DEFAULT 'SCHEDULED'
                                CHECK (visit_status IN (
                                    'SCHEDULED','WINDOW_OPEN','COMPLETED','MISSED','OUT_OF_WINDOW'
                                )),
    is_read_only                BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_psl_enrollment ON patient_study_log(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_psl_study       ON patient_study_log(study_id);
CREATE INDEX IF NOT EXISTS idx_psl_patient     ON patient_study_log(patient_number);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ STUDY TEAMS                                                     │
-- └─────────────────────────────────────────────────────────────────┘

CREATE TABLE IF NOT EXISTS study_teams (
    trial_id             UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    study_name           VARCHAR(300)             NOT NULL,
    pi_id                UUID                     NOT NULL REFERENCES primary_investigators(pi_id),
    created_by_user_id   VARCHAR(200)             NOT NULL,
    created_by_role      VARCHAR(50)              NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_study_teams_pi ON study_teams(pi_id);

CREATE TABLE IF NOT EXISTS study_team_members (
    member_id           UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    trial_id            UUID                     NOT NULL REFERENCES study_teams(trial_id) ON DELETE CASCADE,
    user_id             VARCHAR(200)             NOT NULL,
    member_name         VARCHAR(300),
    email               VARCHAR(200),
    phone               VARCHAR(50),
    role                VARCHAR(30)              NOT NULL
                        CHECK (role IN ('CO_INVESTIGATOR','CRC','CRO','HOSPITAL')),
    co_investigator_id          UUID,
    crc_id                      UUID,
    cro_id                      UUID,
    hospital_id                 UUID,
    can_create_study            BOOLEAN                  NOT NULL DEFAULT FALSE,
    study_creation_granted_at   TIMESTAMP WITH TIME ZONE,
    study_creation_granted_by   VARCHAR(200),
    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_team_members_trial ON study_team_members(trial_id);
CREATE INDEX IF NOT EXISTS idx_team_members_role  ON study_team_members(role);
CREATE INDEX IF NOT EXISTS idx_psl_status      ON patient_study_log(visit_status);


-- ══════════════════════════════════════════════════════════════════════
-- COGNITO-SYNCED ROLE TABLES
-- ══════════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS co_investigators (
    co_investigator_id   UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    ci_number            VARCHAR(50),
    first_name           VARCHAR(100),
    last_name            VARCHAR(100),
    title                VARCHAR(20),
    credentials          VARCHAR(100),
    specialty            VARCHAR(100),
    email                VARCHAR(200)             UNIQUE,
    phone                VARCHAR(50),
    npi_number           VARCHAR(50),
    license_number       VARCHAR(50),
    license_state        VARCHAR(50),
    cv_document_url      VARCHAR(500),
    status               VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_co_inv_email  ON co_investigators(email);
CREATE INDEX IF NOT EXISTS idx_co_inv_phone  ON co_investigators(phone);
CREATE INDEX IF NOT EXISTS idx_co_inv_status ON co_investigators(status);


CREATE TABLE IF NOT EXISTS clinical_research_coordinators (
    crc_id               UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    crc_number           VARCHAR(50),
    first_name           VARCHAR(100),
    last_name            VARCHAR(100),
    title                VARCHAR(20),
    credentials          VARCHAR(100),
    email                VARCHAR(200)             UNIQUE,
    phone                VARCHAR(50),
    certification_number VARCHAR(50),
    status               VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_crc_email  ON clinical_research_coordinators(email);
CREATE INDEX IF NOT EXISTS idx_crc_phone  ON clinical_research_coordinators(phone);
CREATE INDEX IF NOT EXISTS idx_crc_status ON clinical_research_coordinators(status);


CREATE TABLE IF NOT EXISTS contract_research_organizations (
    cro_id               UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    cro_number           VARCHAR(50),
    organization_name    VARCHAR(200),
    contact_person       VARCHAR(200),
    email                VARCHAR(200)             UNIQUE,
    phone                VARCHAR(50),
    address              VARCHAR(500),
    registration_number  VARCHAR(100),
    status               VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cro_email  ON contract_research_organizations(email);
CREATE INDEX IF NOT EXISTS idx_cro_phone  ON contract_research_organizations(phone);
CREATE INDEX IF NOT EXISTS idx_cro_status ON contract_research_organizations(status);


CREATE TABLE IF NOT EXISTS hospitals (
    hospital_id          UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    hospital_number      VARCHAR(50),
    hospital_name        VARCHAR(200),
    contact_person       VARCHAR(200),
    email                VARCHAR(200)             UNIQUE,
    phone                VARCHAR(50),
    address              VARCHAR(500),
    registration_number  VARCHAR(100),
    status               VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hospitals_email  ON hospitals(email);
CREATE INDEX IF NOT EXISTS idx_hospitals_phone  ON hospitals(phone);
CREATE INDEX IF NOT EXISTS idx_hospitals_status ON hospitals(status);


CREATE TABLE IF NOT EXISTS subjects (
    subject_id           UUID                     DEFAULT RANDOM_UUID() PRIMARY KEY,
    subject_number       VARCHAR(50),
    first_name           VARCHAR(100),
    last_name            VARCHAR(100),
    initials             VARCHAR(10),
    date_of_birth        DATE,
    gender               VARCHAR(20),
    email                VARCHAR(200)             UNIQUE,
    phone                VARCHAR(50),
    status               VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE'
                         CHECK (status IN ('ACTIVE','INACTIVE','SUSPENDED','WITHDRAWN')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_subjects_email  ON subjects(email);
CREATE INDEX IF NOT EXISTS idx_subjects_status ON subjects(status);

-- Subject QR-based activation columns (added via ALTER so no-op on fresh schemas that already have them)
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS activation_status   VARCHAR(20)              NOT NULL DEFAULT 'INACTIVE';
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS qr_token            VARCHAR(100)             UNIQUE;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS qr_token_expires_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS activated_at        TIMESTAMP WITH TIME ZONE;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS activated_by        VARCHAR(100);
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS deactivated_at      TIMESTAMP WITH TIME ZONE;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS deactivated_by      VARCHAR(100);
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS patient_id          UUID REFERENCES patients(patient_id);
CREATE INDEX IF NOT EXISTS idx_subjects_qr_token  ON subjects(qr_token);
CREATE INDEX IF NOT EXISTS idx_subjects_patient   ON subjects(patient_id);


-- Migrate existing study_team_members tables (no-op on fresh databases)
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS co_investigator_id          UUID;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS crc_id                      UUID;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS cro_id                      UUID;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS hospital_id                 UUID;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS can_create_study            BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS study_creation_granted_at   TIMESTAMP WITH TIME ZONE;
ALTER TABLE study_team_members ADD COLUMN IF NOT EXISTS study_creation_granted_by   VARCHAR(200);

-- Link studies to study_teams (no-op on fresh databases)
ALTER TABLE studies ADD COLUMN IF NOT EXISTS trial_id UUID REFERENCES study_teams(trial_id);

-- Study template fields (no-op on fresh databases)
ALTER TABLE studies           ADD COLUMN IF NOT EXISTS indication          VARCHAR(500);
ALTER TABLE study_plan_visits ADD COLUMN IF NOT EXISTS duration_value      INTEGER;
ALTER TABLE study_plan_visits ADD COLUMN IF NOT EXISTS duration_unit       VARCHAR(10)
    CHECK (duration_unit IN ('DAYS','WEEKS','MONTHS'));


-- ══════════════════════════════════════════════════════════════════════
-- B2B GST BILLING MODULE
-- Supports CGST+SGST (intra-state) and IGST (inter-state) invoicing,
-- Razorpay B2B order creation, and GSTR-1 monthly export.
-- ══════════════════════════════════════════════════════════════════════

-- ┌─────────────────────────────────────────────────────────────────┐
-- │ GST BILLING PROFILE (doctor/hospital business identity)         │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS gst_billing_profile (
    id                   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    business_name        VARCHAR(250) NOT NULL,
    gstin                VARCHAR(15)  NOT NULL UNIQUE,
    place_of_supply_code VARCHAR(2)   NOT NULL,
    billing_state        VARCHAR(100),
    registered_address   VARCHAR(500),
    business_email       VARCHAR(200),
    itc_eligible         BOOLEAN      NOT NULL DEFAULT TRUE,
    -- Exactly one of pi_id / hospital_id must be set (enforced at application layer).
    pi_id                UUID         REFERENCES primary_investigators(pi_id),
    hospital_id          UUID         REFERENCES hospitals(hospital_id),
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_gbp_gstin ON gst_billing_profile(gstin);

-- ┌─────────────────────────────────────────────────────────────────┐
-- │ TAX INVOICE (Section 31 CGST Act 2017 compliant)               │
-- │ Once status = PAID the row must not be edited.                  │
-- │ Corrections use Credit/Debit Notes (future feature).            │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS tax_invoice (
    id                   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    invoice_number       VARCHAR(20)   NOT NULL UNIQUE,
    invoice_date         DATE          NOT NULL,
    status               VARCHAR(20)   NOT NULL DEFAULT 'DRAFT'
                         CHECK (status IN ('DRAFT','PAID','CANCELLED')),
    tax_type             VARCHAR(15)   NOT NULL
                         CHECK (tax_type IN ('CGST_SGST','IGST')),
    billing_profile_id   BIGINT        NOT NULL REFERENCES gst_billing_profile(id),
    service_description  VARCHAR(500)  NOT NULL,
    sac_code             VARCHAR(10)   NOT NULL,
    base_amount          DECIMAL(12,2) NOT NULL,
    cgst_rate            DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    cgst_amount          DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    sgst_rate            DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    sgst_amount          DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    igst_rate            DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    igst_amount          DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_amount         DECIMAL(12,2) NOT NULL,
    razorpay_order_id    VARCHAR(100),
    razorpay_payment_id  VARCHAR(100),
    place_of_supply      VARCHAR(2)    NOT NULL,
    reference_id         VARCHAR(100),
    subscription_months  INTEGER       NOT NULL DEFAULT 1,
    subscription_start_date DATE,
    subscription_end_date   DATE,
    created_at           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at              TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ti_invoice_number ON tax_invoice(invoice_number);
CREATE INDEX IF NOT EXISTS idx_ti_razorpay_order ON tax_invoice(razorpay_order_id);
CREATE INDEX IF NOT EXISTS idx_ti_status_date    ON tax_invoice(status, invoice_date);

-- Subscription columns added for subscription-based billing (idempotent for existing DBs)
ALTER TABLE tax_invoice ADD COLUMN IF NOT EXISTS subscription_months     INTEGER NOT NULL DEFAULT 1;
ALTER TABLE tax_invoice ADD COLUMN IF NOT EXISTS subscription_start_date DATE;
ALTER TABLE tax_invoice ADD COLUMN IF NOT EXISTS subscription_end_date   DATE;

-- ┌─────────────────────────────────────────────────────────────────┐
-- │ TAX INVOICE SEQUENCE                                            │
-- │ One row per calendar month; next_seq incremented atomically     │
-- │ under pessimistic lock to guarantee sequential invoice numbers. │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS tax_invoice_sequence (
    year_month  VARCHAR(7) PRIMARY KEY,
    next_seq    INT        NOT NULL DEFAULT 1,
    version     INT        NOT NULL DEFAULT 0
);
ALTER TABLE study_plan_visits ADD COLUMN IF NOT EXISTS patient_instruction VARCHAR(2000);


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ CLINICAL VISIT TYPES (reference / lookup table)                 │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS clinical_visit_types (
    code         VARCHAR(50)  NOT NULL PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    description  VARCHAR(500),
    sort_order   INTEGER      NOT NULL DEFAULT 0,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ══════════════════════════════════════════════════════════════════════
-- SUBSCRIPTION BILLING MODULE
-- Monthly flat-rate billing per study; subjects cannot be enrolled
-- until the current month's subscription is PAID.
-- ══════════════════════════════════════════════════════════════════════

-- ┌─────────────────────────────────────────────────────────────────┐
-- │ SUBSCRIPTION CONFIG (singleton row, id=1)                       │
-- │ Admin sets the monthly rate here; default ₹299.                 │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS subscription_config (
    id           BIGINT       NOT NULL DEFAULT 1 PRIMARY KEY,
    monthly_rate DECIMAL(10,2) NOT NULL DEFAULT 299.00,
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(200)
);

-- Seed the default config row so the GET endpoint never returns 404
MERGE INTO subscription_config (id, monthly_rate) KEY(id) VALUES (1, 299.00);

-- ┌─────────────────────────────────────────────────────────────────┐
-- │ STUDY SUBSCRIPTIONS (one row per PI per calendar month)         │
-- │ A single PAID record covers ALL studies owned by that PI.       │
-- └─────────────────────────────────────────────────────────────────┘
CREATE TABLE IF NOT EXISTS study_subscriptions (
    subscription_id          UUID          DEFAULT RANDOM_UUID() PRIMARY KEY,
    pi_id                    UUID          NOT NULL REFERENCES primary_investigators(pi_id),
    billing_year             INTEGER       NOT NULL,
    billing_month            INTEGER       NOT NULL CHECK (billing_month BETWEEN 1 AND 12),
    duration_months          INTEGER       NOT NULL DEFAULT 1,
    subscription_start_date  DATE,
    subscription_end_date    DATE,
    amount                   DECIMAL(10,2) NOT NULL,
    status                   VARCHAR(10)   NOT NULL DEFAULT 'PENDING'
                             CHECK (status IN ('PENDING', 'PAID')),
    paid_at                  TIMESTAMP,
    paid_by                  VARCHAR(200),
    payment_reference        VARCHAR(200),
    created_at               TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (pi_id, billing_year, billing_month)
);

CREATE INDEX IF NOT EXISTS idx_study_sub_pi    ON study_subscriptions(pi_id);
CREATE INDEX IF NOT EXISTS idx_study_sub_month ON study_subscriptions(billing_year, billing_month);

-- Subscription coverage columns added later (idempotent for existing DBs)
ALTER TABLE study_subscriptions ADD COLUMN IF NOT EXISTS duration_months       INTEGER NOT NULL DEFAULT 1;
ALTER TABLE study_subscriptions ADD COLUMN IF NOT EXISTS subscription_start_date DATE;
ALTER TABLE study_subscriptions ADD COLUMN IF NOT EXISTS subscription_end_date   DATE;
