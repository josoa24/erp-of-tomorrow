import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FilterChip } from '../filter-chip'

describe('FilterChip', () => {
  it('shouldExposePressedStateAndCount', () => {
    render(<FilterChip label="En alerte" count={5} />)

    const chip = screen.getByRole('button', { name: 'En alerte, 5' })
    expect(chip).toHaveAttribute('aria-pressed', 'false')
  })

  it('shouldShowSelectedStyleAndClearIcon_whenActive', () => {
    render(<FilterChip label="Tous" count={64} active />)

    const chip = screen.getByRole('button', { name: 'Tous, 64' })
    expect(chip).toHaveAttribute('aria-pressed', 'true')
    expect(chip).toHaveClass('bg-state-selected', 'border-brand-lilac')
    expect(chip.querySelector('svg')).toBeInTheDocument()
  })

  it('shouldCallOnClick_whenToggled', async () => {
    const onClick = vi.fn()
    render(<FilterChip label="Rupture" onClick={onClick} />)

    await userEvent.click(screen.getByRole('button', { name: 'Rupture' }))

    expect(onClick).toHaveBeenCalledTimes(1)
  })

  it('shouldWrapInDashedSuccessStyle_whenHighlighted', () => {
    render(<FilterChip label="Expire bientôt" count={3} highlighted />)

    expect(screen.getByRole('button').parentElement).toHaveClass('highlight-added')
  })
})
