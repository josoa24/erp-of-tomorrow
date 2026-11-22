import type { ContractType } from '../types/employee'

export const CONTRACT_LABELS: Record<ContractType, string> = {
  CDI: 'CDI',
  CDD: 'CDD',
  STAGE: 'Stage',
  JOURNALIER: 'Journalier',
}

export const CONTRACT_TYPES = Object.keys(CONTRACT_LABELS) as ContractType[]
