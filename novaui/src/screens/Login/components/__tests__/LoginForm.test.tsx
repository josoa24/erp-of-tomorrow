import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LoginForm } from '../LoginForm'

const fill = async (email: string, password: string) => {
  await userEvent.type(screen.getByLabelText('Adresse e-mail'), email)
  await userEvent.type(screen.getByLabelText('Mot de passe'), password)
}

describe('LoginForm', () => {
  it('shouldSubmitTrimmedCredentials_withKeyboard', async () => {
    const onSubmit = vi.fn()
    render(<LoginForm submitting={false} onSubmit={onSubmit} />)

    await fill('  hery@boutique-tana.mg ', 'secret{Enter}')

    expect(onSubmit).toHaveBeenCalledWith({ email: 'hery@boutique-tana.mg', password: 'secret' })
  })

  it('shouldDisableButton_whileAFieldIsEmpty', async () => {
    render(<LoginForm submitting={false} onSubmit={vi.fn()} />)

    expect(screen.getByRole('button', { name: 'Se connecter' })).toBeDisabled()
    await userEvent.type(screen.getByLabelText('Adresse e-mail'), 'hery@boutique-tana.mg')

    expect(screen.getByRole('button', { name: 'Se connecter' })).toBeDisabled()
  })

  it('shouldShowLoadingLabel_andIgnoreSubmit_whileSubmitting', async () => {
    const onSubmit = vi.fn()
    render(<LoginForm initialEmail="hery@boutique-tana.mg" submitting onSubmit={onSubmit} />)
    await userEvent.type(screen.getByLabelText('Mot de passe'), 'secret{Enter}')

    await userEvent.click(screen.getByRole('button', { name: 'Exécution…' }))

    expect(screen.getByRole('button', { name: 'Exécution…' })).toHaveAttribute('aria-busy', 'true')
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('shouldPrefillEmailOnly_whenComingBackFromErrorPage', () => {
    render(<LoginForm initialEmail="hery@boutique-tana.mg" submitting={false} onSubmit={vi.fn()} />)

    expect(screen.getByLabelText('Adresse e-mail')).toHaveValue('hery@boutique-tana.mg')
    expect(screen.getByLabelText('Mot de passe')).toHaveValue('')
  })

  it('shouldNotOfferRememberMe', () => {
    render(<LoginForm submitting={false} onSubmit={vi.fn()} />)

    expect(screen.queryByRole('checkbox')).not.toBeInTheDocument()
  })
})
