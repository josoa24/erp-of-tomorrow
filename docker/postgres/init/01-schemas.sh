#!/bin/sh
# Une seule instance PostgreSQL pour tout l'ERP, mais un schéma et un utilisateur par microservice :
# chaque service ne peut lire et écrire que son propre schéma (isolation garantie par la base).
# Exécuté une seule fois, à la création du volume de données.
set -eu

create_service_schema() {
  schema="$1"
  role="$2"
  password="$3"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \
       -v role="$role" -v password="$password" -v schema="$schema" <<'SQL'
CREATE ROLE :"role" LOGIN PASSWORD :'password' CONNECTION LIMIT 15;
CREATE SCHEMA :"schema" AUTHORIZATION :"role";
ALTER ROLE :"role" SET search_path = :"schema";
GRANT CONNECT ON DATABASE :"DBNAME" TO :"role";
SQL
}

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<SQL
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE CONNECT ON DATABASE "$POSTGRES_DB" FROM PUBLIC;
SQL

create_service_schema auth  auth_svc  "${AUTH_DB_PASSWORD:-auth_svc}"
create_service_schema stock stock_svc "${STOCK_DB_PASSWORD:-stock_svc}"
create_service_schema vente vente_svc "${VENTE_DB_PASSWORD:-vente_svc}"
create_service_schema rh    rh_svc    "${RH_DB_PASSWORD:-rh_svc}"
create_service_schema crm   crm_svc   "${CRM_DB_PASSWORD:-crm_svc}"
