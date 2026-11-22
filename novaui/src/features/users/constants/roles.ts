import type { Role } from '../../../types/auth'

export const ROLES: readonly Role[] = ['ADMIN', 'STOCK', 'VENTE', 'RH', 'CRM']

export const ROLES_HINT = 'Un compte peut avoir plusieurs rôles : il accède à tous leurs modules.'

export const sortRoles = (roles: readonly Role[]): Role[] => ROLES.filter((role) => roles.includes(role))

export const toggleRole = (roles: readonly Role[], role: Role): Role[] =>
  sortRoles(roles.includes(role) ? roles.filter((item) => item !== role) : [...roles, role])

export const sameRoles = (left: readonly Role[], right: readonly Role[]): boolean =>
  left.length === right.length && left.every((role) => right.includes(role))
