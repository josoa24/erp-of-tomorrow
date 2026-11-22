import { render, screen } from '@testing-library/react'
import { useLogin } from '../../../hooks/useLogin'
import { LoginScreen } from '../LoginScreen'

vi.mock('../../../hooks/useLogin', () => ({ useLogin: vi.fn() }))

describe('LoginScreen', () => {
  beforeEach(() => {
    vi.mocked(useLogin).mockReturnValue({ submit: vi.fn(), submitting: false, initialEmail: 'hery@boutique-tana.mg' })
  })

  it('shouldPresentBrandCardAndHelp', () => {
    render(<LoginScreen />)

    expect(screen.getByRole('img', { name: 'NOVA' })).toBeInTheDocument()
    expect(screen.getByText('Le socle ERP que l’IA adapte à votre entreprise.')).toBeInTheDocument()
    expect(screen.getByRole('heading', { level: 1, name: 'Connexion' })).toBeInTheDocument()
    expect(screen.getByText('Utilisez l’adresse fournie par votre administrateur.')).toBeInTheDocument()
    expect(screen.getByText('Mot de passe oublié ? Contactez l’administrateur de votre entreprise.')).toBeInTheDocument()
  })

  it('shouldPrefillRememberedEmail', () => {
    render(<LoginScreen />)

    expect(screen.getByLabelText('Adresse e-mail')).toHaveValue('hery@boutique-tana.mg')
  })
})
