import { Link } from '@tanstack/react-router'
import type { NavItem } from '../../constants/moduleNav'
import { NAV_TAB_ACTIVE, NAV_TAB_INACTIVE, navLabelClass, navTabClass } from './navTabStyles'

interface NavTabProps {
  item: NavItem
  compact?: boolean
}

export const NavTab = ({ item, compact = false }: NavTabProps) => {
  const Icon = item.icon

  return (
    <Link
      to={item.to}
      activeOptions={{ exact: item.module === null }}
      className={navTabClass(compact)}
      activeProps={{ className: NAV_TAB_ACTIVE }}
      inactiveProps={{ className: NAV_TAB_INACTIVE }}
      title={compact ? item.label : undefined}
    >
      <Icon aria-hidden="true" />
      <span className={navLabelClass(compact)}>{item.label}</span>
    </Link>
  )
}
