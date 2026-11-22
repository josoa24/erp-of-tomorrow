#!/bin/sh
set -eu

export PGHOST="${PGHOST:-erp-db}"
export PGUSER="${POSTGRES_USER:-postgres}"
export PGPASSWORD="${POSTGRES_PASSWORD:?POSTGRES_PASSWORD est obligatoire}"
export PGDATABASE="${POSTGRES_DB:-erp}"
MAX_ATTEMPTS="${MIGRATION_MAX_ATTEMPTS:-30}"
WAIT_SECONDS="${MIGRATION_WAIT_SECONDS:-10}"

apply() {
  psql -v ON_ERROR_STOP=1 -v module_db_password="${MODULE_DB_PASSWORD:?MODULE_DB_PASSWORD est obligatoire}" -f "$1"
}

for migration in /migrations/*.sql; do
  attempt=1
  until apply "$migration"; do
    if [ "$attempt" -ge "$MAX_ATTEMPTS" ]; then
      echo "Migration en échec après $attempt essais : $migration" >&2
      exit 1
    fi
    echo "Tables pas encore créées par les services, nouvel essai dans ${WAIT_SECONDS}s ($attempt/$MAX_ATTEMPTS) : $migration"
    attempt=$((attempt + 1))
    sleep "$WAIT_SECONDS"
  done
  echo "Migration appliquée : $migration"
done
echo "Toutes les migrations sont appliquées"
