-- Execute une seule fois, a la creation du volume de la base "erp".
-- Un schema par service : aucun service ne lit les tables d'un autre.
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS stock;
CREATE SCHEMA IF NOT EXISTS vente;
CREATE SCHEMA IF NOT EXISTS rh;
CREATE SCHEMA IF NOT EXISTS crm;
