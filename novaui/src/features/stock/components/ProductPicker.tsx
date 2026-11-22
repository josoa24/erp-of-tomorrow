import { useId, useState } from 'react'
import { SearchField } from '../../../components/ui/search-field'
import { cn } from '@/lib/utils'
import { useProductOptions } from '../hooks/useProductOptions'
import { formatStockQuantity } from '../services/stockQuantity'

interface ProductPickerProps {
  value: number | null
  error?: string
  onChange: (productId: number) => void
}

const optionClass =
  'flex min-h-11 cursor-pointer items-center gap-3 rounded-sm px-3 py-2 text-body-s text-ink-primary hover:bg-state-hover has-[:checked]:bg-state-selected has-[:focus-visible]:shadow-focus-ring'

export const ProductPicker = ({ value, error, onChange }: ProductPickerProps) => {
  const [search, setSearch] = useState('')
  const { products, loading } = useProductOptions(search, true)
  const name = useId()
  const errorId = useId()

  return (
    <fieldset aria-describedby={error ? errorId : undefined} className="flex min-w-0 flex-col gap-2">
      <legend className="mb-1.5 text-label text-ink-primary">Produit</legend>
      <SearchField
        placeholder="Rechercher un produit…"
        aria-label="Rechercher le produit reçu"
        slashShortcut={false}
        onSearch={setSearch}
      />
      <div
        className={cn(
          'flex max-h-56 flex-col gap-0.5 overflow-y-auto rounded-sm border border-line-subtle p-1',
          error && 'border-status-critical-fg',
        )}
      >
        {products.map((product) => (
          <label key={product.id} className={optionClass}>
            <input
              type="radio"
              name={name}
              className="size-4 shrink-0 accent-brand-violet outline-none"
              checked={value === product.id}
              onChange={() => onChange(product.id)}
            />
            <span className="min-w-0 flex-1 break-words">{product.name}</span>
            <span className="shrink-0 text-micro text-ink-tertiary">
              {formatStockQuantity(product.quantity, product.unit)}
            </span>
          </label>
        ))}
        {!loading && products.length === 0 && (
          <p className="px-3 py-2 text-body-s text-ink-tertiary">Aucun produit ne correspond.</p>
        )}
      </div>
      {error && (
        <p id={errorId} className="text-micro text-ink-tertiary">
          {error}
        </p>
      )}
    </fieldset>
  )
}
