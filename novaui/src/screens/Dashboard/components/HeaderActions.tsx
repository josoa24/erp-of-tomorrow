import { Bell, User } from 'lucide-react'
import { useLogout } from '../../../hooks/useLogout'

interface HeaderActionsProps {
  hasAlerts: boolean
}

export const HeaderActions = ({ hasAlerts }: HeaderActionsProps) => {
  const logout = useLogout()

  return (
    <>
      <span
        className="relative grid size-14.5 place-items-center rounded-full border border-[rgba(120,130,255,0.25)] bg-nova-panel max-sm:size-11.5"
        role="img"
        aria-label={hasAlerts ? 'Notifications : des alertes sont en cours' : 'Aucune notification'}
      >
        <Bell size={28} strokeWidth={1.5} aria-hidden="true" />
        {hasAlerts && <span className="absolute top-1 right-1.5 size-3.75 rounded-full bg-nova-danger shadow-[0_0_10px_rgba(248,86,109,0.8)]" />}
      </span>
      <button type="button" className="grid size-15 items-end justify-items-center overflow-hidden rounded-full border-2 border-[rgba(190,190,255,0.45)] bg-avatar p-0 text-[#eef0ff] shadow-[0_0_20px_rgba(130,130,255,0.4)] max-sm:size-11.5 [&>svg]:-mb-1 [&>svg]:size-11" onClick={logout} title="Se déconnecter" aria-label="Se déconnecter">
        <User size={34} strokeWidth={0} fill="currentColor" aria-hidden="true" />
      </button>
    </>
  )
}
