import { cn } from '@/lib/utils'

export const navTabClass = (compact = false) =>
  cn(
    'group inline-flex h-11 shrink-0 items-center gap-2 rounded-pill text-label whitespace-nowrap outline-none',
    'transition-colors focus-visible:shadow-focus-ring [&_svg]:size-5 [&_svg]:shrink-0',
    compact ? 'pr-3.5 pl-3' : 'pr-[18px] pl-4',
  )

export const navLabelClass = (compact = false) =>
  compact ? 'max-lg:sr-only max-lg:group-data-[status=active]:not-sr-only' : undefined

export const NAV_TAB_ACTIVE = 'bg-surface-night text-ink-inverse'
export const NAV_TAB_INACTIVE = 'text-ink-secondary hover:bg-state-hover hover:text-ink-primary'
