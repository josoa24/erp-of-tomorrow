import { useMemo } from 'react'
import type { UserRowHandlers } from '../services/userRowActions'
import { useEnableUser } from './useUserCommands'
import type { useUsersDialogs } from './useUsersDialogs'

export const useUserRowHandlers = (dialogs: ReturnType<typeof useUsersDialogs>): UserRowHandlers => {
  const { mutate: enable } = useEnableUser()
  const { openRole, openPassword, openDisable } = dialogs

  return useMemo(
    () => ({ changeRole: openRole, resetPassword: openPassword, disable: openDisable, enable: (user) => enable(user) }),
    [openRole, openPassword, openDisable, enable],
  )
}
