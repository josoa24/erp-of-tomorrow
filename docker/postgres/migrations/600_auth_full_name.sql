ALTER TABLE IF EXISTS auth.user_accounts ADD COLUMN IF NOT EXISTS full_name varchar(120);
