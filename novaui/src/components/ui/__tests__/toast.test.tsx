import { act, render, screen } from '@testing-library/react'
import { fireEvent } from '@testing-library/react'
import { Toast } from '../toast'
import { useToast } from '../toast-context'
import { ToastProvider } from '../toast-provider'

describe('Toast', () => {
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => vi.useRealTimers())

  it('shouldAnnounceSuccess_asStatus', () => {
    render(<Toast message="Module créé" onDismiss={vi.fn()} />)

    expect(screen.getByRole('status')).toHaveTextContent('Module créé')
  })

  it('shouldDismissAutomatically_afterDuration', () => {
    const onDismiss = vi.fn()
    render(<Toast message="Module créé" onDismiss={onDismiss} duration={3000} />)

    act(() => vi.advanceTimersByTime(3000))

    expect(onDismiss).toHaveBeenCalledTimes(1)
  })

  it('shouldRunUndoAction_andDismiss', () => {
    const onAction = vi.fn()
    const onDismiss = vi.fn()
    render(<Toast message="Adaptation appliquée" actionLabel="Annuler" onAction={onAction} onDismiss={onDismiss} />)

    fireEvent.click(screen.getByRole('button', { name: 'Annuler' }))

    expect(onAction).toHaveBeenCalledTimes(1)
    expect(onDismiss).toHaveBeenCalledTimes(1)
  })
})

const Trigger = () => {
  const { showToast } = useToast()
  return (
    <button type="button" onClick={() => showToast({ message: '9 livraisons créées' })}>
      Reprendre
    </button>
  )
}

describe('ToastProvider', () => {
  beforeEach(() => vi.useFakeTimers())
  afterEach(() => vi.useRealTimers())

  it('shouldShowToast_thenHideIt', () => {
    render(
      <ToastProvider>
        <Trigger />
      </ToastProvider>,
    )

    fireEvent.click(screen.getByRole('button', { name: 'Reprendre' }))
    expect(screen.getByRole('status')).toHaveTextContent('9 livraisons créées')
    act(() => vi.advanceTimersByTime(5000))

    expect(screen.queryByRole('status')).not.toBeInTheDocument()
  })
})
