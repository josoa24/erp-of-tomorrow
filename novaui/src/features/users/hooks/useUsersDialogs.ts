import { useCallback, useState } from 'react'
import type { ManagedUser } from '../types/user'

export type UsersDialog =
  | { kind: 'create' }
  | { kind: 'role'; user: ManagedUser }
  | { kind: 'password'; user: ManagedUser }
  | { kind: 'disable'; user: ManagedUser }
  | null

export const useUsersDialogs = () => {
  const [dialog, setDialog] = useState<UsersDialog>(null)

  return {
    dialog,
    openCreate: useCallback(() => setDialog({ kind: 'create' }), []),
    openRole: useCallback((user: ManagedUser) => setDialog({ kind: 'role', user }), []),
    openPassword: useCallback((user: ManagedUser) => setDialog({ kind: 'password', user }), []),
    openDisable: useCallback((user: ManagedUser) => setDialog({ kind: 'disable', user }), []),
    close: useCallback(() => setDialog(null), []),
  }
}
