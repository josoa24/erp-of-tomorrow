import { CenteredLayout } from '../../components/common/CenteredLayout'
import { Panel } from '../../components/ui/panel'
import { useLogin } from '../../hooks/useLogin'
import { LoginForm } from './components/LoginForm'

export const LoginScreen = () => {
  const { submit, submitting } = useLogin()

  return (
    <CenteredLayout>
      <Panel padding="xl" className="flex w-full flex-col gap-5 max-sm:px-5 max-sm:py-6">
        <div className="flex flex-col gap-1">
          <h1 className="text-heading text-ink-primary">Connexion</h1>
          <p className="text-body-s text-ink-secondary">Accédez à votre espace de travail.</p>
        </div>
        <LoginForm onSubmit={submit} submitting={submitting} />
      </Panel>
      <p className="text-center text-micro text-ink-tertiary">Pas de compte ? Contactez votre administrateur.</p>
    </CenteredLayout>
  )
}
