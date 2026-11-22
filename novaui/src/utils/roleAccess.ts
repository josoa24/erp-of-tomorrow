import type { Role, Session } from '../types/auth'

export type ErpModule = 'stock' | 'vente' | 'rh' | 'crm'

const MODULE_ROLES: Record<ErpModule, Role[]> = {
  stock: ['STOCK'],
  vente: ['VENTE'],
  rh: ['RH'],
  crm: ['CRM'],
}

export type RoleInput = Role | readonly Role[]

export const toRoleList = (roles: RoleInput): readonly Role[] => (typeof roles === 'string' ? [roles] : roles)

export const canAccessModule = (roles: RoleInput, module: ErpModule): boolean =>
  toRoleList(roles).some((role) => role === 'ADMIN' || MODULE_ROLES[module].includes(role))

export const sessionRoles = ({ role, roles }: Pick<Session, 'role' | 'roles'>): Role[] =>
  Array.isArray(roles) && roles.length > 0 ? roles : [role]
