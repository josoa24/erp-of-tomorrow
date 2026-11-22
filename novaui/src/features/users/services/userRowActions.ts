import { KeyRound, Power, PowerOff, ShieldCheck } from 'lucide-react'
import type { RowAction } from '../../../components/module/types'
import type { ManagedUser } from '../types/user'

export interface UserRowHandlers {
  changeRole: (user: ManagedUser) => void
  resetPassword: (user: ManagedUser) => void
  disable: (user: ManagedUser) => void
  enable: (user: ManagedUser) => void
}

export const buildUserRowActions = (user: ManagedUser, handlers: UserRowHandlers): RowAction[] => [
  { key: 'role', label: 'Changer les rôles', icon: ShieldCheck, onSelect: () => handlers.changeRole(user) },
  { key: 'password', label: 'Réinitialiser le mot de passe', icon: KeyRound, onSelect: () => handlers.resetPassword(user) },
  user.enabled
    ? { key: 'disable', label: 'Désactiver', icon: PowerOff, tone: 'danger', onSelect: () => handlers.disable(user) }
    : { key: 'enable', label: 'Réactiver', icon: Power, onSelect: () => handlers.enable(user) },
]
