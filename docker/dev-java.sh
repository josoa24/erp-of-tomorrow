#!/bin/sh
# Demarre un service Spring Boot en mode developpement (rechargement a chaud).
# - un observateur recompile des qu'un fichier de src/ change ;
# - spring-boot-devtools redemarre l'application quand target/classes change.
set -e
mkdir -p target
touch target/.watch-stamp
(
  while true; do
    sleep 2
    if [ -n "$(find src -type f -newer target/.watch-stamp 2>/dev/null | head -n 1)" ]; then
      touch target/.watch-stamp
      mvn -q -B -o compile || echo "[dev] echec de compilation, en attente de la prochaine modification"
    fi
  done
) &
exec mvn -B spring-boot:run
