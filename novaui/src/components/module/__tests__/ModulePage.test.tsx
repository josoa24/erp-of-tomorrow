import { render, screen } from '@testing-library/react'
import { Clock, Package } from 'lucide-react'
import { Button } from '../../ui/button'
import { FilterChip } from '../../ui/filter-chip'
import { FilterBar } from '../FilterBar'
import { ListCard } from '../ListCard'
import { ModulePage } from '../ModulePage'

describe('ModulePage', () => {
  it('shouldRenderHeaderKpisAndList', () => {
    render(
      <ModulePage
        title="Stock"
        subtitle="64 produits · 5 en alerte"
        search={{ placeholder: 'Rechercher un produit…', onSearch: vi.fn() }}
        primaryAction={<Button>Entrée de stock</Button>}
        kpis={[
          { key: 'availability', icon: Package, tone: 'mint', label: 'Disponibilité', value: '92,2 %' },
          { key: 'expiring', icon: Clock, label: 'Expire bientôt', value: 3, highlighted: true },
        ]}
      >
        <ListCard label="Produits" toolbar={<FilterBar sortHint="Triés par niveau de stock"><FilterChip label="Tous" count={64} active /></FilterBar>}>
          <p>Tableau</p>
        </ListCard>
      </ModulePage>,
    )

    expect(screen.getByRole('heading', { level: 1, name: 'Stock' })).toHaveClass('text-display-m')
    expect(screen.getByText('64 produits · 5 en alerte')).toBeInTheDocument()
    expect(screen.getByRole('searchbox', { name: 'Rechercher un produit…' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Entrée de stock' })).toBeInTheDocument()
    expect(screen.getByRole('region', { name: 'Indicateurs' })).toHaveTextContent('92,2 %')
    expect(screen.getByRole('group', { name: 'Filtres' })).toHaveTextContent('Tous')
    expect(screen.getByText('Triés par niveau de stock')).toBeInTheDocument()
    expect(screen.getByRole('region', { name: 'Produits' })).toHaveTextContent('Tableau')
  })

  it('shouldOmitKpiStrip_whenNoKpiIsGiven', () => {
    render(<ModulePage title="Mon profil" />)

    expect(screen.queryByRole('region', { name: 'Indicateurs' })).not.toBeInTheDocument()
    expect(screen.queryByRole('searchbox')).not.toBeInTheDocument()
  })
})
