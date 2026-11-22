import { Link, useNavigate } from '@tanstack/react-router'
import { LogOut, Plus, User } from 'lucide-react'
import type { NavItem } from '../../constants/moduleNav'
import { useCreateModuleRequest } from '../../features/modules/hooks/useCreateModuleRequest'
import { useModuleDefinitions } from '../../features/modules/hooks/useModuleDefinitions'
import { moduleIconFor } from '../../features/modules/services/moduleIcon'
import { useLogout } from '../../hooks/useLogout'
import { ROUTES } from '../../navigation/routes'
import { Dialog } from '../ui/dialog'
import { MoreSheetRowContent } from './MoreSheetRowContent'
import { moreSheetRowClass } from './moreSheetStyles'
import { CREATE_MODULE_LABEL } from './MyModulesGroup'

interface MoreSheetProps {
  open: boolean
  onClose: () => void
  overflow: NavItem[]
}

export const MoreSheet = ({ open, onClose, overflow }: MoreSheetProps) => {
  const { modules } = useModuleDefinitions()
  const createModule = useCreateModuleRequest()
  const logout = useLogout()
  const navigate = useNavigate()
  const run = (action: () => void) => () => {
    onClose()
    action()
  }

  return (
    <Dialog open={open} onClose={onClose} title="Plus" placement="sheet">
      <nav aria-label="Autres écrans" className="-mx-1 flex flex-col">
        {overflow.map((item) => (
          <Link key={item.id} to={item.to} onClick={onClose} className={moreSheetRowClass()}>
            <MoreSheetRowContent icon={item.icon}>{item.label}</MoreSheetRowContent>
          </Link>
        ))}
        <p className="px-3 pt-3 pb-1 text-caption text-ink-tertiary uppercase">Mes modules</p>
        {modules.map((module) => (
          <Link key={module.id} to={ROUTES.customModule} params={{ slug: module.slug }} onClick={onClose} className={moreSheetRowClass()}>
            <MoreSheetRowContent icon={moduleIconFor(module.icon)}>{module.name}</MoreSheetRowContent>
          </Link>
        ))}
        <button type="button" onClick={run(createModule.open)} className={moreSheetRowClass()}>
          <MoreSheetRowContent icon={Plus}>{CREATE_MODULE_LABEL}</MoreSheetRowContent>
        </button>
        <div role="separator" className="my-2 h-px bg-line-subtle" />
        <button type="button" onClick={run(() => void navigate({ to: ROUTES.profile }))} className={moreSheetRowClass()}>
          <MoreSheetRowContent icon={User}>Mon profil</MoreSheetRowContent>
        </button>
        <button type="button" onClick={run(logout)} className={moreSheetRowClass(true)}>
          <MoreSheetRowContent icon={LogOut}>Se déconnecter</MoreSheetRowContent>
        </button>
      </nav>
    </Dialog>
  )
}
