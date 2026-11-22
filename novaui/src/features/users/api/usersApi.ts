import { httpGet, httpGetPage, httpPost, httpPut, type Page, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Role } from '../../../types/auth'
import type { CreateUserRequest, ManagedUser, UserCounts, UsersListQuery } from '../types/user'

export const USERS_PATH = '/api/auth/users'

const userUrl = (id: number): string => `${USERS_PATH}/${id}`

export const listUsers = (query: UsersListQuery, options: RequestOptions = {}): Promise<Page<ManagedUser>> =>
  httpGetPage<ManagedUser>(`${USERS_PATH}${buildQuery({ ...query })}`, options)

export const getUserCounts = (options: RequestOptions = {}): Promise<UserCounts> =>
  httpGet<UserCounts>(`${USERS_PATH}/counts`, options)

export const createUser = (request: CreateUserRequest): Promise<ManagedUser> =>
  httpPost<ManagedUser>(USERS_PATH, request)

export const changeUserRoles = (id: number, roles: Role[]): Promise<ManagedUser> =>
  httpPut<ManagedUser>(userUrl(id), { roles })

export const resetUserPassword = (id: number, newPassword: string): Promise<void> =>
  httpPost<void>(`${userUrl(id)}/password`, { newPassword })

export const disableUser = (id: number): Promise<ManagedUser> => httpPost<ManagedUser>(`${userUrl(id)}/disable`)

export const enableUser = (id: number): Promise<ManagedUser> => httpPost<ManagedUser>(`${userUrl(id)}/enable`)
