import { useMemo, useState } from 'react'
import { Combobox } from '../../../components/ui/combobox'
import { useReferenceOptions } from '../hooks/useReferenceOptions'
import { REFERENCE_LABELS, REFERENCE_NOUNS } from '../services/referenceOptions'
import type { ModuleColumn } from '../types/moduleDefinition'
import type { ReferenceOption } from '../types/moduleRecord'

interface ReferencePickerProps {
  column: ModuleColumn
  value: ReferenceOption | null
  error?: string
  onChange: (value: ReferenceOption | null) => void
}

const toOption = (option: ReferenceOption) => ({ value: String(option.id), label: option.label })

export const ReferencePicker = ({ column, value, error, onChange }: ReferencePickerProps) => {
  const target = column.reference ?? 'CUSTOMER'
  const [search, setSearch] = useState('')
  const { options, loading, forbidden } = useReferenceOptions(target, search)
  const items = useMemo(() => options.map(toOption), [options])

  return (
    <Combobox
      label={column.required ? column.label : `${column.label} (facultatif)`}
      placeholder={`Chercher ${REFERENCE_NOUNS[target]}…`}
      options={items}
      value={value ? toOption(value) : null}
      loading={loading}
      invalid={error !== undefined}
      helper={error}
      emptyOptionLabel={column.required ? undefined : 'Aucun'}
      unavailableText={forbidden ? `Votre rôle n’a pas accès à ${REFERENCE_LABELS[target]}` : undefined}
      onQueryChange={setSearch}
      onChange={(option) => onChange(option ? { id: Number(option.value), label: option.label } : null)}
    />
  )
}
