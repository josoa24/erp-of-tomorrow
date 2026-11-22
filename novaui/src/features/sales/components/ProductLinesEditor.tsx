import { useState } from 'react'
import { SearchField } from '../../../components/ui/search-field'
import { PICKER_RESULTS_LIMIT, useCatalogProducts } from '../hooks/useCatalogProducts'
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

  return (
    <div className="flex min-w-0 flex-col gap-3">
      <p className="text-label text-ink-primary">Produits</p>
      <SearchField placeholder="Ajouter un produit…" aria-label="Rechercher un produit à ajouter" slashShortcut={false} onSearch={setSearch} />
      {search !== '' && (
        <ul aria-label={`${PICKER_RESULTS_LIMIT} produits au plus`} className="flex flex-col gap-0.5 rounded-sm border border-line-subtle p-1">
          {products.map((product) => (
            <li key={product.id}>
              <button
                type="button"
                className="flex min-h-11 w-full items-center justify-between gap-3 rounded-sm px-3 text-left text-body-s hover:bg-state-hover focus-visible:shadow-focus-ring"
                onClick={() => onAdd(product)}
              >
                <span>{product.name}</span>
                <span className="text-micro text-ink-tertiary">{product.quantity} en stock</span>
              </button>
            </li>
          ))}
          {!loading && products.length === 0 && <li className="px-3 py-2 text-body-s text-ink-tertiary">Aucun produit ne correspond.</li>}
        </ul>
      )}
      <ul className="flex flex-col gap-3">
        {lines.map((line) => (
          <DraftLineRow key={line.productId} line={line} onQuantity={onQuantity} onRemove={onRemove} />
        ))}
      </ul>
    </div>
  )
}
