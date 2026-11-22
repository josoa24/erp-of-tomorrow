import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { mockViewport } from '../../../test/mockViewport'
import { DataTable } from '../DataTable'
import type { DataTableProps } from '../types'
import { COLUMNS, PRODUCTS, actionsFor, type ProductRow } from './stockFixture'

const renderTable = (props: Partial<DataTableProps<ProductRow>> = {}) =>
  render(
    <DataTable<ProductRow>
      caption="Produits en stock"
      columns={COLUMNS}
      rows={PRODUCTS}
      getRowKey={(row) => row.id}
      getRowLabel={(row) => row.name}
      {...props}
    />,
  )

describe('DataTable on desktop', () => {
  beforeEach(() => mockViewport(1440))
  afterEach(() => vi.unstubAllGlobals())

  it('shouldRenderCaptionHeadersAndRows', () => {
    renderTable()

    const table = screen.getByRole('table', { name: 'Produits en stock' })
    expect(within(table).getAllByRole('columnheader').map((cell) => cell.textContent)).toEqual([
      'Produit',
      'Catégorie',
      'Stock',
      'N° de lotNouveau',
    ])
    expect(within(table).getByRole('cell', { name: 'Huile 5 L' })).toBeInTheDocument()
  })

  it('shouldExposeSortState_andHighlightNewColumn', () => {
    renderTable()

    expect(screen.getByRole('columnheader', { name: 'Catégorie' })).toHaveAttribute('aria-sort', 'ascending')
    expect(screen.getByRole('cell', { name: 'LOT-2610-C' })).toHaveClass('bg-status-success-bg')
    expect(screen.getByRole('cell', { name: '18 u.' })).toHaveClass('text-right')
  })

  it('shouldOpenRowActions_withRowLabel', async () => {
    const onView = vi.fn()
    renderTable({ rowActions: actionsFor(onView) })

    await userEvent.click(screen.getByRole('button', { name: 'Actions pour Huile 5 L' }))
    await userEvent.click(screen.getByRole('menuitem', { name: 'Voir' }))

    expect(onView).toHaveBeenCalledWith(PRODUCTS[1])
  })

  it('shouldShowSkeleton_whenLoading', () => {
    renderTable({ loading: true })

    expect(screen.getByRole('status', { name: 'Chargement…' })).toBeInTheDocument()
    expect(screen.queryByRole('table')).not.toBeInTheDocument()
  })

  it('shouldShowEmptySlot_whenNoRows', () => {
    renderTable({ rows: [], empty: <p>Aucun produit</p> })

    expect(screen.getByText('Aucun produit')).toBeInTheDocument()
  })
})

describe('DataTable on mobile', () => {
  beforeEach(() => mockViewport(375))
  afterEach(() => vi.unstubAllGlobals())

  it('shouldRenderEachRowAsACard_withLabelValuePairs', () => {
    renderTable({ rowActions: actionsFor(vi.fn()) })

    expect(screen.queryByRole('table')).not.toBeInTheDocument()
    const cards = within(screen.getByRole('list', { name: 'Produits en stock' })).getAllByRole('listitem')
    expect(cards).toHaveLength(2)
    expect(within(cards[1]).getAllByRole('term').map((term) => term.textContent)).toEqual(['Catégorie', 'Stock', 'N° de lotNouveau'])
    expect(within(cards[1]).getByText('Épicerie sèche').tagName).toBe('DD')
    expect(within(cards[1]).getByRole('button', { name: 'Actions pour Huile 5 L' })).toBeInTheDocument()
  })
})
