import { render, screen } from '@testing-library/react'
import { Avatar } from '../avatar'

describe('Avatar', () => {
  it('shouldShowInitialsAndUseNameAsLabel', () => {
    render(<Avatar name="Hery Rabe" size="l" />)

    const avatar = screen.getByRole('img', { name: 'Hery Rabe' })
    expect(avatar).toHaveTextContent('HR')
    expect(avatar).toHaveClass('size-14', 'bg-status-info-vivid')
  })

  it('shouldPreferExplicitInitials', () => {
    render(<Avatar name="Rasoa Andria" initials="RA" tone="mint" />)

    expect(screen.getByRole('img', { name: 'Rasoa Andria' })).toHaveTextContent('RA')
  })

  it('shouldHideFromAssistiveTech_whenDecorative', () => {
    render(<Avatar name="Toky Rakoto" decorative />)

    expect(screen.queryByRole('img')).not.toBeInTheDocument()
    expect(screen.getByText('TR')).toHaveAttribute('aria-hidden', 'true')
  })
})
