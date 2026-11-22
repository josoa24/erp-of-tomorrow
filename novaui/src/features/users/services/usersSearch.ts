import type { Role } from '../../../types/auth'
import type { UsersListQuery, UsersSearch } from '../types/user'
import { ROLES } from '../constants/roles'
import { DEFAULT_USERS_FILTER, isUsersFilterKey, resolveUsersFilter } from './usersFilters'

export const USERS_PAGE_SIZE = 20

const toText = (value: unknown): string | undefined =>
  typeof value === 'string' || typeof value === 'number' ? String(value).trim() || undefined : undefined

const toRoles = (value: unknown): Role[] | undefined => {
  const items: unknown[] = Array.isArray(value) ? value : [value]
  const roles = ROLES.filter((role) => items.includes(role))
  return roles.length > 0 ? roles : undefined
}

const toPage = (value: unknown): number | undefined => {
  const page = typeof value === 'string' ? Number(value) : value
  return typeof page === 'number' && Number.isInteger(page) && page > 1 ? page : undefined
}

export const validateUsersSearch = (search: Record<string, unknown>): UsersSearch => ({
  filtre: isUsersFilterKey(search.filtre) && search.filtre !== DEFAULT_USERS_FILTER ? search.filtre : undefined,
  role: toRoles(search.role),
  recherche: toText(search.recherche),
  page: toPage(search.page),
})

export const toUsersListQuery = (search: UsersSearch): UsersListQuery => ({
  search: search.recherche,
  role: search.role,
  enabled: resolveUsersFilter(search.filtre).enabled,
  page: (search.page ?? 1) - 1,
  size: USERS_PAGE_SIZE,
})

export const pageCount = (total: number): number => Math.max(1, Math.ceil(total / USERS_PAGE_SIZE))

export const clampPage = (page: number, total: number): number => Math.min(Math.max(page, 1), pageCount(total))

export const pageBounds = (page: number, total: number): { first: number; last: number } => {
  const current = clampPage(page, total)
  return {
    first: total === 0 ? 0 : (current - 1) * USERS_PAGE_SIZE + 1,
    last: Math.min(current * USERS_PAGE_SIZE, total),
  }
}
