import { useMemo, useState } from 'react'
import { Combobox } from '@/components/ui/combobox'
import { useCatalogProducts } from '../hooks/useCatalogProducts'
import type { DraftLine } from '../services/saleDraft'
import type { CatalogProduct } from '../types/catalog'
import { DraftLineRow } from './DraftLineRow'

interface ProductLinesEditorProps {
  lines: DraftLine[]
  onAdd: (product: CatalogProduct) => void
  onQuantity: (productId: number, quantity: number) => void
  onRemove: (productId: number) => void
}

export const ProductLinesEditor = ({ lines, onAdd, onQuantity, onRemove }: ProductLinesEditorProps) => {
  const [search, setSearch] = useState('')
  const { products, loading } = useCatalogProducts(search)
  const options = useMemo(
    () => products.map((product) => ({ value: String(product.id), label: product.name, description: `${product.quantity} en stock` })),
    [products],
  )

  return (
    <div className="flex min-w-0 flex-col gap-3">
      <Combobox
        label="Produits"
        placeholder="Ajouter un produit"
        options={options}
        loading={loading}
        onQueryChange={setSearch}
        onSelect={(option) => {
          const product = products.find((item) => String(item.id) === option.value)
          if (product) onAdd(product)
        }}
      />
      <ul className="flex flex-col gap-3">
        {lines.map((line) => (
          <DraftLineRow key={line.productId} line={line} onQuantity={onQuantity} onRemove={onRemove} />
        ))}
      </ul>
    </div>
  )
}
