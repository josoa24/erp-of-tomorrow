SET ROLE rh_svc;

ALTER TABLE IF EXISTS rh.leave_requests ADD COLUMN IF NOT EXISTS decided_by bigint;

RESET ROLE;
