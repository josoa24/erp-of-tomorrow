import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { EclipseDockProvider } from '../../shell/EclipseDockProvider'
import { useEclipseDock } from '../../shell/eclipse-dock-context'
import { AdaptationBar, type AdaptationBarProps } from '../AdaptationBar'

const DockProbe = () => <p>{useEclipseDock().hidden ? 'dock masqué' : 'dock visible'}</p>

const renderBar = (props: Partial<AdaptationBarProps> = {}, visible = true) =>
  render(
    <EclipseDockProvider>
      <DockProbe />
      {visible && (
        <AdaptationBar
          request="Ajoute le n° de lot et la date de péremption"
          summary="Eclipse a ajouté : colonnes N° de lot et Péremption"
          canApply
          onApply={vi.fn()}
          onCancel={vi.fn()}
          {...props}
        />
      )}
    </EclipseDockProvider>,
  )

describe('AdaptationBar', () => {
  it('shouldShowRequestAndSummary', () => {
    renderBar()

    const bar = screen.getByRole('region', { name: "Proposition d'Eclipse" })
    expect(bar).toHaveTextContent('« Ajoute le n° de lot et la date de péremption »')
    expect(bar).toHaveTextContent('Eclipse a ajouté : colonnes N° de lot et Péremption')
  })

  it('shouldApplyOrCancel_whenAllowed', async () => {
    const onApply = vi.fn()
    const onCancel = vi.fn()
    renderBar({ onApply, onCancel })

    await userEvent.click(screen.getByRole('button', { name: 'Appliquer pour l’équipe' }))
    await userEvent.click(screen.getByRole('button', { name: 'Annuler' }))

    expect(onApply).toHaveBeenCalledTimes(1)
    expect(onCancel).toHaveBeenCalledTimes(1)
  })

  it('shouldReplaceApplyByNotice_whenUserCannotApply', () => {
    renderBar({ canApply: false })

    expect(screen.queryByRole('button', { name: 'Appliquer pour l’équipe' })).not.toBeInTheDocument()
    expect(screen.getByText('Seul un administrateur peut appliquer')).toBeInTheDocument()
  })

  it('shouldShowExecution_whileApplying', () => {
    renderBar({ applying: true })

    expect(screen.getByRole('button', { name: 'Exécution…' })).toHaveAttribute('aria-busy', 'true')
  })

  it('shouldHideTheDock_whileVisible', () => {
    const { rerender } = renderBar()
    expect(screen.getByText('dock masqué')).toBeInTheDocument()

    rerender(
      <EclipseDockProvider>
        <DockProbe />
      </EclipseDockProvider>,
    )

    expect(screen.getByText('dock visible')).toBeInTheDocument()
  })
})
