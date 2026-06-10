-- ══════════════════════════════════════════════════════════════════════
-- CLINICAL TRIALS / STUDIES MANAGEMENT SYSTEM — SAMPLE DATA
-- ══════════════════════════════════════════════════════════════════════
-- NOTE: Section 1 below seeds the *legacy* domain tables (customer, study,
--       study_visit, invoice, promo_code) used by StudyController / StudyUseCase.
--       Section 2 onwards seeds the clinical-domain tables (UUID-keyed).
-- ══════════════════════════════════════════════════════════════════════


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ SECTION 1 — LEGACY DOMAIN TEST DATA (StudyController API)       │
-- └─────────────────────────────────────────────────────────────────┘

-- ── Customers ─────────────────────────────────────────────────────
INSERT INTO customer (name, email, phone, created_at) VALUES
    ('BioTech Pharma Inc.',  'contact@biotechpharma.com',  '+1-800-111-2222', CURRENT_DATE),
    ('HealthCorp Ltd.',      'info@healthcorp.com',         '+1-800-333-4444', CURRENT_DATE);


-- ── Studies ────────────────────────────────────────────────────────
--  ID 1 — DRAFT      (3 visits, 10 patients, Customer 1)
--  ID 2 — ACTIVE     (5 visits, 20 patients, Customer 2, paid)
--  ID 3 — INACTIVE   (4 visits, 15 patients, no customer)
--  ID 4 — PAYMENT_PENDING (3 visits, 8 patients, Customer 1)
-- ───────────────────────────────────────────────────────────────────
INSERT INTO study
    (study_id, indication, number_of_patients, total_visits_per_patient,
     first_patient_screening_date, last_patient_visit_date,
     principal_investigator_name, status, customer_id)
VALUES
    ('STUDY-2026-001', 'Type 2 Diabetes',         10, 5,
     '2026-07-01', '2027-01-15', 'Dr. Sarah Kim',   'DRAFT',           1),

    ('STUDY-2026-002', 'Hypertension',             20, 5,
     '2026-05-01', '2027-05-30', 'Dr. James Wilson', 'ACTIVE',         2),

    ('STUDY-2026-003', 'Chronic Kidney Disease',   15, 4,
     '2026-08-15', '2027-08-15', 'Dr. Priya Sharma', 'INACTIVE',       NULL),

    ('STUDY-2026-004', 'Rheumatoid Arthritis',      8, 3,
     '2026-09-01', '2027-03-01', 'Dr. Mark Lee',    'PAYMENT_PENDING', 1);


-- ── Study Visits ──────────────────────────────────────────────────
-- Study 1 (DRAFT) — 5 visits
INSERT INTO study_visit (visit_number, study_id) VALUES (1, 1), (2, 1), (3, 1), (4, 1), (5, 1);

-- Study 2 (ACTIVE) — 5 visits, fully filled in
INSERT INTO study_visit
    (visit_number, visit_type, duration_from_last_visit_days,
     window_minus_days, window_plus_days, patient_instructions, study_id)
VALUES
    (1, 'SCREENING',  0,  2,  3, 'Fasting 8 hours before visit',          2),
    (2, 'TREATMENT',  30, 3,  3, 'Take medication as prescribed',         2),
    (3, 'TREATMENT',  30, 3,  5, 'BP check + blood draw',                 2),
    (4, 'FOLLOW_UP',  30, 5,  5, 'Complete quality-of-life questionnaire', 2),
    (5, 'FOLLOW_UP',  30, 5,  7, 'Final assessment — no fasting required', 2);

-- Study 3 (INACTIVE) — 4 visits
INSERT INTO study_visit (visit_number, study_id) VALUES (1, 3), (2, 3), (3, 3), (4, 3);

-- Study 4 (PAYMENT_PENDING) — 3 visits
INSERT INTO study_visit (visit_number, study_id) VALUES (1, 4), (2, 4), (3, 4);


-- ── Invoice ───────────────────────────────────────────────────────
--  Study 2 (ACTIVE) — paid in full
--  Study 4 (PAYMENT_PENDING) — awaiting payment
-- ───────────────────────────────────────────────────────────────────
INSERT INTO invoice
    (base_fee, gst, discount_amount, total_amount,
     promo_code_used, payment_reference, status, study_id)
VALUES
    (59000.00, 10620.00, 0.00, 69620.00,
     NULL, 'PAY-REF-20260501-ABC', 'SUCCESS', 2),

    (23600.00,  4248.00, 0.00, 27848.00,
     NULL, NULL, 'INITIATED', 4);


-- ── Promo Codes ───────────────────────────────────────────────────
--  LAUNCH50   — 50% off, no expiry, max 100 uses
--  FLAT1000   — ₹1,000 flat deduction, expires 2026-12-31
--  FREEBIES   — full waiver (100%), max 5 uses
--  EXPIRED24  — expired (for testing error handling)
-- ───────────────────────────────────────────────────────────────────
INSERT INTO promo_code
    (code, discount_type, discount_value, expiry_date, is_active, max_usages, used_count)
VALUES
    ('LAUNCH50',  'PERCENTAGE', 50.00, NULL,         TRUE,  100, 0),
    ('FLAT1000',  'FLAT',       1000.00, '2026-12-31', TRUE,  NULL, 0),
    ('FREEBIES',  'FULL',       NULL,   NULL,         TRUE,  5,   0),
    ('EXPIRED24', 'PERCENTAGE', 20.00, '2024-12-31', FALSE, NULL, 0);

-- ── Promo Code Assignments ────────────────────────────────────────
--  LAUNCH50  → all customers (customer_id NULL)
--  FREEBIES  → all customers (customer_id NULL)
--  FLAT1000  → Customer 1 only (BioTech Pharma)
-- ───────────────────────────────────────────────────────────────────
INSERT INTO promo_code_assignment (promo_code_id, customer_id) VALUES
    (1, NULL),
    (3, NULL),
    (2, 1);


-- ── MPA ────────────────────────────────────────────────────────────────
INSERT INTO mpa (mpa_id, mpa_number, mpa_name, organization_name,
                 contract_type, effective_date, status)
VALUES
    ('a0000001-0000-0000-0000-000000000001',
     'MPA-2026-001', 'Master Agreement - Metro Health Network',
     'Metro Health Network LLC', 'MASTER', '2026-01-01', 'ACTIVE');


-- ── MEDICAL CENTERS ────────────────────────────────────────────────────
--
--  MC-001  Metro General Hospital       (New York, NY)
--  MC-002  Metro Research Institute     (Boston, MA)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO medical_centers
    (center_id, mpa_id, center_code, center_name, center_type, city, state, status)
VALUES
    ('c0000001-0000-0000-0000-000000000001',
     'a0000001-0000-0000-0000-000000000001',
     'MC-001', 'Metro General Hospital', 'HOSPITAL', 'New York', 'NY', 'ACTIVE'),

    ('c0000002-0000-0000-0000-000000000001',
     'a0000001-0000-0000-0000-000000000001',
     'MC-002', 'Metro Research Institute', 'RESEARCH_INSTITUTE', 'Boston', 'MA', 'ACTIVE');


-- ── PRIMARY INVESTIGATORS ──────────────────────────────────────────────
--
--  PI-001  Dr. James Wilson  MD, PhD  Endocrinology
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO primary_investigators
    (pi_id, pi_number, first_name, last_name, title, credentials, specialty, email, status)
VALUES
    ('b0000001-0000-0000-0000-000000000001',
     'PI-001', 'James', 'Wilson', 'Dr.', 'MD, PhD',
     'Endocrinology', 'j.wilson@metroresearch.com', 'ACTIVE');


-- ── PI ↔ CENTER ASSIGNMENTS ────────────────────────────────────────────
--
--  Dr. Wilson is PRIMARY_INVESTIGATOR at BOTH centers
--  MC-001 = primary site
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO pi_center_assignments
    (pi_id, center_id, role, is_primary_site, assignment_date, status)
VALUES
    ('b0000001-0000-0000-0000-000000000001',
     'c0000001-0000-0000-0000-000000000001',
     'PRIMARY_INVESTIGATOR', TRUE, '2026-01-15', 'ACTIVE'),

    ('b0000001-0000-0000-0000-000000000001',
     'c0000002-0000-0000-0000-000000000001',
     'PRIMARY_INVESTIGATOR', FALSE, '2026-02-01', 'ACTIVE');


-- ── STUDY ──────────────────────────────────────────────────────────────
--
--  ABC-XY-001  Efficacy of Drug X in Type 1 Diabetes  PHASE_III
--  Default status = INACTIVE; becomes ACTIVE after STUDY_ACTIVATION payment
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO studies
    (study_id, study_number, study_name, indication, phase,
     no_of_patients_to_enroll, total_number_of_visits,
     date_first_patient_screening, calculated_rate_per_service,
     status, created_by_pi_id)
VALUES
    ('e0000001-0000-0000-0000-000000000001',
     'ABC-XY-001', 'Efficacy of Drug X in Type 1 Diabetes',
     'Type 1 Diabetes', 'PHASE_III',
     50, 5, '2026-04-01', 1500.00,
     'INACTIVE', 'b0000001-0000-0000-0000-000000000001');


-- ── STUDY SITE ASSIGNMENT ──────────────────────────────────────────────
INSERT INTO study_site_assignments
    (study_site_id, study_id, center_id, pi_id,
     site_number, target_enrollment, site_status, activation_date)
VALUES
    ('d0000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'c0000001-0000-0000-0000-000000000001',
     'b0000001-0000-0000-0000-000000000001',
     'SITE-01', 50, 'PENDING', NULL);


-- ── STUDY PLAN VISITS ─────────────────────────────────────────────────
--
--  Visit 1  Screening          Day   0   window  −2 / +3
--  Visit 2  Treatment Dose 2   Day  30   window  −3 / +3
--  Visit 3  Treatment Dose 3   Day  60   window  −3 / +5
--  Visit 4  Follow-Up          Day  90   window  −5 / +5
--  Visit 5  End of Study       Day 120   window  −5 / +7
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO study_plan_visits
    (study_id, visit_number, visit_name, visit_type,
     type_of_visit_or_dose, duration_days, duration_display,
     window_period_before, window_period_after, window_period_display,
     target_day_offset, visit_cost, sort_order)
VALUES
    ('e0000001-0000-0000-0000-000000000001',
     1, 'Screening', 'SCREENING',
     'Screening/Treatment-Dose 1', NULL, 'Duration from visit 1(Days)',
     2, 3, '2 days | 3 days', 0, 300.00, 1),

    ('e0000001-0000-0000-0000-000000000001',
     2, 'Treatment Visit 2', 'TREATMENT',
     'Treatment-Dose 2', 30, '30 days',
     3, 3, '3 days | 3 days', 30, 300.00, 2),

    ('e0000001-0000-0000-0000-000000000001',
     3, 'Treatment Visit 3', 'TREATMENT',
     'Treatment-Dose 3', 60, '60 days',
     3, 5, '3 days | 5 days', 60, 300.00, 3),

    ('e0000001-0000-0000-0000-000000000001',
     4, 'Follow-Up Visit 4', 'FOLLOW_UP',
     'Follow-up', 90, '90 days',
     5, 5, '5 days | 5 days', 90, 300.00, 4),

    ('e0000001-0000-0000-0000-000000000001',
     5, 'End of Study', 'END_OF_STUDY',
     'End of Study', 120, '120 days',
     5, 7, '5 days | 7 days', 120, 300.00, 5);


-- ── PATIENTS ──────────────────────────────────────────────────────────
INSERT INTO patients
    (patient_id, patient_number, first_name, last_name, initials,
     date_of_birth, gender, status)
VALUES
    ('d3000001-0000-0000-0000-000000000001',
     'PAT-2026-001', 'Alice', 'Morgan', 'A.M.',
     '1978-05-12', 'FEMALE', 'ACTIVE'),

    ('d3000002-0000-0000-0000-000000000001',
     'PAT-2026-002', 'Robert', 'Chen', 'R.C.',
     '1965-09-30', 'MALE', 'ACTIVE');


-- ── STUDY ENROLLMENTS ─────────────────────────────────────────────────
INSERT INTO study_enrollments
    (enrollment_id, study_id, patient_id, study_site_id,
     subject_number, screening_date, enrollment_date,
     informed_consent_date, enrollment_status)
VALUES
    ('d1000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'd3000001-0000-0000-0000-000000000001',
     'd0000001-0000-0000-0000-000000000001',
     'ABC-XY-001-001', '2026-04-01', '2026-04-08',
     '2026-04-01', 'ENROLLED'),

    ('d1000002-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'd3000002-0000-0000-0000-000000000001',
     'd0000001-0000-0000-0000-000000000001',
     'ABC-XY-001-002', '2026-04-03', '2026-04-10',
     '2026-04-03', 'ENROLLED');


-- ── PATIENT VISITS ────────────────────────────────────────────────────
--  Patient 1 — visits generated from study plan (base date = 2026-04-08)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO patient_visits
    (enrollment_id, plan_visit_id,
     scheduled_date, window_open_date, window_close_date, visit_status)
SELECT
    'd1000001-0000-0000-0000-000000000001',
    plan_visit_id,
    DATE '2026-04-08' + target_day_offset,
    DATE '2026-04-08' + target_day_offset - window_period_before,
    DATE '2026-04-08' + target_day_offset + window_period_after,
    CASE
        WHEN (DATE '2026-04-08' + target_day_offset) = '2026-04-08' THEN 'COMPLETED'
        ELSE 'SCHEDULED'
    END
FROM study_plan_visits
WHERE study_id = 'e0000001-0000-0000-0000-000000000001'
ORDER BY visit_number;

--  Patient 2 — visits generated from study plan (base date = 2026-04-10)
INSERT INTO patient_visits
    (enrollment_id, plan_visit_id,
     scheduled_date, window_open_date, window_close_date, visit_status)
SELECT
    'd1000002-0000-0000-0000-000000000001',
    plan_visit_id,
    DATE '2026-04-10' + target_day_offset,
    DATE '2026-04-10' + target_day_offset - window_period_before,
    DATE '2026-04-10' + target_day_offset + window_period_after,
    'SCHEDULED'
FROM study_plan_visits
WHERE study_id = 'e0000001-0000-0000-0000-000000000001'
ORDER BY visit_number;


-- ── PAYMENTS ──────────────────────────────────────────────────────────
--  Study activation payment (pending — study is still INACTIVE)
-- ───────────────────────────────────────────────────────────────────────
INSERT INTO payments
    (payment_id, study_id, payment_type,
     amount, tax_amount, total_amount,
     rate_per_service, gst_percentage,
     payment_status, due_date, notes)
VALUES
    ('d2000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'STUDY_ACTIVATION',
     1500.00, 270.00, 1770.00,
     1500.00, 18.00,
     'PENDING', '2026-04-30',
     'Activation payment for ABC-XY-001 — GST 18%');
