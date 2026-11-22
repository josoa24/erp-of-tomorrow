import { X } from 'lucide-react'
import { useDeferredValue, useState } from 'react'
import { IconButton } from '../../../components/ui/icon-button'
import { Input } from '../../../components/ui/input'
import { useReferenceOptions } from '../hooks/useReferenceOptions'
import { REFERENCE_LABELS, REFERENCE_NOUNS } from '../services/referenceOptions'
import type { ModuleColumn } from '../types/moduleDefinition'
import type { ReferenceOption } from '../types/moduleRecord'

const MAX_OPTIONS = 6

interface ReferencePickerProps {
  column: ModuleColumn
  value: ReferenceOption | null
  error?: string
  onChange: (value: ReferenceOption | null) => void
}

export const ReferencePicker = ({ column, value, error, onChange }: ReferencePickerProps) => {
  const target = column.reference ?? 'CUSTOMER'
  const [search, setSearch] = useState('')
  const deferred = useDeferredValue(search)
  const { options, loading, forbidden } = useReferenceOptions(target, deferred)

  if (forbidden) {
    return (
      <p className="text-body-s text-ink-tertiary">
        {column.label} : votre rôle ne donne pas accès à {REFERENCE_LABELS[target]}, ce champ est ignoré.
      </p>
    )
  }
  if (value) {
    return (
      <div className="flex flex-col gap-1.5">
        <span className="text-label text-ink-primary">{column.label}</span>
        <span className="flex h-12 items-center justify-between rounded-sm border border-line-strong bg-surface-solid pr-2 pl-3.5">
          <span className="text-body text-ink-primary">{value.label}</span>
          <IconButton aria-label={`Retirer ${column.label}`} variant="ghost" onClick={() => onChange(null)}>
            <X />
          </IconButton>
        </span>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-2">
      <Input
        label={column.label}
        required={column.required}
        placeholder={`Chercher ${REFERENCE_NOUNS[target]}…`}
        value={search}
        invalid={error !== undefined}
        helper={error}
        onChange={(event) => setSearch(event.target.value)}
      />
      <ul aria-label={`Résultats ${column.label}`} className="flex flex-wrap gap-2">
        {loading && <li className="text-micro text-ink-tertiary">Recherche…</li>}
        {!loading && options.length === 0 && <li className="text-micro text-ink-tertiary">Aucun résultat</li>}
        {options.slice(0, MAX_OPTIONS).map((option) => (
          <li key={option.id}>
            <button
              type="button"
              className="rounded-pill border border-line-strong px-3 py-1.5 text-label text-ink-primary hover:bg-state-hover"
              onClick={() => onChange(option)}
            >
              {option.label}
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
