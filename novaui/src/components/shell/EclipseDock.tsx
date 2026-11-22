import { useLocation, useMatches } from '@tanstack/react-router'
import { useEffect, useRef, useState } from 'react'
import { ECLIPSE_UNAVAILABLE_LABEL } from '../../constants/eclipsePlaceholders'
import { EclipseAnswer } from '../../features/eclipse/components/EclipseAnswer'
import { useEclipseRequest } from '../../features/eclipse/hooks/useEclipseRequest'
import { EclipseBar } from '../eclipse/EclipseBar'
import { dockPlaceholderFor, isDockHiddenFor } from './dockPlaceholder'
import { useEclipseDock } from './eclipse-dock-context'

export const EclipseDock = ({ mobile = false }: { mobile?: boolean }) => {
  const staticData = useMatches({ select: (matches) => matches[matches.length - 1]?.staticData })
  const pathname = useLocation({ select: (location) => location.pathname })
  const { hidden, mobileOpen, closeDock, focusSignal } = useEclipseDock()
  const [value, setValue] = useState('')
  const inputRef = useRef<HTMLTextAreaElement>(null)
  const { submit, pending, answer, unavailable, dismiss } = useEclipseRequest(staticData?.adaptationModule ?? 'accueil')

  useEffect(() => {
    if (focusSignal > 0) inputRef.current?.focus()
  }, [focusSignal])

  useEffect(() => {
    closeDock()
  }, [pathname, closeDock])

  if (hidden || isDockHiddenFor(staticData) || (mobile && !mobileOpen)) return null

  return (
    <div className="fixed inset-x-4 bottom-[116px] z-20 mx-auto max-w-[760px] md:bottom-7">
      {answer && <EclipseAnswer text={answer} onDismiss={dismiss} />}
      <EclipseBar
        variant="light"
        placeholder={dockPlaceholderFor(staticData)}
        value={value}
        onChange={setValue}
        onSubmit={(text) => {
          setValue('')
          void submit(text)
        }}
        status={unavailable ? 'unavailable' : pending ? 'streaming' : 'idle'}
        unavailableLabel={ECLIPSE_UNAVAILABLE_LABEL}
        inputRef={inputRef}
      />
    </div>
  )
}
