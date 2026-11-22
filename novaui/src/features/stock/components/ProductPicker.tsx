import { useMemo, useState } from 'react'
import { Combobox } from '@/components/ui/combobox'
import type { ComboboxOption } from '@/components/ui/combobox-types'
import { useProductOptions } from '../hooks/useProductOptions'
import { formatStockQuantity } from '../services/stockQuantity'

interface ProductPickerProps {
  value: number | null
  error?: string
  onChange: (productId: number) => void
}

export const ProductPicker = ({ value, error, onChange }: ProductPickerProps) => {
  const [search, setSearch] = useState('')
  const [selected, setSelected] = useState<ComboboxOption | null>(null)
  const { products, loading } = useProductOptions(search, true)
  const options = useMemo(
    () =>
      products.map((product) => ({
        value: String(product.id),
        label: product.name,
        description: formatStockQuantity(product.quantity, product.unit),
      })),
    [products],
  )
  const current = value === null ? null : selected

  return (
    <Combobox
      label="Produit"
      placeholder="Choisir un produit"
      options={options}
      value={current}
      loading={loading}
      invalid={Boolean(error)}
      helper={error}
      onQueryChange={setSearch}
      onChange={(option) => {
        setSelected(option)
        if (option) onChange(Number(option.value))
      }}
    />
  )
}
