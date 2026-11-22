import { render, screen } from '@testing-library/react'
import { Package } from 'lucide-react'
import { Count } from '../count'
import { EmptyState } from '../empty-state'
import { Kbd } from '../kbd'
import { Panel } from '../panel'
import { TableSkeleton } from '../skeleton'
import { Stat } from '../stat'

describe('display primitives', () => {
  it('shouldRenderInverseKbd_onDarkSurfaces', () => {
    render(<Kbd inverse>Ctrl K</Kbd>)

    expect(screen.getByText('Ctrl K')).toHaveClass('text-ink-inverse')
  })

  it('shouldRenderPanelWithTranslucentSurfaceByDefault', () => {
    render(<Panel aria-label="Indicateurs">contenu</Panel>)

    expect(screen.getByRole('region', { name: 'Indicateurs' })).toHaveClass('bg-surface-panel', 'rounded-xl', 'p-5')
  })

  it('shouldCapCount_whenAboveMax', () => {
    render(<Count value={150} tone="night" />)

    expect(screen.getByText('99+')).toHaveClass('bg-surface-night')
  })

  it('shouldRenderStatLabelValueAndHint', () => {
    render(<Stat icon={Package} tone="mint" label="Disponibilité" value="92,2 %" hint="59 produits au-dessus du seuil" />)

    expect(screen.getByText('Disponibilité')).toHaveClass('uppercase', 'text-caption')
    expect(screen.getByText('92,2 %')).toHaveClass('text-number-m')
    expect(screen.getByText('59 produits au-dessus du seuil')).toBeInTheDocument()
  })

  it('shouldWrapStatInDashedSuccessStyle_whenHighlighted', () => {
    const { container } = render(<Stat icon={Package} label="Expire bientôt" value={3} highlighted />)

    expect(container.firstElementChild).toHaveClass('highlight-added')
  })

  it('shouldRenderEmptyStateTitleDescriptionAndActions', () => {
    render(
      <EmptyState
        icon={Package}
        title="Aucune livraison"
        description="Rien à livrer pour le moment."
        actions={<button type="button">Créer</button>}
      />,
    )

    expect(screen.getByRole('heading', { name: 'Aucune livraison' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Créer' })).toBeInTheDocument()
  })

  it('shouldAnnounceTableSkeleton_asLoadingStatus', () => {
    render(<TableSkeleton rows={3} columns={2} />)

    expect(screen.getByRole('status', { name: 'Chargement…' })).toBeInTheDocument()
  })
})
