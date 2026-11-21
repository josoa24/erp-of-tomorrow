import { CalendarDays, ChevronDown } from 'lucide-react'
import { PERIOD_OPTIONS } from '../../../constants/periods'
import type { PeriodKey } from '../../../types/dashboard'
import styles from './PeriodSelect.module.css'

interface PeriodSelectProps {
  value: PeriodKey
  onChange: (period: PeriodKey) => void
}

export const PeriodSelect = ({ value, onChange }: PeriodSelectProps) => (
  <label className={styles.wrapper}>
    <CalendarDays size={24} strokeWidth={1.5} aria-hidden="true" />
    <select
      className={styles.select}
      aria-label="Période"
      value={value}
      onChange={(event) => onChange(event.target.value as PeriodKey)}
    >
      {PERIOD_OPTIONS.map((option) => (
        <option key={option.key} value={option.key}>
          {option.label}
        </option>
      ))}
    </select>
    <ChevronDown size={20} strokeWidth={1.8} aria-hidden="true" className={styles.chevron} />
  </label>
)
