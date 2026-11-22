import { render, screen } from '@testing-library/react'
import { ShieldCheck } from 'lucide-react'
import { StatusPill } from '../status-pill'

describe('StatusPill', () => {
  it('shouldAlwaysShowIconAndLabel', () => {
    render(<StatusPill tone="critical" label="Risque de rupture" />)

    const pill = screen.getByText('Risque de rupture')
    expect(pill.querySelector('svg')).toHaveAttribute('aria-hidden', 'true')
    expect(pill).toHaveClass('bg-status-critical-bg', 'text-status-critical-fg')
  })

  it('shouldUseVividBackground_whenEmphasisIsVivid', () => {
    render(<StatusPill tone="success" emphasis="vivid" label="En stock" />)

    expect(screen.getByText('En stock')).toHaveClass('bg-status-success-vivid', 'text-ink-primary')
  })

  it('shouldUseSunkenNeutralByDefault_withCustomIcon', () => {
    render(<StatusPill label="Administrateur" icon={ShieldCheck} />)

    const pill = screen.getByText('Administrateur')
    expect(pill).toHaveClass('bg-surface-sunken', 'text-ink-secondary')
    expect(pill.querySelector('.lucide-shield-check')).toBeInTheDocument()
  })
})
