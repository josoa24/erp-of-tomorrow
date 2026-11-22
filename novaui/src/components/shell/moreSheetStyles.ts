import { cn } from '@/lib/utils'

export const moreSheetRowClass = (danger = false) =>
  cn(
    'flex min-h-[52px] w-full items-center gap-3 rounded-sm px-3 text-left text-body outline-none',
    'transition-colors hover:bg-state-hover focus-visible:shadow-focus-ring [&_svg]:size-5 [&_svg]:shrink-0',
    danger ? 'text-action-danger' : 'text-ink-primary',
  )
