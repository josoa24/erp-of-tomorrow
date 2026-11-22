import { useLocation, useMatches, useParams } from '@tanstack/react-router'
import { useEffect, useRef } from 'react'
import { EclipseAssistant } from '../../features/eclipse/components/EclipseAssistant'
import { useModuleDefinitions } from '../../features/modules/hooks/useModuleDefinitions'
import { dockPlaceholder } from '../../features/modules/services/moduleLabels'
import { dockPlaceholderFor, isDockHiddenFor } from './dockPlaceholder'
import { useEclipseDock } from './eclipse-dock-context'

export const EclipseDock = ({ mobile = false }: { mobile?: boolean }) => {
  const staticData = useMatches({ select: (matches) => matches[matches.length - 1]?.staticData })
  const pathname = useLocation({ select: (location) => location.pathname })
  const { hidden, mobileOpen, closeDock, focusSignal } = useEclipseDock()
  const inputRef = useRef<HTMLTextAreaElement>(null)
  const customSlug = useParams({ strict: false, select: (params) => params.slug })
  const { modules } = useModuleDefinitions()
  const customName = modules.find((module) => module.slug === customSlug)?.name

  useEffect(() => {
    if (focusSignal > 0) inputRef.current?.focus()
  }, [focusSignal])

  useEffect(() => {
    closeDock()
  }, [pathname, closeDock])

  if (hidden || isDockHiddenFor(staticData) || (mobile && !mobileOpen)) return null

  return (
    <div className="fixed inset-x-4 bottom-[116px] z-20 mx-auto max-w-[760px] md:bottom-7">
      <EclipseAssistant
        variant="light"
        placeholder={customName ? dockPlaceholder(customName) : dockPlaceholderFor(staticData)}
        moduleKey={customSlug ?? staticData?.adaptationModule ?? 'accueil'}
        placement="above"
        inputRef={inputRef}
      />
    </div>
  )
}
