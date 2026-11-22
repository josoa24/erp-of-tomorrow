import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'

export const MoreSheetRowContent = ({ icon: Icon, children }: { icon: LucideIcon; children: ReactNode }) => (
  <>
    <Icon aria-hidden="true" />
    {children}
  </>
)
