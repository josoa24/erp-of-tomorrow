import { Outlet } from '@tanstack/react-router'
import { CreateModuleFlow } from '../../features/modules/screens/CreateModuleFlow'
import { EclipseAdaptationProvider } from '../../features/eclipse/components/EclipseAdaptationProvider'
import { useBreakpoint } from '../../hooks/useBreakpoint'
import { readSession } from '../../services/sessionStore'
import { sessionRoles } from '../../utils/roleAccess'
import { NovaBackground } from '../brand/NovaBackground'
import { ToastProvider } from '../ui/toast-provider'
import { AppHeader } from './AppHeader'
import { EclipseDockProvider } from './EclipseDockProvider'
import { ShellBottomArea } from './ShellBottomArea'

export const MAIN_CONTENT_ID = 'contenu'

export const AppShell = () => {
  const breakpoint = useBreakpoint()
  const mobile = breakpoint === 'mobile'
  const session = readSession()
  const roles = session ? sessionRoles(session) : undefined

  return (
    <ToastProvider>
      <EclipseDockProvider>
        <EclipseAdaptationProvider>
          <NovaBackground />
          <a
            href={`#${MAIN_CONTENT_ID}`}
            className="sr-only z-50 rounded-pill bg-surface-night px-4 py-2 text-label text-ink-inverse focus:not-sr-only focus:fixed focus:top-3 focus:left-3"
          >
            Aller au contenu
          </a>
          <AppHeader breakpoint={breakpoint} />
          <main id={MAIN_CONTENT_ID} tabIndex={-1} className="px-4 pt-6 pb-40 outline-none md:px-8 md:pt-7">
            <Outlet />
          </main>
          <ShellBottomArea mobile={mobile} roles={roles} />
          <CreateModuleFlow />
        </EclipseAdaptationProvider>
      </EclipseDockProvider>
    </ToastProvider>
  )
}
