import { Lock } from 'lucide-react'
import { Navigate } from 'react-router-dom'
import { CenteredLayout } from '../../components/common/CenteredLayout'
import { GlassCard } from '../../components/common/GlassCard'
import { useLogin } from '../../hooks/useLogin'
import { ROUTES } from '../../navigation/routes'
import { readSession } from '../../services/sessionStore'
import { LoginForm } from './components/LoginForm'

export const LoginScreen = () => {
  const { submit, submitting } = useLogin()

  if (readSession()) return <Navigate to={ROUTES.home} replace />

  return (
    <CenteredLayout>
      <GlassCard variant="outline" className="flex w-[min(414px,100%)] flex-col items-center px-7.5 py-5.5 -mt-20 max-sm:mt-0 max-sm:px-5">
        <Lock className="text-white" size={40} strokeWidth={1.6} aria-hidden="true" />
        <h1 className="mt-3 mb-6.5 text-2xl font-semibold">Connexion</h1>
        <LoginForm onSubmit={submit} submitting={submitting} />
        <p className="mt-4.5 text-center text-[15px] text-[#e4e5ff]">Pas de compte ? Contactez votre administrateur.</p>
      </GlassCard>
    </CenteredLayout>
  )
}
