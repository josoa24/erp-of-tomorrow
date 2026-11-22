ALTER TABLE IF EXISTS stock.suppliers ADD COLUMN IF NOT EXISTS city varchar(80);
ALTER TABLE IF EXISTS stock.suppliers ADD COLUMN IF NOT EXISTS nif varchar(20);
ALTER TABLE IF EXISTS stock.suppliers ADD COLUMN IF NOT EXISTS stat varchar(30);
