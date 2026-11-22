import { ChoiceChip } from '../../../components/ui/choice-chip'
import type { ChoiceOption } from '../types/choiceOption'

interface ChoiceGroupProps<Value extends string> {
  label: string
  options: readonly ChoiceOption<Value>[]
  value: Value
  onChange: (value: Value) => void
}

export const ChoiceGroup = <Value extends string>({ label, options, value, onChange }: ChoiceGroupProps<Value>) => (
  <div role="group" aria-label={label} className="flex flex-col gap-1.5">
    <span aria-hidden="true" className="text-label text-ink-primary">
      {label}
    </span>
    <div className="flex flex-wrap gap-2">
      {options.map((option) => (
        <ChoiceChip
          key={option.value}
          label={option.label}
          icon={option.icon}
          selected={option.value === value}
          onClick={() => onChange(option.value)}
        />
      ))}
    </div>
  </div>
)
