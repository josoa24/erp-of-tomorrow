import type { PartyType } from './contact'

export type InteractionType = 'APPEL' | 'EMAIL' | 'REUNION' | 'VISITE' | 'NOTE'
export type TimelineKind = 'INTERACTION' | 'SALE' | 'PURCHASE_ORDER'

export interface InteractionRequest {
  type: InteractionType
  summary: string
  occurredAt?: string
}

export interface Interaction {
  id: number
  partyType: PartyType
  partyId: number
  type: InteractionType
  occurredAt: string
  summary: string
  author: string | null
  createdAt: string
}

export interface TimelineEntry {
  kind: TimelineKind
  date: string
  title: string
  detail: string | null
  amount: number | null
  reference: number | null
}
