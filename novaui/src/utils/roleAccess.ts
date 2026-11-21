import type { Role } from '../types/auth'

export type ErpModule = 'stock' | 'vente' | 'rh' | 'crm'

const MODULE_ROLES: Record<ErpModule, Role[]> = {
  stock: ['STOCK'],
  vente: ['VENTE'],
  rh: ['RH'],
  crm: ['CRM'],
}

export const canAccessModule = (role: Role, module: ErpModule): boolean =>
  role === 'ADMIN' || MODULE_ROLES[module].includes(role)
