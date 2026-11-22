import { changeUserRole, createUser, disableUser, enableUser, resetUserPassword } from '../api/usersApi'
import { userDisplayName } from '../services/usersFormat'
import type { Role } from '../../../types/auth'
import type { CreateUserRequest, ManagedUser } from '../types/user'
import { useUsersMutation } from './useUsersMutation'

export const useCreateUser = () =>
  useUsersMutation({ run: (request: CreateUserRequest) => createUser(request) })

export const useChangeRole = () =>
  useUsersMutation({
    run: ({ user, role }: { user: ManagedUser; role: Role }) => changeUserRole(user.id, role),
    message: (_, { user }) => `Rôle de ${userDisplayName(user)} mis à jour`,
  })

export const useResetPassword = () =>
  useUsersMutation({ run: ({ user, password }: { user: ManagedUser; password: string }) => resetUserPassword(user.id, password) })

export const useDisableUser = () =>
  useUsersMutation({
    run: (user: ManagedUser) => disableUser(user.id),
    message: (_, user) => `Compte de ${userDisplayName(user)} désactivé`,
  })

export const useEnableUser = () =>
  useUsersMutation({
    run: (user: ManagedUser) => enableUser(user.id),
    message: (_, user) => `Compte de ${userDisplayName(user)} réactivé`,
  })
