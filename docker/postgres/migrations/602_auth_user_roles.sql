SET ROLE auth_svc;

DO $$
BEGIN
    IF to_regclass('auth.user_accounts') IS NULL THEN
        RETURN;
    END IF;

    CREATE TABLE IF NOT EXISTS auth.user_roles (
        user_id bigint NOT NULL REFERENCES auth.user_accounts (id) ON DELETE CASCADE,
        role varchar(20) NOT NULL,
        CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role),
        CONSTRAINT user_roles_role_check CHECK (role IN ('ADMIN', 'STOCK', 'VENTE', 'RH', 'CRM'))
    );

    CREATE INDEX IF NOT EXISTS ix_user_roles_role ON auth.user_roles (role);

    INSERT INTO auth.user_roles (user_id, role)
    SELECT id, role FROM auth.user_accounts
    ON CONFLICT DO NOTHING;
END
$$;

RESET ROLE;
