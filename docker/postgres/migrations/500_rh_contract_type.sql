SET ROLE rh_svc;

ALTER TABLE IF EXISTS rh.employees ADD COLUMN IF NOT EXISTS contract_type varchar(16);

ALTER TABLE IF EXISTS rh.employees DROP CONSTRAINT IF EXISTS employees_contract_type_check;

ALTER TABLE IF EXISTS rh.employees ADD CONSTRAINT employees_contract_type_check
    CHECK (contract_type IN ('CDI', 'CDD', 'STAGE', 'JOURNALIER'));

RESET ROLE;
