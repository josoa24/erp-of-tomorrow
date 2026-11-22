import { TriangleAlert } from 'lucide-react'
import { useLocation, useNavigate } from 'react-router-dom'
import { CenteredLayout } from '../../components/common/CenteredLayout'
import { GlassCard } from '../../components/common/GlassCard'
import { PillButton } from '../../components/common/PillButton'
import { ERROR_CONTENT } from '../../constants/errorContent'
import { ROUTES } from '../../navigation/routes'
import type { ErrorRouteState } from '../../types/errorRoute'

export const ErrorScreen = () => {
  const navigate = useNavigate()
  const state = useLocation().state as ErrorRouteState | null
  const content = ERROR_CONTENT[state?.type ?? 'notFound']

  return (
    <CenteredLayout>
      <GlassCard variant="outline" className="flex w-[min(440px,100%)] flex-col items-center gap-3.5 px-7.5 pt-8 pb-7.5 text-center">
        <TriangleAlert className="text-[#e9a6ff] drop-shadow-[0_0_10px_rgba(220,120,255,0.6)]" size={40} strokeWidth={1.5} aria-hidden="true" />
        <h1 className="text-2xl font-semibold">{content.title}</h1>
        <p className="text-base text-nova-muted">{state?.message ?? content.message}</p>
        <div className="mt-2.5 grid w-full gap-3">
          <PillButton onClick={() => navigate(state?.from ?? ROUTES.home)}>Réessayer</PillButton>
          <PillButton variant="ghost" onClick={() => navigate(ROUTES.home)}>
            Retour
          </PillButton>
        </div>
      </GlassCard>
    </CenteredLayout>
  )
}
