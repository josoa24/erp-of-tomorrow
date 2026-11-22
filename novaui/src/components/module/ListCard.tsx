import type { ReactNode } from 'react'
import { Panel } from '../ui/panel'

export interface ListCardProps {
  label: string
  toolbar?: ReactNode
  children: ReactNode
}

export const ListCard = ({ label, toolbar, children }: ListCardProps) => (
  <Panel aria-label={label} className="flex flex-col gap-4 max-md:p-3">
    {toolbar}
    {children}
  </Panel>
)
