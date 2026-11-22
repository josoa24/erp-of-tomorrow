SET ROLE vente_svc;

ALTER TABLE IF EXISTS vente.sales ADD COLUMN IF NOT EXISTS payment_method varchar(20);

ALTER TABLE IF EXISTS vente.sales ADD COLUMN IF NOT EXISTS credit_days integer;

ALTER TABLE IF EXISTS vente.sales ADD COLUMN IF NOT EXISTS to_deliver boolean DEFAULT false;

UPDATE vente.sales SET to_deliver = false WHERE to_deliver IS NULL;

ALTER TABLE IF EXISTS vente.sales ALTER COLUMN to_deliver SET DEFAULT false;

ALTER TABLE IF EXISTS vente.sales ALTER COLUMN to_deliver SET NOT NULL;

ALTER TABLE IF EXISTS vente.sales DROP CONSTRAINT IF EXISTS sales_payment_method_check;

ALTER TABLE IF EXISTS vente.sales ADD CONSTRAINT sales_payment_method_check
    CHECK (payment_method IN ('CASH', 'MVOLA', 'ORANGE_MONEY', 'AIRTEL_MONEY', 'CREDIT'));

ALTER TABLE IF EXISTS vente.sales DROP CONSTRAINT IF EXISTS ck_sales_credit_days_positive;

ALTER TABLE IF EXISTS vente.sales ADD CONSTRAINT ck_sales_credit_days_positive
    CHECK (credit_days IS NULL OR credit_days > 0);

RESET ROLE;
