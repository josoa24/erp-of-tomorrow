import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AiButton } from '../ai-button'
import { Button } from '../button'

describe('Button', () => {
  it('shouldCallOnClick', async () => {
    const onClick = vi.fn()
    render(<Button onClick={onClick}>Valider</Button>)
    await userEvent.click(screen.getByRole('button', { name: 'Valider' }))
    expect(onClick).toHaveBeenCalledTimes(1)
  })

  it('shouldBlockClickAndSetBusy_whenLoading', async () => {
    const onClick = vi.fn()
    render(<Button loading onClick={onClick}>Valider</Button>)
    const button = screen.getByRole('button', { name: /Valider/ })
    await userEvent.click(button)
    expect(onClick).not.toHaveBeenCalled()
    expect(button).toHaveAttribute('aria-busy', 'true')
  })

  it('shouldDefaultToTypeButton', () => {
    render(<Button>Ok</Button>)
    expect(screen.getByRole('button')).toHaveAttribute('type', 'button')
  })
})

describe('AiButton', () => {
  it('shouldShowDefaultLabel', () => {
    render(<AiButton />)
    expect(screen.getByRole('button', { name: 'Demander à l’IA' })).toBeInTheDocument()
  })
})
