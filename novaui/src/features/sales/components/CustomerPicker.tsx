import { useMemo, useState } from 'react'
import { Combobox } from '@/components/ui/combobox'
import type { ComboboxOption } from '@/components/ui/combobox-types'
import { useCustomerOptions } from '../hooks/useCustomerOptions'
import type { CustomerOption } from '../types/catalog'

interface CustomerPickerProps {
  value: CustomerOption | null
  onChange: (customer: CustomerOption | null) => void
}

const toOption = (customer: CustomerOption): ComboboxOption => ({
  value: String(customer.id),
  label: customer.name,
  description: customer.city ?? undefined,
})

export const CustomerPicker = ({ value, onChange }: CustomerPickerProps) => {
  const [search, setSearch] = useState('')
  const { customers, loading } = useCustomerOptions(search)
  const options = useMemo(() => customers.map(toOption), [customers])

  return (
    <Combobox
      label="Client"
      placeholder="Client comptoir"
      emptyOptionLabel="Client comptoir"
      options={options}
      value={value ? toOption(value) : null}
      loading={loading}
      onQueryChange={setSearch}
      onChange={(option) => onChange(option ? (customers.find((c) => String(c.id) === option.value) ?? null) : null)}
    />
  )
}
