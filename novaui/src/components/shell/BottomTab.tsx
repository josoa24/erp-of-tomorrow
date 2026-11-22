import { Link } from '@tanstack/react-router'
import type { NavItem } from '../../constants/moduleNav'

export const bottomTabClass =
  'flex h-16 w-full flex-col items-center justify-center gap-1 rounded-md text-micro outline-none focus-visible:shadow-focus-ring [&_svg]:size-[22px]'

export const BottomTab = ({ item }: { item: NavItem }) => {
  const Icon = item.icon

  return (
    <Link
      to={item.to}
      activeOptions={{ exact: item.module === null }}
      className={bottomTabClass}
      activeProps={{ className: 'text-brand-violet' }}
      inactiveProps={{ className: 'text-ink-secondary' }}
    >
      <Icon aria-hidden="true" />
      {item.label}
    </Link>
  )
}
