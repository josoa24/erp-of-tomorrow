import { useMemo } from 'react'
import { Combobox } from '@/components/ui/combobox'
import type { ComboboxOption } from '@/components/ui/combobox-types'
import type { Department } from '../types/employee'

interface DepartmentSelectProps {
  departments: Department[]
  value: string
  error?: string
  onChange: (value: string) => void
}

export const DepartmentSelect = ({ departments, value, error, onChange }: DepartmentSelectProps) => {
  const options = useMemo<ComboboxOption[]>(
    () => departments.map((department) => ({ value: String(department.id), label: department.name })),
    [departments],
  )

  return (
    <Combobox
      label="Service"
      placeholder="Choisir un service"
      options={options}
      value={options.find((option) => option.value === value) ?? null}
      invalid={Boolean(error)}
      helper={error}
      onChange={(option) => onChange(option?.value ?? '')}
    />
  )
}
