import { Trash2 } from 'lucide-react'
import { useMemo, useState } from 'react'
import { Combobox } from '@/components/ui/combobox'
import { IconButton } from '../../../components/ui/icon-button'
import { Input } from '../../../components/ui/input'
import { useProductOptions } from '../hooks/useProductOptions'
import { formatOrderAmount } from '../services/orderFormat'
import { lineTotal, type OrderLineDraft } from '../services/orderForm'

interface OrderLineRowProps {
  line: OrderLineDraft
  removable: boolean
  onChange: (key: number, patch: Partial<OrderLineDraft>) => void
  onRemove: (key: number) => void
}

export const OrderLineRow = ({ line, removable, onChange, onRemove }: OrderLineRowProps) => {
  const [search, setSearch] = useState('')
  const { products, loading } = useProductOptions(search, true)
  const options = useMemo(() => products.map((product) => ({ value: String(product.id), label: product.name, description: product.sku })), [products])
  const selected = line.productId === null ? null : { value: String(line.productId), label: line.productLabel }

  return (
    <div className="grid grid-cols-[1fr_auto] items-end gap-3 rounded-lg border border-line-subtle p-3 sm:grid-cols-[2fr_1fr_1fr_auto_auto]">
      <Combobox
        label="Produit"
        placeholder="Choisir un produit"
        options={options}
        value={selected}
        loading={loading}
        onQueryChange={setSearch}
        onChange={(option) => {
          const product = products.find((item) => String(item.id) === option?.value)
          onChange(line.key, {
            productId: product?.id ?? null,
            productLabel: product?.name ?? '',
            unitPrice: product ? String(product.purchasePrice ?? product.unitPrice ?? '') : line.unitPrice,
          })
        }}
      />
      <Input label="Quantité" inputMode="numeric" value={line.quantity} onChange={(event) => onChange(line.key, { quantity: event.target.value })} />
      <Input label="Prix unitaire (Ar)" inputMode="decimal" value={line.unitPrice} onChange={(event) => onChange(line.key, { unitPrice: event.target.value })} />
      <p className="pb-3 text-label text-ink-secondary">{formatOrderAmount(lineTotal(line))}</p>
      <IconButton aria-label="Retirer la ligne" disabled={!removable} onClick={() => onRemove(line.key)}>
        <Trash2 aria-hidden="true" />
      </IconButton>
    </div>
  )
}
