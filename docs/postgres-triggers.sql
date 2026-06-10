-- ══════════════════════════════════════════════════════════════════════
-- POSTGRESQL-ONLY: FUNCTIONS & TRIGGERS
-- Apply these on a PostgreSQL instance AFTER running schema.sql.
-- These are NOT executed in the H2 dev environment.
-- ══════════════════════════════════════════════════════════════════════


-- ── Auto-activate study after successful STUDY_ACTIVATION payment ─────
CREATE OR REPLACE FUNCTION fn_activate_study_on_payment()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.payment_type = 'STUDY_ACTIVATION'
       AND NEW.payment_status = 'COMPLETED'
       AND OLD.payment_status != 'COMPLETED' THEN
        UPDATE studies
        SET status       = 'ACTIVE',
            activated_at = now(),
            updated_at   = now()
        WHERE study_id = NEW.study_id
          AND status   = 'INACTIVE';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_activate_study_on_payment
    AFTER UPDATE ON payments
    FOR EACH ROW
    EXECUTE FUNCTION fn_activate_study_on_payment();


-- ── Auto-revoke access when study is DISABLED ─────────────────────────
--  PI retains access; all other roles lose access immediately.
CREATE OR REPLACE FUNCTION fn_revoke_access_on_disable()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'DISABLED' AND OLD.status != 'DISABLED' THEN
        UPDATE study_access
        SET is_active  = false,
            revoked_at = now()
        WHERE study_id  = NEW.study_id
          AND is_active = true
          AND user_role != 'PRIMARY_INVESTIGATOR';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_revoke_access_on_disable
    AFTER UPDATE ON studies
    FOR EACH ROW
    EXECUTE FUNCTION fn_revoke_access_on_disable();


-- ── Auto-generate patient_visits rows on enrollment ───────────────────
--  Fires when enrollment_status transitions to 'ENROLLED'.
CREATE OR REPLACE FUNCTION fn_generate_patient_visits()
RETURNS TRIGGER AS $$
DECLARE
    v_plan      RECORD;
    v_base_date DATE;
BEGIN
    IF NEW.enrollment_status = 'ENROLLED'
       AND (OLD IS NULL OR OLD.enrollment_status != 'ENROLLED') THEN

        v_base_date := COALESCE(NEW.enrollment_date, CURRENT_DATE);

        FOR v_plan IN
            SELECT * FROM study_plan_visits
            WHERE study_id = NEW.study_id
            ORDER BY visit_number
        LOOP
            INSERT INTO patient_visits (
                enrollment_id, plan_visit_id,
                scheduled_date, window_open_date, window_close_date, visit_status
            ) VALUES (
                NEW.enrollment_id,
                v_plan.plan_visit_id,
                v_base_date + v_plan.target_day_offset,
                v_base_date + v_plan.target_day_offset - v_plan.window_period_before,
                v_base_date + v_plan.target_day_offset + v_plan.window_period_after,
                CASE
                    WHEN (v_base_date + v_plan.target_day_offset)
                             BETWEEN CURRENT_DATE AND CURRENT_DATE + 7
                    THEN 'UPCOMING'
                    ELSE 'SCHEDULED'
                END
            )
            ON CONFLICT (enrollment_id, plan_visit_id) DO NOTHING;
        END LOOP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_generate_patient_visits
    AFTER INSERT OR UPDATE ON study_enrollments
    FOR EACH ROW
    EXECUTE FUNCTION fn_generate_patient_visits();


-- ── Scheduled job: update visit status based on date proximity ────────
--  Run daily via pg_cron or an external scheduler.
CREATE OR REPLACE FUNCTION fn_update_visit_status()
RETURNS void AS $$
BEGIN
    UPDATE patient_visits
    SET visit_status = 'UPCOMING', updated_at = now()
    WHERE visit_status = 'SCHEDULED'
      AND scheduled_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '7 days';

    UPDATE patient_visits
    SET visit_status = 'MISSED', updated_at = now()
    WHERE visit_status IN ('SCHEDULED', 'UPCOMING')
      AND window_close_date < CURRENT_DATE;
END;
$$ LANGUAGE plpgsql;


-- ── updated_at auto-update trigger ────────────────────────────────────
CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_updated_at_mpa         BEFORE UPDATE ON mpa                   FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_centers     BEFORE UPDATE ON medical_centers        FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_pi          BEFORE UPDATE ON primary_investigators  FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_studies     BEFORE UPDATE ON studies                FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_patients    BEFORE UPDATE ON patients               FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_enrollments BEFORE UPDATE ON study_enrollments      FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_visits      BEFORE UPDATE ON patient_visits         FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
CREATE TRIGGER trg_updated_at_payments    BEFORE UPDATE ON payments               FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
