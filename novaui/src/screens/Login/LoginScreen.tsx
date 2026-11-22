import { Lock } from 'lucide-react'
import { Navigate } from 'react-router-dom'
import { CenteredLayout } from '../../components/common/CenteredLayout'
import { GlassCard } from '../../components/common/GlassCard'
import { useLogin } from '../../hooks/useLogin'
import { ROUTES } from '../../navigation/routes'
import { readSession } from '../../services/sessionStore'
import { LoginForm } from './components/LoginForm'
import styles from './LoginScreen.module.css'

export const LoginScreen = () => {
  const { submit, submitting } = useLogin()

  if (readSession()) return <Navigate to={ROUTES.home} replace />

  return (
    <CenteredLayout>
      <GlassCard variant="outline" className={styles.card}>
        <Lock className={styles.icon} size={40} strokeWidth={1.6} aria-hidden="true" />
        <h1 className={styles.title}>Connexion</h1>
        <LoginForm onSubmit={submit} submitting={submitting} />
        <p className={styles.footer}>Pas de compte ? Contactez votre administrateur.</p>
      </GlassCard>
    </CenteredLayout>
  )
}
