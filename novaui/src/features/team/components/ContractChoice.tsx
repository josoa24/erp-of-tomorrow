import { ChoiceChip } from '../../../components/ui/choice-chip'
import { CONTRACT_LABELS, CONTRACT_TYPES } from '../constants/contracts'
import type { ContractType } from '../types/employee'

interface ContractChoiceProps {
  value: ContractType | ''
  onChange: (value: ContractType | '') => void
}

export const ContractChoice = ({ value, onChange }: ContractChoiceProps) => (
  <div role="group" aria-label="Contrat" className="flex flex-col gap-1.5">
    <span aria-hidden="true" className="text-label text-ink-primary">
      Contrat
    </span>
    <div className="flex flex-wrap gap-2">
      {CONTRACT_TYPES.map((type) => (
        <ChoiceChip
          key={type}
          label={CONTRACT_LABELS[type]}
          selected={type === value}
          onClick={() => onChange(type === value ? '' : type)}
        />
      ))}
    </div>
  </div>
)
