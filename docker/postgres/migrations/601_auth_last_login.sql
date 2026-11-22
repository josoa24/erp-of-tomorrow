SET ROLE auth_svc;

DO $$
BEGIN
    IF to_regclass('auth.user_accounts') IS NULL THEN
        RETURN;
    END IF;

    ALTER TABLE auth.user_accounts ADD COLUMN IF NOT EXISTS last_login_at timestamp(6) with time zone;
END
$$;

RESET ROLE;
