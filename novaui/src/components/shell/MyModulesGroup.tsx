import { Link } from '@tanstack/react-router'
import { Plus } from 'lucide-react'
import { useCreateModuleRequest } from '../../features/modules/hooks/useCreateModuleRequest'
import { useModuleDefinitions } from '../../features/modules/hooks/useModuleDefinitions'
import { moduleIconFor } from '../../features/modules/services/moduleIcon'
import { ROUTES } from '../../navigation/routes'
import { IconButton } from '../ui/icon-button'
import { NAV_TAB_ACTIVE, NAV_TAB_INACTIVE, navTabClass } from './navTabStyles'

export const CREATE_MODULE_LABEL = 'Créer un module'

export const MyModulesGroup = ({ compact = false }: { compact?: boolean }) => {
  const { modules } = useModuleDefinitions()
  const { open } = useCreateModuleRequest()

  return (
    <div role="group" aria-label="Mes modules" className="flex shrink-0 items-center gap-1 rounded-pill bg-bg-lilac p-1">
      {modules.map((module) => {
        const Icon = moduleIconFor(module.icon)
        return (
          <Link
            key={module.id}
            to={ROUTES.customModule}
            params={{ slug: module.slug }}
            className={navTabClass(compact)}
            activeProps={{ className: NAV_TAB_ACTIVE }}
            inactiveProps={{ className: NAV_TAB_INACTIVE }}
          >
            <Icon aria-hidden="true" />
            {module.name}
          </Link>
        )
      })}
      <IconButton variant="surface" size="m" aria-label={CREATE_MODULE_LABEL} onClick={open} className="text-brand-violet">
        <Plus />
      </IconButton>
    </div>
  )
}
