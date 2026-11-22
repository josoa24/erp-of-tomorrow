#!/bin/sh
set -eu

cd "$(dirname "$0")/../.."

for migration in docker/postgres/migrations/*.sql; do
  echo "Migration : $migration"
  docker compose "$@" exec -T erp-db sh -c \
    'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -v module_db_password="${MODULE_DB_PASSWORD:-module_svc}" -f -' \
    < "$migration"
done
