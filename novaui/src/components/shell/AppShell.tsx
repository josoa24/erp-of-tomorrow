import { Outlet } from '@tanstack/react-router'
import { useBreakpoint } from '../../hooks/useBreakpoint'
import { readSession } from '../../services/sessionStore'
import { NovaBackground } from '../brand/NovaBackground'
import { ToastProvider } from '../ui/toast-provider'
import { AppHeader } from './AppHeader'
import { BottomBar } from './BottomBar'
import { EclipseDock } from './EclipseDock'
import { EclipseDockProvider } from './EclipseDockProvider'

export const MAIN_CONTENT_ID = 'contenu'

export const AppShell = () => {
  const breakpoint = useBreakpoint()
  const mobile = breakpoint === 'mobile'
  const role = readSession()?.role

  return (
    <ToastProvider>
      <EclipseDockProvider>
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
        <EclipseDock mobile={mobile} />
        {mobile && role && <BottomBar role={role} />}
      </EclipseDockProvider>
    </ToastProvider>
  )
}
