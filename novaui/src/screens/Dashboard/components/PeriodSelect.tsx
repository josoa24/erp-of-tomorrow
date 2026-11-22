import { CalendarDays, ChevronDown } from 'lucide-react'
import { PERIOD_OPTIONS } from '../../../constants/periods'
import type { PeriodKey } from '../../../types/dashboard'

interface PeriodSelectProps {
  value: PeriodKey
  onChange: (period: PeriodKey) => void
}

export const PeriodSelect = ({ value, onChange }: PeriodSelectProps) => (
  <label className="relative inline-flex h-14.5 cursor-pointer items-center gap-3 rounded-full border border-[rgba(120,130,255,0.3)] bg-nova-panel pr-4.5 pl-5.5 focus-within:outline-2 focus-within:outline-offset-3 focus-within:outline-nova-accent max-sm:h-11.5 max-sm:px-3 max-sm:[&>svg:first-child]:hidden">
    <CalendarDays size={24} strokeWidth={1.5} aria-hidden="true" />
    <select
      className="mr-[-26px] cursor-pointer appearance-none border-0 bg-transparent pr-6.5 text-[19px] focus:outline-none max-sm:text-[15px] [&>option]:bg-[#12144a] [&>option]:text-nova-text"
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
    <ChevronDown size={20} strokeWidth={1.8} aria-hidden="true" className="pointer-events-none" />
  </label>
)
