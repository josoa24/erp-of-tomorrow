import { Bell, User } from 'lucide-react'
import { useLogout } from '../../../hooks/useLogout'
import styles from './HeaderActions.module.css'

interface HeaderActionsProps {
  hasAlerts: boolean
}

export const HeaderActions = ({ hasAlerts }: HeaderActionsProps) => {
  const logout = useLogout()

  return (
    <>
      <span
        className={styles.bell}
        role="img"
        aria-label={hasAlerts ? 'Notifications : des alertes sont en cours' : 'Aucune notification'}
      >
        <Bell size={28} strokeWidth={1.5} aria-hidden="true" />
        {hasAlerts && <span className={styles.dot} />}
      </span>
      <button type="button" className={styles.avatar} onClick={logout} title="Se déconnecter" aria-label="Se déconnecter">
        <User size={34} strokeWidth={0} fill="currentColor" aria-hidden="true" />
      </button>
    </>
  )
}
