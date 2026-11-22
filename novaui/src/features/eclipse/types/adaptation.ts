export type AdaptationModuleKey = 'stock' | 'ventes' | 'relations' | 'equipe'

export type AdaptationKey =
  | 'stock.lots-peremption'
  | 'ventes.mobile-money-credit'
  | 'relations.nif-plafond-credit'
  | 'equipe.avances-salaire'

export interface AdaptationProposal {
  adaptationKey: AdaptationKey
  moduleKey: AdaptationModuleKey
  request: string
  title: string
  summary: string
}

export interface AdaptationActivationRequest {
  moduleKey: AdaptationModuleKey
  adaptationKey: AdaptationKey
}

export interface AdaptationActivation extends AdaptationActivationRequest {
  activatedBy: number
  activatedAt: string
}
