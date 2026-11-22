export type PartyType = 'CUSTOMER' | 'SUPPLIER'
export type ContactKind = 'ALL' | PartyType
export type ContactStatus = 'ACTIVE' | 'FOLLOW_UP'
export type CustomerCategory = 'PARTICULIER' | 'ENTREPRISE'

export interface PartyRef {
  kind: PartyType
  partyId: number
}

export interface Contact extends PartyRef {
  name: string
  initials: string
  typeLabel: string
  city: string | null
  phone: string | null
  lastInteractionAt: string | null
  status: ContactStatus
  nif: string | null
  stat: string | null
  creditLimit: number | null
  outstanding: number | null
  overLimit: boolean
  professional?: boolean
  email?: string | null
}

export interface ContactCounts {
  all: number
  customers: number
  suppliers: number
  followUp: number
  professional: number
}

export interface ContactSummary {
  activeCustomers: number
  totalCustomers: number
  suppliers: number
  openPurchaseOrders: number
  followUp: number
  creditOutstanding: number
  overLimitCustomers: number
}

export interface ContactQuery {
  kind: ContactKind
  search?: string
  followUp?: boolean
  professional?: boolean
}

export interface NewContactRequest {
  kind: PartyType
  name: string
  phone?: string
  email?: string
  city?: string
  type?: CustomerCategory
  nif?: string
  stat?: string
  creditLimit?: number
}
