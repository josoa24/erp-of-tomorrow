import { Link } from '@tanstack/react-router'
import type { Breakpoint } from '../../hooks/useBreakpoint'
import { cn } from '@/lib/utils'
import { readSession } from '../../services/sessionStore'
import { NovaLogo } from '../brand/NovaLogo'
import { MainNav } from './MainNav'
import { UserMenu } from './UserMenu'

export const AppHeader = ({ breakpoint }: { breakpoint: Breakpoint }) => {
  const role = readSession()?.role
  const showNav = breakpoint !== 'mobile' && role !== undefined

  return (
    <header
      className={cn(
        'grid items-center gap-4 px-4 pt-3 md:px-8 md:pt-[17px]',
        breakpoint === 'desktop' ? 'grid-cols-[1fr_auto_1fr]' : 'grid-cols-[auto_minmax(0,1fr)_auto]',
      )}
    >
      <Link to="/" aria-label="NOVA, accueil" className="justify-self-start rounded-xs outline-none focus-visible:shadow-focus-ring">
        <NovaLogo />
      </Link>
      <div className="flex min-w-0 justify-center">
        {showNav && <MainNav role={role} compact={breakpoint === 'tablet'} />}
      </div>
      <div className="justify-self-end">
        <UserMenu />
      </div>
    </header>
  )
}
