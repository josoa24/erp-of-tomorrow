import type { Role } from '../types/auth'

export const ROLE_LABELS: Record<Role, string> = {
  ADMIN: 'Administrateur',
  VENTE: 'Ventes',
  STOCK: 'Stock',
  RH: 'Ressources humaines',
  CRM: 'Relations',
}
