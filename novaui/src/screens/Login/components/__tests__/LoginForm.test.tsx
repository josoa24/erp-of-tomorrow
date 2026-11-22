import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LoginForm } from '../LoginForm'

describe('LoginForm', () => {
  it('shouldSubmitTrimmedCredentialsAndRememberChoice', async () => {
    const onSubmit = vi.fn()
    const user = userEvent.setup()
    render(<LoginForm submitting={false} onSubmit={onSubmit} />)

    await user.type(screen.getByLabelText('Adresse e-mail'), '  stock@erp.local ')
    await user.type(screen.getByLabelText('Mot de passe'), 'secret')
    await user.click(screen.getByLabelText('Se souvenir de moi'))
    await user.click(screen.getByRole('button', { name: 'Se connecter' }))

    expect(onSubmit).toHaveBeenCalledWith({ email: 'stock@erp.local', password: 'secret' }, true)
  })

  it('shouldNotSubmit_whenFieldsAreEmpty', async () => {
    const onSubmit = vi.fn()
    render(<LoginForm submitting={false} onSubmit={onSubmit} />)

    await userEvent.click(screen.getByRole('button', { name: 'Se connecter' }))

    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('shouldDisableButton_whileSubmitting', () => {
    render(<LoginForm submitting onSubmit={vi.fn()} />)

    expect(screen.getByRole('button', { name: 'Connexion…' })).toBeDisabled()
  })
})
