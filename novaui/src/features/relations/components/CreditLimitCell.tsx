import { TriangleAlert } from 'lucide-react'
import { formatAriary } from '../services/ariary'
import type { Contact } from '../types/contact'
import { EmptyValue } from './EmptyValue'

type CreditLimitCellProps = Pick<Contact, 'creditLimit' | 'outstanding' | 'overLimit'>

export const CreditLimitCell = ({ creditLimit, outstanding, overLimit }: CreditLimitCellProps) => {
  if (creditLimit === null) return <EmptyValue />
  const limit = formatAriary(creditLimit)
  if (!overLimit) return <span>{limit}</span>

  return (
    <span
      className="inline-flex items-center gap-1.5 text-status-critical-fg"
      title={outstanding === null ? undefined : `Encours ${formatAriary(outstanding)}`}
    >
      <TriangleAlert aria-hidden="true" className="size-4 shrink-0" />
      {limit} · dépassé
    </span>
  )
}
