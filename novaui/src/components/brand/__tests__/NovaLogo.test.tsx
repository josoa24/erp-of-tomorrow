import { render, screen } from '@testing-library/react'
import logotypeUrl from '../../../assets/nova-logotype.svg'
import symbolUrl from '../../../assets/nova-symbol.svg'
import { NovaLogo } from '../NovaLogo'

describe('NovaLogo', () => {
  it('shouldRenderHeaderLogotype_whenNoSizeIsGiven', () => {
    render(<NovaLogo />)

    const logo = screen.getByRole('img', { name: 'NOVA' })
    expect(logo).toHaveAttribute('height', '23')
    expect(logo).toHaveAttribute('src', logotypeUrl)
  })

  it('shouldRenderLoginSize_whenSizeIsL', () => {
    render(<NovaLogo size="l" />)

    expect(screen.getByRole('img', { name: 'NOVA' })).toHaveAttribute('height', '48')
  })

  it('shouldRenderSymbol_whenSymbolOnly', () => {
    render(<NovaLogo symbolOnly size="m" />)

    const logo = screen.getByRole('img', { name: 'NOVA' })
    expect(logo).toHaveAttribute('src', symbolUrl)
    expect(logo).toHaveAttribute('width', '54.1')
  })
})
