import { Ellipsis, Sparkles } from 'lucide-react'
import { useState } from 'react'
import { cn } from '@/lib/utils'
import { splitForBottomBar, visibleNavItems } from '../../services/navigation'
import type { Role } from '../../types/auth'
import { BottomTab, bottomTabClass } from './BottomTab'
import { useEclipseDock } from './eclipse-dock-context'
import { MoreSheet } from './MoreSheet'

export const BottomBar = ({ role }: { role: Role }) => {
  const [moreOpen, setMoreOpen] = useState(false)
  const { mobileOpen, openDock, closeDock } = useEclipseDock()
  const { primary, overflow } = splitForBottomBar(visibleNavItems(role))
  const [home, first, second] = primary

  return (
    <nav
      aria-label="Navigation principale"
      className="fixed inset-x-0 bottom-0 z-30 rounded-t-[28px] bg-surface-solid pt-2 pb-[max(env(safe-area-inset-bottom),20px)] shadow-elevation-2"
    >
      <div className="mx-auto grid max-w-[480px] grid-cols-5 items-center px-1">
        {home && <BottomTab item={home} />}
        <div>{first && <BottomTab item={first} />}</div>
        <button
          type="button"
          aria-label="Ouvrir Eclipse"
          aria-expanded={mobileOpen}
          onClick={mobileOpen ? closeDock : openDock}
          className="mx-auto -mt-[26px] inline-flex size-[60px] items-center justify-center rounded-pill border-4 border-surface-solid bg-night-orb text-ink-inverse outline-none focus-visible:shadow-focus-ring [&_svg]:size-6"
        >
          <Sparkles aria-hidden="true" />
        </button>
        <div>{second && <BottomTab item={second} />}</div>
        <button
          type="button"
          aria-haspopup="dialog"
          aria-expanded={moreOpen}
          onClick={() => setMoreOpen(true)}
          className={cn(bottomTabClass, moreOpen ? 'text-brand-violet' : 'text-ink-secondary')}
        >
          <Ellipsis aria-hidden="true" />
          Plus
        </button>
      </div>
      <MoreSheet open={moreOpen} onClose={() => setMoreOpen(false)} overflow={overflow} />
    </nav>
  )
}
