import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AiButton } from '../ai-button'
import { Button } from '../button'
import { IconButton } from '../icon-button'

describe('Button', () => {
  it('shouldCallOnClick', async () => {
    const onClick = vi.fn()
    render(<Button onClick={onClick}>Valider</Button>)

    await userEvent.click(screen.getByRole('button', { name: 'Valider' }))

    expect(onClick).toHaveBeenCalledTimes(1)
  })

  it('shouldShowExecutionLabelAndBlockClick_whenLoading', async () => {
    const onClick = vi.fn()
    render(<Button loading onClick={onClick}>Valider</Button>)

    const button = screen.getByRole('button', { name: 'Exécution…' })
    await userEvent.click(button)

    expect(onClick).not.toHaveBeenCalled()
    expect(button).toHaveAttribute('aria-busy', 'true')
    expect(button).toHaveAttribute('aria-disabled', 'true')
  })

  it('shouldNotSubmitTheFormTwice_whenLoading', async () => {
    const onSubmit = vi.fn((event: SubmitEvent) => event.preventDefault())
    render(
      <form onSubmit={(event) => onSubmit(event.nativeEvent as SubmitEvent)}>
        <Button type="submit" loading>Enregistrer</Button>
      </form>,
    )

    await userEvent.click(screen.getByRole('button'))

    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('shouldExposeDisabledState_whenDisabled', () => {
    render(<Button disabled>Valider</Button>)

    const button = screen.getByRole('button', { name: 'Valider' })
    expect(button).toBeDisabled()
    expect(button).toHaveAttribute('aria-disabled', 'true')
    expect(button).toHaveClass('disabled:opacity-40')
  })

  it('shouldDefaultToTypeButton', () => {
    render(<Button>Ok</Button>)

    expect(screen.getByRole('button')).toHaveAttribute('type', 'button')
  })

  it('shouldApplyEclipseGradient_whenVariantIsEclipse', () => {
    render(<Button variant="eclipse" size="s">Appliquer</Button>)

    expect(screen.getByRole('button')).toHaveClass('bg-night', 'h-9')
  })
})

describe('AiButton', () => {
  it('shouldShowDefaultLabel_withEclipseStyle', () => {
    render(<AiButton />)

    expect(screen.getByRole('button', { name: 'Demander à l’IA' })).toHaveClass('bg-night')
  })
})

describe('IconButton', () => {
  it('shouldExposeItsAccessibleLabel', () => {
    render(<IconButton aria-label="Créer un module" variant="surface" size="m">+</IconButton>)

    expect(screen.getByRole('button', { name: 'Créer un module' })).toHaveClass('size-9')
  })
})
