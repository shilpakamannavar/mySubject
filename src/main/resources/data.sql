-- ══════════════════════════════════════════════════════════════════════
-- CLINICAL TRIALS / STUDIES MANAGEMENT SYSTEM — SAMPLE DATA
-- All statements use MERGE INTO so this file is safe to re-run on every
-- startup (file-based H2 persists data across restarts).
-- ══════════════════════════════════════════════════════════════════════


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ SECTION 1 — LEGACY DOMAIN TEST DATA (StudyController API)       │
-- └─────────────────────────────────────────────────────────────────┘

MERGE INTO customer (id, name, email, phone, created_at) KEY(id) VALUES
    (1, 'BioTech Pharma Inc.',  'contact@biotechpharma.com',  '+1-800-111-2222', CURRENT_DATE),
    (2, 'HealthCorp Ltd.',      'info@healthcorp.com',         '+1-800-333-4444', CURRENT_DATE);


MERGE INTO study
    (id, study_id, number_of_patients, total_visits_per_patient,
     status, customer_id)
KEY(id) VALUES
    (1, 'STUDY-2026-001', 10, 5, 'DRAFT',           1),
    (2, 'STUDY-2026-002', 20, 5, 'ACTIVE',          2),
    (3, 'STUDY-2026-003', 15, 4, 'INACTIVE',        NULL),
    (4, 'STUDY-2026-004',  8, 3, 'PAYMENT_PENDING', 1);


-- Study 1 (DRAFT) — 5 visits
MERGE INTO study_visit (id, visit_number, study_id) KEY(id) VALUES
    (1, 1, 1), (2, 2, 1), (3, 3, 1), (4, 4, 1), (5, 5, 1);

-- Study 2 (ACTIVE) — 5 visits
MERGE INTO study_visit
    (id, visit_number, visit_type, duration_from_last_visit_days,
     window_minus_days, window_plus_days, patient_instructions, study_id)
KEY(id) VALUES
    (6,  1, 'SCREENING',  0,  2, 3, 'Fasting 8 hours before visit',           2),
    (7,  2, 'TREATMENT',  30, 3, 3, 'Take medication as prescribed',          2),
    (8,  3, 'TREATMENT',  30, 3, 5, 'BP check + blood draw',                  2),
    (9,  4, 'FOLLOW_UP',  30, 5, 5, 'Complete quality-of-life questionnaire', 2),
    (10, 5, 'FOLLOW_UP',  30, 5, 7, 'Final assessment — no fasting required', 2);

-- Study 3 (INACTIVE) — 4 visits
MERGE INTO study_visit (id, visit_number, study_id) KEY(id) VALUES
    (11, 1, 3), (12, 2, 3), (13, 3, 3), (14, 4, 3);

-- Study 4 (PAYMENT_PENDING) — 3 visits
MERGE INTO study_visit (id, visit_number, study_id) KEY(id) VALUES
    (15, 1, 4), (16, 2, 4), (17, 3, 4);


MERGE INTO invoice
    (id, base_fee, gst, discount_amount, total_amount,
     promo_code_used, payment_reference, status, study_id)
KEY(id) VALUES
    (1, 59000.00, 10620.00, 0.00, 69620.00, NULL, 'PAY-REF-20260501-ABC', 'SUCCESS',   2),
    (2, 23600.00,  4248.00, 0.00, 27848.00, NULL, NULL,                   'INITIATED', 4);


MERGE INTO promo_code
    (id, code, discount_type, discount_value, expiry_date, is_active, max_usages, used_count)
KEY(id) VALUES
    (1, 'LAUNCH50',  'PERCENTAGE', 50.00,   NULL,         TRUE,  100,  0),
    (2, 'FLAT1000',  'FLAT',       1000.00, '2026-12-31', TRUE,  NULL, 0),
    (3, 'FREEBIES',  'FULL',       NULL,    NULL,         TRUE,  5,    0),
    (4, 'EXPIRED24', 'PERCENTAGE', 20.00,   '2024-12-31', FALSE, NULL, 0);


MERGE INTO promo_code_assignment (id, promo_code_id, customer_id) KEY(id) VALUES
    (1, 1, NULL),
    (2, 3, NULL),
    (3, 2, 1);


-- Reset identity sequences above the explicitly-seeded IDs so that
-- new application-inserted rows never collide with seed data.
ALTER TABLE customer              ALTER COLUMN id RESTART WITH 10000;
ALTER TABLE study                 ALTER COLUMN id RESTART WITH 10000;
ALTER TABLE study_visit           ALTER COLUMN id RESTART WITH 10000;
ALTER TABLE invoice               ALTER COLUMN id RESTART WITH 10000;
ALTER TABLE promo_code            ALTER COLUMN id RESTART WITH 10000;
ALTER TABLE promo_code_assignment ALTER COLUMN id RESTART WITH 10000;


-- ┌─────────────────────────────────────────────────────────────────┐
-- │ SECTION 2 — CLINICAL DOMAIN DATA (UUID-keyed tables)            │
-- └─────────────────────────────────────────────────────────────────┘

MERGE INTO mpa
    (mpa_id, mpa_number, mpa_name, organization_name,
     contract_type, effective_date, status)
KEY(mpa_id) VALUES
    ('a0000001-0000-0000-0000-000000000001',
     'MPA-2026-001', 'Master Agreement - Metro Health Network',
     'Metro Health Network LLC', 'MASTER', '2026-01-01', 'ACTIVE');


MERGE INTO medical_centers
    (center_id, mpa_id, center_code, center_name, center_type, city, state, status)
KEY(center_id) VALUES
    ('c0000001-0000-0000-0000-000000000001',
     'a0000001-0000-0000-0000-000000000001',
     'MC-001', 'Metro General Hospital',   'HOSPITAL',            'New York', 'NY', 'ACTIVE'),
    ('c0000002-0000-0000-0000-000000000001',
     'a0000001-0000-0000-0000-000000000001',
     'MC-002', 'Metro Research Institute', 'RESEARCH_INSTITUTE',  'Boston',   'MA', 'ACTIVE');


MERGE INTO primary_investigators
    (pi_id, pi_number, first_name, last_name, title, credentials, specialty, email, status)
KEY(pi_id) VALUES
    ('b0000001-0000-0000-0000-000000000001',
     'PI-001', 'James', 'Wilson', 'Dr.', 'MD, PhD',
     'Endocrinology', 'j.wilson@metroresearch.com', 'ACTIVE');


MERGE INTO pi_center_assignments
    (assignment_id, pi_id, center_id, role, is_primary_site, assignment_date, status)
KEY(pi_id, center_id, role) VALUES
    ('a0000001-0000-0000-0000-000000000001',
     'b0000001-0000-0000-0000-000000000001',
     'c0000001-0000-0000-0000-000000000001',
     'PRIMARY_INVESTIGATOR', TRUE, '2026-01-15', 'ACTIVE'),
    ('a0000002-0000-0000-0000-000000000001',
     'b0000001-0000-0000-0000-000000000001',
     'c0000002-0000-0000-0000-000000000001',
     'PRIMARY_INVESTIGATOR', FALSE, '2026-02-01', 'ACTIVE');


MERGE INTO studies
    (study_id, study_number, study_name, phase,
     no_of_patients_to_enroll, total_number_of_visits,
     calculated_rate_per_service,
     status, created_by_pi_id)
KEY(study_id) VALUES
    ('e0000001-0000-0000-0000-000000000001',
     'ABC-XY-001', 'Efficacy of Drug X in Type 1 Diabetes', 'PHASE_III',
     50, 5, 1500.00,
     'ACTIVE', 'b0000001-0000-0000-0000-000000000001');

-- Keep the API-created Type II study permanently ACTIVE.
UPDATE studies
   SET status = 'ACTIVE'
 WHERE study_id = 'cec5cdcb-7c41-448d-bdd9-3747bcd825fb'
   AND status != 'ACTIVE';


MERGE INTO study_site_assignments
    (study_site_id, study_id, center_id, pi_id,
     site_number, target_enrollment, site_status, activation_date)
KEY(study_site_id) VALUES
    ('d0000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'c0000001-0000-0000-0000-000000000001',
     'b0000001-0000-0000-0000-000000000001',
     'SITE-01', 50, 'PENDING', NULL);


-- Explicit plan_visit_id UUIDs so patient_visits can reference them safely
MERGE INTO study_plan_visits
    (plan_visit_id, study_id, visit_number, visit_name, visit_type,
     type_of_visit_or_dose, duration_days, duration_display,
     window_period_before, window_period_after, window_period_display,
     target_day_offset, visit_cost, sort_order)
KEY(plan_visit_id) VALUES
    ('f0000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     1, 'Screening',         'SCREENING',
     'Screening/Treatment-Dose 1', NULL, 'Duration from visit 1(Days)',
     2, 3, '2 days | 3 days',   0,   300.00, 1),

    ('f0000002-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     2, 'Treatment Visit 2', 'TREATMENT',
     'Treatment-Dose 2', 30, '30 days',
     3, 3, '3 days | 3 days',  30,   300.00, 2),

    ('f0000003-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     3, 'Treatment Visit 3', 'TREATMENT',
     'Treatment-Dose 3', 60, '60 days',
     3, 5, '3 days | 5 days',  60,   300.00, 3),

    ('f0000004-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     4, 'Follow-Up Visit 4', 'FOLLOW_UP',
     'Follow-up', 90, '90 days',
     5, 5, '5 days | 5 days',  90,   300.00, 4),

    ('f0000005-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     5, 'End of Study',       'END_OF_STUDY',
     'End of Study', 120, '120 days',
     5, 7, '5 days | 7 days', 120,   300.00, 5);


MERGE INTO patients
    (patient_id, patient_number, first_name, last_name, initials,
     date_of_birth, gender, status)
KEY(patient_id) VALUES
    ('d3000001-0000-0000-0000-000000000001',
     'PAT-2026-001', 'Alice',  'Morgan', 'A.M.', '1978-05-12', 'FEMALE', 'ACTIVE'),
    ('d3000002-0000-0000-0000-000000000001',
     'PAT-2026-002', 'Robert', 'Chen',   'R.C.', '1965-09-30', 'MALE',   'ACTIVE');


MERGE INTO study_enrollments
    (enrollment_id, study_id, patient_id, study_site_id,
     subject_number, screening_date, enrollment_date,
     informed_consent_date, enrollment_status)
KEY(enrollment_id) VALUES
    ('d1000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'd3000001-0000-0000-0000-000000000001',
     'd0000001-0000-0000-0000-000000000001',
     'ABC-XY-001-001', '2026-04-01', '2026-04-08', '2026-04-01', 'ENROLLED'),
    ('d1000002-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'd3000002-0000-0000-0000-000000000001',
     'd0000001-0000-0000-0000-000000000001',
     'ABC-XY-001-002', '2026-04-03', '2026-04-10', '2026-04-03', 'ENROLLED');


-- Patient 1 visits (base date 2026-04-08)
-- Patient 2 visits (base date 2026-04-10)
-- Dates pre-computed from study_plan_visits target_day_offset + window values.
MERGE INTO patient_visits
    (visit_id, enrollment_id, plan_visit_id,
     scheduled_date, window_open_date, window_close_date, visit_status)
KEY(visit_id) VALUES
    -- Patient 1
    ('e0000001-0000-0000-0000-000000000001', 'd1000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', '2026-04-08', '2026-04-06', '2026-04-11', 'COMPLETED'),
    ('e0000002-0000-0000-0000-000000000001', 'd1000001-0000-0000-0000-000000000001', 'f0000002-0000-0000-0000-000000000001', '2026-05-08', '2026-05-05', '2026-05-11', 'SCHEDULED'),
    ('e0000003-0000-0000-0000-000000000001', 'd1000001-0000-0000-0000-000000000001', 'f0000003-0000-0000-0000-000000000001', '2026-06-07', '2026-06-04', '2026-06-12', 'SCHEDULED'),
    ('e0000004-0000-0000-0000-000000000001', 'd1000001-0000-0000-0000-000000000001', 'f0000004-0000-0000-0000-000000000001', '2026-07-07', '2026-07-02', '2026-07-12', 'SCHEDULED'),
    ('e0000005-0000-0000-0000-000000000001', 'd1000001-0000-0000-0000-000000000001', 'f0000005-0000-0000-0000-000000000001', '2026-08-06', '2026-08-01', '2026-08-13', 'SCHEDULED'),
    -- Patient 2
    ('e0000006-0000-0000-0000-000000000001', 'd1000002-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', '2026-04-10', '2026-04-08', '2026-04-13', 'SCHEDULED'),
    ('e0000007-0000-0000-0000-000000000001', 'd1000002-0000-0000-0000-000000000001', 'f0000002-0000-0000-0000-000000000001', '2026-05-10', '2026-05-07', '2026-05-13', 'SCHEDULED'),
    ('e0000008-0000-0000-0000-000000000001', 'd1000002-0000-0000-0000-000000000001', 'f0000003-0000-0000-0000-000000000001', '2026-06-09', '2026-06-06', '2026-06-14', 'SCHEDULED'),
    ('e0000009-0000-0000-0000-000000000001', 'd1000002-0000-0000-0000-000000000001', 'f0000004-0000-0000-0000-000000000001', '2026-07-09', '2026-07-04', '2026-07-14', 'SCHEDULED'),
    ('e0000010-0000-0000-0000-000000000001', 'd1000002-0000-0000-0000-000000000001', 'f0000005-0000-0000-0000-000000000001', '2026-08-08', '2026-08-03', '2026-08-15', 'SCHEDULED');


-- Seed paid subscriptions for both test PIs so enrollment works without going through billing
-- ┌─────────────────────────────────────────────────────────────────┐
-- │ CLINICAL VISIT TYPES — seed rows (idempotent)                   │
-- └─────────────────────────────────────────────────────────────────┘
MERGE INTO clinical_visit_types (code, display_name, description, sort_order, is_active) KEY(code) VALUES
    ('SCREENING',         'Screening',          'Initial visit to assess patient eligibility before enrolment', 1, TRUE),
    ('BASELINE',          'Baseline',           'Pre-treatment measurement visit establishing baseline values',  2, TRUE),
    ('RANDOMIZATION',     'Randomization',      'Visit at which the patient is randomized to a study arm',      3, TRUE),
    ('TREATMENT',         'Treatment',          'Scheduled visit during the active treatment period',           4, TRUE),
    ('FOLLOW_UP',         'Follow-Up',          'Post-treatment follow-up visit to monitor patient outcomes',   5, TRUE),
    ('END_OF_STUDY',      'End of Study',       'Final study visit marking completion of the protocol',        6, TRUE),
    ('EARLY_TERMINATION', 'Early Termination',  'Visit for patients who withdraw or are discontinued early',   7, TRUE),
    ('UNSCHEDULED',       'Unscheduled',        'Unplanned visit not part of the original study schedule',     8, TRUE);


MERGE INTO study_subscriptions
    (pi_id, billing_year, billing_month, duration_months,
     subscription_start_date, subscription_end_date,
     amount, status, paid_at, paid_by, payment_reference)
KEY(pi_id, billing_year, billing_month) VALUES
    ('b0000001-0000-0000-0000-000000000001', 2026, 9, 3,
     '2026-09-01', '2026-11-30', 897.00, 'PAID',
     CURRENT_TIMESTAMP, 'seed', 'SEED-SUB-001'),
    ('08f3df98-1d08-41e1-a518-8094ef0c638e', 2026, 9, 3,
     '2026-09-01', '2026-11-30', 897.00, 'PAID',
     CURRENT_TIMESTAMP, 'seed', 'SEED-SUB-002');

-- Paid subscription for PI who created the Type II study (8d2d89e6, phone +919834309137).
MERGE INTO study_subscriptions
    (pi_id, billing_year, billing_month, duration_months,
     subscription_start_date, subscription_end_date,
     amount, status, paid_at, paid_by, payment_reference)
KEY(pi_id, billing_year, billing_month) VALUES
    ('8d2d89e6-69c6-4947-a409-8f61d8930848', 2026, 9, 3,
     '2026-09-01', '2026-11-30', 897.00, 'PAID',
     CURRENT_TIMESTAMP, 'seed', 'SEED-SUB-TYPE2-PI-FIX');

MERGE INTO payments
    (payment_id, study_id, payment_type,
     amount, tax_amount, total_amount,
     rate_per_service, gst_percentage,
     payment_status, due_date, notes)
KEY(payment_id) VALUES
    ('d2000001-0000-0000-0000-000000000001',
     'e0000001-0000-0000-0000-000000000001',
     'STUDY_ACTIVATION',
     1500.00, 270.00, 1770.00,
     1500.00, 18.00,
     'PENDING', '2026-04-30',
     'Activation payment for ABC-XY-001 — GST 18%');
