import type { UiTone } from '../types/uiBlock'
import type { ActionStatus, ActionEntry } from '../types/agent'

export const ACTION_TTL_MIN = 10

export interface ActionCardView {
  eyebrow: string
  border: string
  pill: { label: string; tone: UiTone } | null
  footer: string
  alert: boolean
}

const SAFE = 'border-line-subtle'

export const ACTION_VIEWS: Record<ActionStatus, ActionCardView> = {
  pending: {
    eyebrow: 'Écriture à confirmer',
    border: 'border-status-warning-vivid',
    pill: { label: `Expire dans ${ACTION_TTL_MIN} min`, tone: 'warning' },
    footer: 'Rien n’est enregistré avant votre confirmation. Entrée = Confirmer, Échap = Annuler.',
    alert: false,
  },
  confirming: { eyebrow: 'Enregistrement…', border: 'border-status-info-vivid', pill: { label: 'En cours', tone: 'info' }, footer: '', alert: false },
  cancelling: { eyebrow: 'Annulation…', border: 'border-status-info-vivid', pill: { label: 'En cours', tone: 'info' }, footer: '', alert: false },
  done: {
    eyebrow: 'Écriture enregistrée',
    border: 'border-status-success-vivid',
    pill: { label: 'Enregistrée', tone: 'success' },
    footer: 'Exécutée avec vos droits, sans passer par l’IA.',
    alert: false,
  },
  cancelled: { eyebrow: 'Écriture annulée', border: SAFE, pill: { label: 'Rien n’a été enregistré', tone: 'neutral' }, footer: '', alert: false },
  refused: {
    eyebrow: 'Écriture refusée par l’ERP',
    border: 'border-status-critical-vivid',
    pill: null,
    footer: 'L’ERP a refusé : rien n’a été modifié.',
    alert: true,
  },
  failed: {
    eyebrow: 'Écriture non effectuée',
    border: 'border-status-critical-vivid',
    pill: null,
    footer: 'Une erreur est survenue : rien n’a été modifié.',
    alert: true,
  },
  expired: {
    eyebrow: 'Proposition expirée',
    border: SAFE,
    pill: null,
    footer: `Les propositions expirent après ${ACTION_TTL_MIN} min.`,
    alert: false,
  },
}

export const viewOf = (entry: ActionEntry): ActionCardView => {
  const base = ACTION_VIEWS[entry.status]
  if (!base.pill && entry.code) return { ...base, pill: { label: entry.code, tone: entry.status === 'expired' ? 'neutral' : 'critical' } }
  return base
}

const ARGUMENT_LABELS: Record<string, string> = {
  product_id: 'Produit',
  type: 'Type',
  quantity: 'Quantité',
  reason: 'Motif',
  supplier_id: 'Fournisseur',
  customer_id: 'Client',
  sale_id: 'Vente',
  purchase_order_id: 'Bon de commande',
}

export const argumentFields = (args: Record<string, unknown>): { label: string; value: string }[] =>
  Object.entries(args)
    .filter(([, value]) => typeof value === 'string' || typeof value === 'number')
    .map(([key, value]) => ({ label: ARGUMENT_LABELS[key] ?? key, value: String(value) }))
