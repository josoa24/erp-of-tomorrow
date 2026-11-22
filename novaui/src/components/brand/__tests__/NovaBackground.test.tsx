import { render } from '@testing-library/react'
import signatureLinesUrl from '../../../assets/signature-lines.svg'
import { NovaBackground } from '../NovaBackground'

describe('NovaBackground', () => {
  it('shouldStayDecorative_withSignatureLines', () => {
    const { container } = render(<NovaBackground />)

    const layer = container.firstElementChild
    expect(layer).toHaveAttribute('aria-hidden', 'true')
    expect(layer?.querySelector('img')).toHaveAttribute('src', signatureLinesUrl)
  })

  it('shouldClipOverflow_soThePageNeverScrollsHorizontally', () => {
    const { container } = render(<NovaBackground />)

    expect(container.firstElementChild).toHaveClass('fixed', 'inset-0', 'overflow-hidden')
  })
})
