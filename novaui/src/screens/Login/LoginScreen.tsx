import { CenteredLayout } from '../../components/common/CenteredLayout'
import { Panel } from '../../components/ui/panel'
import { useLogin } from '../../hooks/useLogin'
import { LoginForm } from './components/LoginForm'

const LOGIN_TAGLINE = 'Le socle ERP que l’IA adapte à votre entreprise.'

export const LoginScreen = () => {
  const { submit, submitting, initialEmail } = useLogin()

  return (
    <CenteredLayout tagline={LOGIN_TAGLINE}>
      <Panel padding="xl" className="flex w-full flex-col gap-5 max-sm:p-5">
        <div className="flex flex-col gap-1">
          <h1 className="text-heading text-ink-primary">Connexion</h1>
          <p className="text-body-s text-ink-secondary">Utilisez l’adresse fournie par votre administrateur.</p>
        </div>
        <LoginForm initialEmail={initialEmail} onSubmit={submit} submitting={submitting} />
      </Panel>
      <p className="text-center text-micro text-ink-tertiary">
        Mot de passe oublié ? Contactez l’administrateur de votre entreprise.
      </p>
    </CenteredLayout>
  )
}
