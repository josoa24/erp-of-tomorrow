import { TriangleAlert } from 'lucide-react'
import { useLocation, useNavigate } from 'react-router-dom'
import { CenteredLayout } from '../../components/common/CenteredLayout'
import { GlassCard } from '../../components/common/GlassCard'
import { PillButton } from '../../components/common/PillButton'
import { ERROR_CONTENT } from '../../constants/errorContent'
import { ROUTES } from '../../navigation/routes'
import type { ErrorRouteState } from '../../types/errorRoute'
import styles from './ErrorScreen.module.css'

export const ErrorScreen = () => {
  const navigate = useNavigate()
  const state = useLocation().state as ErrorRouteState | null
  const content = ERROR_CONTENT[state?.type ?? 'notFound']

  return (
    <CenteredLayout>
      <GlassCard variant="outline" className={styles.card}>
        <TriangleAlert className={styles.icon} size={40} strokeWidth={1.5} aria-hidden="true" />
        <h1 className={styles.title}>{content.title}</h1>
        <p className={styles.message}>{state?.message ?? content.message}</p>
        <div className={styles.actions}>
          <PillButton onClick={() => navigate(state?.from ?? ROUTES.home)}>Réessayer</PillButton>
          <PillButton variant="ghost" onClick={() => navigate(ROUTES.home)}>
            Retour
          </PillButton>
        </div>
      </GlassCard>
    </CenteredLayout>
  )
}
