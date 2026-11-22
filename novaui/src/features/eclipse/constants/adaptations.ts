import type { AdaptationKey, AdaptationModuleKey, AdaptationProposal } from '../types/adaptation'

export const ADAPTATION_MODULE_KEYS: readonly AdaptationModuleKey[] = ['stock', 'ventes', 'relations', 'equipe']

export const ADAPTATION_KEYS: readonly AdaptationKey[] = [
  'stock.lots-peremption',
  'ventes.mobile-money-credit',
  'relations.nif-plafond-credit',
  'equipe.avances-salaire',
]

export const ADAPTATION_MODULE: Readonly<Record<AdaptationKey, AdaptationModuleKey>> = {
  'stock.lots-peremption': 'stock',
  'ventes.mobile-money-credit': 'ventes',
  'relations.nif-plafond-credit': 'relations',
  'equipe.avances-salaire': 'equipe',
}

export const ADAPTATION_CATALOG: Readonly<Record<AdaptationKey, AdaptationProposal>> = {
  'stock.lots-peremption': {
    adaptationKey: 'stock.lots-peremption',
    moduleKey: 'stock',
    title: 'Lots et dates de péremption',
    request: 'Ajoute le n° de lot et la date de péremption, avec une alerte 30 jours avant',
    summary: 'Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt',
  },
  'ventes.mobile-money-credit': {
    adaptationKey: 'ventes.mobile-money-credit',
    moduleKey: 'ventes',
    title: 'Mobile Money et ventes à crédit',
    request: 'Ajoute le paiement Mobile Money (MVola, Orange Money) et le suivi des ventes à crédit',
    summary: 'Eclipse a ajouté : colonne Paiement · filtre À crédit · indicateur Crédit en cours',
  },
  'relations.nif-plafond-credit': {
    adaptationKey: 'relations.nif-plafond-credit',
    moduleKey: 'relations',
    title: 'NIF / STAT et plafond de crédit',
    request: 'Ajoute le NIF/STAT des clients professionnels et un plafond de crédit',
    summary: 'Eclipse a ajouté : colonnes NIF / STAT et Plafond crédit · filtre Professionnels · alerte de dépassement',
  },
  'equipe.avances-salaire': {
    adaptationKey: 'equipe.avances-salaire',
    moduleKey: 'equipe',
    title: 'Avances sur salaire',
    request: 'Ajoute les avances sur salaire, plafonnées à 30 % du salaire',
    summary: 'Eclipse a ajouté : colonnes Avance et Plafond · filtre Avance demandée · validation par le gérant',
  },
}

export const isAdaptationKey = (value: unknown): value is AdaptationKey =>
  typeof value === 'string' && (ADAPTATION_KEYS as readonly string[]).includes(value)
