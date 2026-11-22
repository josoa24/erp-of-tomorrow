import type { UserCounts, UsersFilterKey } from '../types/user'

export const DEFAULT_USERS_FILTER: UsersFilterKey = 'tous'

export interface UsersFilterOption {
  key: UsersFilterKey
  label: string
  countKey: keyof UserCounts
  enabled: boolean | undefined
}

export const USERS_FILTERS: readonly UsersFilterOption[] = [
  { key: 'tous', label: 'Tous', countKey: 'all', enabled: undefined },
  { key: 'actifs', label: 'Actifs', countKey: 'active', enabled: true },
  { key: 'desactives', label: 'Désactivés', countKey: 'disabled', enabled: false },
]

export const isUsersFilterKey = (value: unknown): value is UsersFilterKey =>
  USERS_FILTERS.some((filter) => filter.key === value)

export const resolveUsersFilter = (key: UsersFilterKey | undefined): UsersFilterOption =>
  USERS_FILTERS.find((filter) => filter.key === key) ?? USERS_FILTERS[0]
