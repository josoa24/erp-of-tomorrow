#!/bin/sh
set -e
cd /home/saandria/Documents/devfest/erp-of-tomorrow
BASE=${1:-web/feat-nova-adaptation-state}
for b in stock/feat-nova-stock-ui vente/feat-nova-ventes-ui crm/feat-nova-relations-ui rh/feat-nova-equipe-ui eclipse/feat-eclipse-flows-ui accueil/feat-nova-accueil-navigation-ui; do
  d=../erp-of-tomorrow.worktrees/$(echo $b | tr '/' '-')
  git worktree add -b $b $d $BASE 2>&1 | tail -1
done
git worktree list
