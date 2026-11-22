import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { useState } from 'react'
import { Dialog } from '../dialog'

const Harness = ({ onClose = vi.fn() }: { onClose?: () => void }) => {
  const [open, setOpen] = useState(false)
  const close = () => {
    onClose()
    setOpen(false)
  }
  return (
    <>
      <button type="button" onClick={() => setOpen(true)}>
        Créer un module
      </button>
      <Dialog open={open} onClose={close} title="Créer un module" description="Décrivez votre besoin" size="l">
        <input aria-label="Description" />
      </Dialog>
    </>
  )
}

const openDialog = async () => {
  await userEvent.click(screen.getByRole('button', { name: 'Créer un module' }))
  return screen.getByRole('dialog', { name: 'Créer un module' })
}

describe('Dialog', () => {
  it('shouldRenderNothing_whenClosed', () => {
    render(<Harness />)

    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
  })

  it('shouldBeModalLabelledAndDescribed_whenOpen', async () => {
    render(<Harness />)

    const dialog = await openDialog()

    expect(dialog).toHaveAttribute('aria-modal', 'true')
    expect(dialog).toHaveAccessibleDescription('Décrivez votre besoin')
    expect(dialog).toHaveClass('max-w-[880px]')
  })

  it('shouldMoveFocusInside_andTrapTab', async () => {
    render(<Harness />)
    await openDialog()

    expect(screen.getByRole('button', { name: 'Fermer' })).toHaveFocus()
    await userEvent.tab()
    expect(screen.getByRole('textbox', { name: 'Description' })).toHaveFocus()
    await userEvent.tab()
    expect(screen.getByRole('button', { name: 'Fermer' })).toHaveFocus()
  })

  it('shouldCloseOnEscape_andReturnFocusToTrigger', async () => {
    const onClose = vi.fn()
    render(<Harness onClose={onClose} />)
    await openDialog()

    await userEvent.keyboard('{Escape}')

    expect(onClose).toHaveBeenCalledTimes(1)
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Créer un module' })).toHaveFocus()
  })

  it('shouldClose_whenScrimIsClicked', async () => {
    const onClose = vi.fn()
    render(<Harness onClose={onClose} />)
    await openDialog()

    await userEvent.click(screen.getByTestId('dialog-scrim'))

    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('shouldClose_whenCloseButtonIsClicked', async () => {
    const onClose = vi.fn()
    render(<Harness onClose={onClose} />)
    await openDialog()

    await userEvent.click(screen.getByRole('button', { name: 'Fermer' }))

    expect(onClose).toHaveBeenCalledTimes(1)
  })
})
