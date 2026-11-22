import { formatNif, formatStat } from '../services/fiscalIds'
import { EmptyValue } from './EmptyValue'

interface NifStatCellProps {
  nif: string | null
  stat: string | null
}

export const NifStatCell = ({ nif, stat }: NifStatCellProps) => {
  const formattedNif = formatNif(nif)
  const formattedStat = formatStat(stat)
  if (!formattedNif && !formattedStat) return <EmptyValue />

  return (
    <span className="flex flex-col">
      {formattedNif && <span>{formattedNif}</span>}
      {formattedStat && <span className="text-micro">STAT {formattedStat}</span>}
    </span>
  )
}
