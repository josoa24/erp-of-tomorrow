import { cn } from '@/lib/utils'
import { NAV_ITEMS } from '../../../constants/navItems'

const ACTIVE_ID = 'home'

export const NavPill = () => (
  <nav className="flex gap-0.5 rounded-full border border-[rgba(120,130,255,0.3)] bg-nova-panel p-1.25 shadow-[inset_0_0_24px_rgba(90,100,255,0.1)]" aria-label="Navigation principale">
    {NAV_ITEMS.map(({ id, label, icon: Icon, available }) => (
      <button
        key={id}
        type="button"
        className={cn(
          'grid h-14.5 w-22.5 place-items-center rounded-full text-[#e6e7ff] transition-colors duration-200 disabled:text-[rgba(230,231,255,0.85)] max-sm:h-11.5 max-sm:w-14 max-sm:[&>svg]:size-6',
          id === ACTIVE_ID
            ? 'bg-nav-active shadow-nav-active'
            : 'hover:not-disabled:bg-[rgba(120,110,255,0.15)]',
        )}
        aria-label={label}
        aria-current={id === ACTIVE_ID ? 'page' : undefined}
        title={available ? label : `${label} (bientôt disponible)`}
        disabled={!available}
      >
        <Icon size={30} strokeWidth={1.5} aria-hidden="true" />
      </button>
    ))}
  </nav>
)
