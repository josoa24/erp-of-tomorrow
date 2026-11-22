import type { UsersListQuery, UsersSearch } from '../types/user'
import { isRole } from '../constants/roles'
import { DEFAULT_USERS_FILTER, isUsersFilterKey, resolveUsersFilter } from './usersFilters'

export const USERS_PAGE_SIZE = 20

const toText = (value: unknown): string | undefined =>
  typeof value === 'string' ? value.trim() || undefined : undefined

const toPage = (value: unknown): number | undefined => {
  const page = typeof value === 'string' ? Number(value) : value
  return typeof page === 'number' && Number.isInteger(page) && page > 1 ? page : undefined
}

export const validateUsersSearch = (search: Record<string, unknown>): UsersSearch => ({
  filtre: isUsersFilterKey(search.filtre) && search.filtre !== DEFAULT_USERS_FILTER ? search.filtre : undefined,
  role: isRole(search.role) ? search.role : undefined,
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

export const pageBounds = (page: number, total: number): { first: number; last: number } => ({
  first: total === 0 ? 0 : (page - 1) * USERS_PAGE_SIZE + 1,
  last: Math.min(page * USERS_PAGE_SIZE, total),
})
