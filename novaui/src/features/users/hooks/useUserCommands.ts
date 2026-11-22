import { changeUserRoles, createUser, disableUser, enableUser, resetUserPassword } from '../api/usersApi'
import { userRowLabel } from '../services/usersFormat'
import type { Role } from '../../../types/auth'
import type { CreateUserRequest, ManagedUser } from '../types/user'
import { useUsersMutation } from './useUsersMutation'

export const useCreateUser = () =>
  useUsersMutation({ run: (request: CreateUserRequest) => createUser(request) })

export const useChangeRoles = () =>
  useUsersMutation({
    run: ({ user, roles }: { user: ManagedUser; roles: Role[] }) => changeUserRoles(user.id, roles),
    message: (_, { user }) => `Rôles de ${userRowLabel(user)} mis à jour`,
  })

export const useResetPassword = () =>
  useUsersMutation({ run: ({ user, password }: { user: ManagedUser; password: string }) => resetUserPassword(user.id, password) })

export const useDisableUser = () =>
  useUsersMutation({
    run: (user: ManagedUser) => disableUser(user.id),
    message: (_, user) => `Compte de ${userRowLabel(user)} désactivé`,
  })

export const useEnableUser = () =>
  useUsersMutation({
    run: (user: ManagedUser) => enableUser(user.id),
    message: (_, user) => `Compte de ${userRowLabel(user)} réactivé`,
  })
