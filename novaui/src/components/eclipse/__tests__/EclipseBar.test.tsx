import { fireEvent, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { useState } from 'react'
import { EclipseBar, type EclipseBarProps } from '../EclipseBar'

const Harness = (props: Partial<EclipseBarProps>) => {
  const [value, setValue] = useState('')
  return (
    <EclipseBar
      placeholder="Demandez ou adaptez « Stock »…"
      value={value}
      onChange={setValue}
      onSubmit={vi.fn()}
      {...props}
    />
  )
}

const input = () => screen.getByRole('textbox', { name: 'Demander à Eclipse' })

describe('EclipseBar', () => {
  it('shouldSubmitTrimmedText_onEnter', async () => {
    const onSubmit = vi.fn()
    render(<Harness onSubmit={onSubmit} />)

    await userEvent.type(input(), '  produits à commander {Enter}')

    expect(onSubmit).toHaveBeenCalledWith('produits à commander')
  })

  it('shouldInsertNewline_onShiftEnter_withoutSubmitting', async () => {
    const onSubmit = vi.fn()
    render(<Harness onSubmit={onSubmit} />)

    await userEvent.type(input(), 'ligne 1{Shift>}{Enter}{/Shift}ligne 2')

    expect(onSubmit).not.toHaveBeenCalled()
    expect(input()).toHaveValue('ligne 1\nligne 2')
  })

  it('shouldIgnoreEmptySubmission', async () => {
    const onSubmit = vi.fn()
    render(<Harness onSubmit={onSubmit} />)

    await userEvent.click(screen.getByRole('button', { name: 'Envoyer à Eclipse' }))

    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('shouldFocusOnCtrlK_andBlurOnEscape', async () => {
    render(<Harness />)

    fireEvent.keyDown(window, { key: 'k', ctrlKey: true })
    expect(input()).toHaveFocus()
    await userEvent.keyboard('{Escape}')

    expect(input()).not.toHaveFocus()
  })

  it('shouldOfferStop_whileStreaming', async () => {
    const onStop = vi.fn()
    render(<Harness status="streaming" onStop={onStop} />)

    await userEvent.click(screen.getByRole('button', { name: 'Arrêter la réponse' }))

    expect(onStop).toHaveBeenCalledTimes(1)
  })

  it('shouldDisableInputAndSend_whenUnavailable', () => {
    render(<Harness status="unavailable" unavailableLabel="Eclipse arrive bientôt" />)

    expect(input()).toBeDisabled()
    expect(screen.getByRole('button', { name: 'Envoyer à Eclipse' })).toBeDisabled()
    expect(input()).toHaveAttribute('placeholder', 'Eclipse arrive bientôt')
  })

  it('shouldDrawSignatureLines_onNightVariant', () => {
    const { container } = render(<Harness variant="night" />)

    expect(container.querySelector('img[aria-hidden="true"]')).toBeInTheDocument()
    expect(container.firstElementChild).toHaveClass('bg-night-bar')
  })
})
