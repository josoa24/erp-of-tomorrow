SET ROLE auth_svc;

DO $$
BEGIN
    IF to_regclass('auth.user_accounts') IS NULL THEN
        RETURN;
    END IF;

    IF EXISTS (
        SELECT 1 FROM auth.user_accounts GROUP BY lower(email) HAVING count(*) > 1
    ) THEN
        RAISE NOTICE 'Index unique sur lower(email) ignoré : des e-mails en doublon existent';
        RETURN;
    END IF;

    CREATE UNIQUE INDEX IF NOT EXISTS ux_user_accounts_email_lower
        ON auth.user_accounts (lower(email));
END
$$;

RESET ROLE;
