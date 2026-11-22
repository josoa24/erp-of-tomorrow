ALTER TABLE IF EXISTS vente.customers ADD COLUMN IF NOT EXISTS nif varchar(20);
ALTER TABLE IF EXISTS vente.customers ADD COLUMN IF NOT EXISTS stat varchar(30);
ALTER TABLE IF EXISTS vente.customers ADD COLUMN IF NOT EXISTS credit_limit numeric(14,2);

DO $$
BEGIN
    IF to_regclass('vente.customers') IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'ck_customers_credit_limit_non_negative'
          AND conrelid = 'vente.customers'::regclass
    ) THEN
        ALTER TABLE vente.customers
            ADD CONSTRAINT ck_customers_credit_limit_non_negative
            CHECK (credit_limit IS NULL OR credit_limit >= 0);
    END IF;
END
$$;
