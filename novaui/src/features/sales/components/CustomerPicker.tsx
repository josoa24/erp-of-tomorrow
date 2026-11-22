import { useId, useState } from 'react'
import { SearchField } from '../../../components/ui/search-field'
import { useCustomerOptions } from '../hooks/useCustomerOptions'
import type { CustomerOption } from '../types/catalog'

interface CustomerPickerProps {
  value: CustomerOption | null
  onChange: (customer: CustomerOption | null) => void
}

const optionClass =
  'flex min-h-11 cursor-pointer items-center gap-3 rounded-sm px-3 py-2 text-body-s text-ink-primary hover:bg-state-hover has-[:checked]:bg-state-selected has-[:focus-visible]:shadow-focus-ring'

export const CustomerPicker = ({ value, onChange }: CustomerPickerProps) => {
  const [search, setSearch] = useState('')
  const { customers } = useCustomerOptions(search)
  const name = useId()
  const options: (CustomerOption | null)[] = [null, ...customers]
  const shown = value && !customers.some((customer) => customer.id === value.id) ? [null, value, ...customers] : options

  return (
    <fieldset className="flex min-w-0 flex-col gap-2">
      <legend className="mb-1.5 text-label text-ink-primary">Client</legend>
      <SearchField placeholder="Rechercher un client…" aria-label="Rechercher un client" slashShortcut={false} onSearch={setSearch} />
      <div className="flex max-h-44 flex-col gap-0.5 overflow-y-auto rounded-sm border border-line-subtle p-1">
        {shown.map((customer) => (
          <label key={customer?.id ?? 'comptoir'} className={optionClass}>
            <input
              type="radio"
              name={name}
              className="size-4 shrink-0 accent-brand-violet outline-none"
              checked={(value?.id ?? null) === (customer?.id ?? null)}
              onChange={() => onChange(customer)}
            />
            <span className="min-w-0 flex-1 break-words">{customer?.name ?? 'Client comptoir'}</span>
            {customer?.city && <span className="shrink-0 text-micro text-ink-tertiary">{customer.city}</span>}
          </label>
        ))}
      </div>
    </fieldset>
  )
}
