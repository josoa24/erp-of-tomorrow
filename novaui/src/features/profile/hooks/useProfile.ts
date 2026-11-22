import { useNavigate } from '@tanstack/react-router'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { useLogout } from '../../../hooks/useLogout'
import { ROUTES } from '../../../navigation/routes'
import { useModuleDefinitions } from '../../modules/hooks/useModuleDefinitions'
import { accessChips, accessSentence } from '../services/profileAccess'
import { useAccount } from './useAccount'

export const useProfile = () => {
  const user = useCurrentUser()
  const account = useAccount()
  const { modules } = useModuleDefinitions()
  const logout = useLogout()
  const navigate = useNavigate()
  if (!user) return null

  return {
    name: account?.fullName?.trim() || user.displayName,
    email: user.email,
    initials: user.initials,
    roles: user.roles,
    sentence: accessSentence(user.roles),
    accesses: accessChips(user.roles, modules),
    logout,
    isAdmin: user.role === 'ADMIN',
    openUsers: () => void navigate({ to: ROUTES.users }),
  }
}
