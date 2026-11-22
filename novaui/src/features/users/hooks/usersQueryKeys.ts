import type { UsersListQuery } from '../types/user'

export const USERS_QUERY_KEY = ['users'] as const

export const usersQueryKeys = {
  list: (query: UsersListQuery) => [...USERS_QUERY_KEY, 'list', query] as const,
  counts: [...USERS_QUERY_KEY, 'counts'] as const,
}
