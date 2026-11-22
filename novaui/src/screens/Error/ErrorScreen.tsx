import { useNavigate, useRouter, useRouterState } from '@tanstack/react-router'
import { Button } from '../../components/ui/button'
import { Panel } from '../../components/ui/panel'
import { ROUTES } from '../../navigation/routes'
import { ErrorLayout } from './components/ErrorLayout'
import { ACTION_LABELS, DISPLAYS, errorChip, errorKindOf } from './errorDisplay'

export const ErrorScreen = () => {
  const navigate = useNavigate()
  const { history } = useRouter()
  const error = useRouterState({ select: (state) => state.location.state.error })
  const { title, icon: Icon, action } = DISPLAYS[errorKindOf(error)]
  const chip = errorChip(error)

  const run = () => {
    if (action === 'login') void navigate({ to: ROUTES.login, replace: true })
    else if (action === 'retry') history.push(error?.from ?? ROUTES.home)
    else void navigate({ to: ROUTES.home })
  }

  return (
    <ErrorLayout>
      <Panel tone="elevated" padding="xl" className="flex w-full max-w-[560px] flex-col items-center gap-4 text-center">
        <span className="inline-flex size-[72px] items-center justify-center rounded-pill bg-status-critical-bg">
          <Icon aria-hidden="true" className="size-8 text-status-critical-fg" />
        </span>
        <h1 className="text-title text-ink-primary">{title}</h1>
        <p className="text-body text-ink-secondary">{error?.message ?? 'Cette page n’existe pas ou a été déplacée.'}</p>
        {chip && <span className="rounded-pill bg-bg-lilac px-3 py-1 text-caption text-ink-secondary">{chip}</span>}
        <Button className="mt-2 w-full" onClick={run}>
          {ACTION_LABELS[action]}
        </Button>
      </Panel>
    </ErrorLayout>
  )
}
