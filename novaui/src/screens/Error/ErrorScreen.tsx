import { TriangleAlert } from 'lucide-react'
import { useNavigate, useRouter, useRouterState } from '@tanstack/react-router'
import { CenteredLayout } from '../../components/common/CenteredLayout'
import { Button } from '../../components/ui/button'
import { Panel } from '../../components/ui/panel'
import { ERROR_CONTENT } from '../../constants/errorContent'
import { ROUTES } from '../../navigation/routes'

export const ErrorScreen = () => {
  const navigate = useNavigate()
  const { history } = useRouter()
  const error = useRouterState({ select: (state) => state.location.state.error })
  const content = ERROR_CONTENT[error?.type ?? 'notFound']

  return (
    <CenteredLayout>
      <Panel tone="elevated" padding="xl" className="flex w-full flex-col items-center gap-4 text-center">
        <span className="inline-flex size-[72px] items-center justify-center rounded-pill bg-status-critical-bg">
          <TriangleAlert aria-hidden="true" className="size-8 text-status-critical-fg" />
        </span>
        <h1 className="text-title text-ink-primary">{content.title}</h1>
        <p className="text-body text-ink-secondary">{error?.message ?? content.message}</p>
        <div className="mt-2 grid w-full gap-3">
          <Button onClick={() => history.push(error?.from ?? ROUTES.home)}>Réessayer</Button>
          <Button variant="secondary" onClick={() => void navigate({ to: ROUTES.home })}>
            Retour
          </Button>
        </div>
      </Panel>
    </CenteredLayout>
  )
}
