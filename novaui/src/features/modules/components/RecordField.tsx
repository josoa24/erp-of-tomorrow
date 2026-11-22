import { ChoiceChip } from '../../../components/ui/choice-chip'
import { Input } from '../../../components/ui/input'
import type { FieldValue } from '../services/recordForm'
import type { ColumnType, ModuleColumn } from '../types/moduleDefinition'
import type { ReferenceOption } from '../types/moduleRecord'
import { ReferencePicker } from './ReferencePicker'

const INPUT_TYPES: Record<ColumnType, string> = {
  TEXT: 'text',
  NUMBER: 'text',
  MONEY: 'text',
  DATE: 'date',
  TIME: 'time',
  DATETIME: 'datetime-local',
  CHOICE: 'text',
  REFERENCE: 'text',
}

interface RecordFieldProps {
  column: ModuleColumn
  value: FieldValue
  error?: string
  onChange: (value: FieldValue) => void
}

export const RecordField = ({ column, value, error, onChange }: RecordFieldProps) => {
  if (column.type === 'REFERENCE') {
    return <ReferencePicker column={column} value={value as ReferenceOption | null} error={error} onChange={onChange} />
  }
  if (column.type === 'CHOICE') {
    return (
      <div role="group" aria-label={column.label} className="flex flex-col gap-1.5">
        <span aria-hidden="true" className="text-label text-ink-primary">{column.label}</span>
        <div className="flex flex-wrap gap-2">
          {(column.choices ?? []).map((choice) => (
            <ChoiceChip key={choice} label={choice} selected={value === choice} onClick={() => onChange(value === choice ? '' : choice)} />
          ))}
        </div>
        {error && <span className="text-micro text-ink-tertiary">{error}</span>}
      </div>
    )
  }
  const numeric = column.type === 'NUMBER' || column.type === 'MONEY'
  return (
    <Input
      label={column.type === 'MONEY' ? `${column.label} (Ar)` : column.label}
      type={INPUT_TYPES[column.type]}
      inputMode={numeric ? 'decimal' : undefined}
      required={column.required}
      value={typeof value === 'string' ? value : ''}
      invalid={error !== undefined}
      helper={error}
      onChange={(event) => onChange(event.target.value)}
    />
  )
}
