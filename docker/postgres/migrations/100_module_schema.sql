SET nova.module_db_password = :'module_db_password';

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'module_svc') THEN
        EXECUTE format('CREATE ROLE module_svc LOGIN PASSWORD %L CONNECTION LIMIT 15',
                       current_setting('nova.module_db_password'));
    END IF;
    EXECUTE format('GRANT CONNECT ON DATABASE %I TO module_svc', current_database());
END
$$;

RESET nova.module_db_password;

CREATE SCHEMA IF NOT EXISTS module AUTHORIZATION module_svc;
ALTER ROLE module_svc SET search_path = module;
