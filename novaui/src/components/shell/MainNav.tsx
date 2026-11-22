import { visibleNavItems } from '../../services/navigation'
import type { Role } from '../../types/auth'
import { MyModulesGroup } from './MyModulesGroup'
import { NavTab } from './NavTab'

interface MainNavProps {
  role: Role
  compact?: boolean
}

export const MainNav = ({ role, compact = false }: MainNavProps) => (
  <nav
    aria-label="Navigation principale"
    className="flex max-w-full min-w-0 items-center gap-1 overflow-x-auto rounded-pill bg-surface-panel p-2 [scrollbar-width:none]"
  >
    {visibleNavItems(role).map((item) => (
      <NavTab key={item.id} item={item} compact={compact} />
    ))}
    <MyModulesGroup compact={compact} />
  </nav>
)
