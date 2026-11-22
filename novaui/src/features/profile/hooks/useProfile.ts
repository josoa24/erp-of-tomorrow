import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { useLogout } from '../../../hooks/useLogout'
import { useModuleDefinitions } from '../../modules/hooks/useModuleDefinitions'
import { accessChips, accessSentence } from '../services/profileAccess'
import { useAccount } from './useAccount'

export const useProfile = () => {
  const user = useCurrentUser()
  const account = useAccount()
  const { modules } = useModuleDefinitions()
  const logout = useLogout()
  if (!user) return null

  return {
    name: account?.fullName?.trim() || user.displayName,
    email: user.email,
    initials: user.initials,
    roleLabel: user.roleLabel,
    sentence: accessSentence(user.role),
    accesses: accessChips(user.role, modules),
    logout,
  }
}
