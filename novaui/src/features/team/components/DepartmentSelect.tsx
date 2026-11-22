import { useId } from 'react'
import { Field } from '../../../components/ui/field'
import { fieldControlClass, helperIdFor } from '../../../components/ui/field-styles'
import type { Department } from '../types/employee'

interface DepartmentSelectProps {
  departments: Department[]
  value: string
  error?: string
  onChange: (value: string) => void
}

export const DepartmentSelect = ({ departments, value, error, onChange }: DepartmentSelectProps) => {
  const id = useId()
  return (
    <Field id={id} label="Service" helper={error}>
      <div className={fieldControlClass}>
        <select
          id={id}
          required
          value={value}
          aria-invalid={error !== undefined || undefined}
          aria-describedby={error ? helperIdFor(id) : undefined}
          className="h-12 min-w-0 flex-1 bg-transparent text-body text-ink-primary outline-none"
          onChange={(event) => onChange(event.target.value)}
        >
          <option value="">Choisir un service</option>
          {departments.map((department) => (
            <option key={department.id} value={String(department.id)}>
              {department.name}
            </option>
          ))}
        </select>
      </div>
    </Field>
  )
}
