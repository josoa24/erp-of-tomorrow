#!/bin/sh
# Demarre un service Spring Boot en mode developpement (rechargement a chaud).
# - un observateur recompile des qu'un fichier de src/ change ;
# - spring-boot-devtools redemarre l'application quand le fichier target/classes/.reload-trigger change
#   (touche apres chaque compilation reussie : evite un redemarrage sur des classes a moitie ecrites).
set -e
mkdir -p target
touch target/.watch-stamp
(
  while true; do
    sleep 2
    if [ -n "$(find src -type f -newer target/.watch-stamp 2>/dev/null | head -n 1)" ]; then
      touch target/.watch-stamp
      # devtools ne redemarre qu'a la fin de la compilation (fichier declencheur), jamais a mi-chemin
      if mvn -q -B -o compile; then
        touch target/classes/.reload-trigger
      else
        echo "[dev] echec de compilation, en attente de la prochaine modification"
      fi
    fi
  done
) &
exec mvn -B spring-boot:run
