import { useLocation, useMatches } from '@tanstack/react-router'
import { useEffect, useRef, useState } from 'react'
import { ECLIPSE_PLACEHOLDERS, ECLIPSE_UNAVAILABLE_LABEL } from '../../constants/eclipsePlaceholders'
import { EclipseBar } from '../eclipse/EclipseBar'
import { useEclipseDock } from './eclipse-dock-context'

export const EclipseDock = ({ mobile = false }: { mobile?: boolean }) => {
  const staticData = useMatches({ select: (matches) => matches[matches.length - 1]?.staticData })
  const pathname = useLocation({ select: (location) => location.pathname })
  const { hidden, mobileOpen, closeDock, focusSignal } = useEclipseDock()
  const [value, setValue] = useState('')
  const inputRef = useRef<HTMLTextAreaElement>(null)

  useEffect(() => {
    if (focusSignal > 0) inputRef.current?.focus()
  }, [focusSignal])

  useEffect(() => {
    closeDock()
  }, [pathname, closeDock])

  if (hidden || staticData?.hideEclipseDock || (mobile && !mobileOpen)) return null

  return (
    <div className="fixed inset-x-4 bottom-[116px] z-20 mx-auto max-w-[760px] md:bottom-7">
      <EclipseBar
        variant="light"
        placeholder={staticData?.eclipsePlaceholder ?? ECLIPSE_PLACEHOLDERS.default}
        value={value}
        onChange={setValue}
        onSubmit={() => setValue('')}
        status="unavailable"
        unavailableLabel={ECLIPSE_UNAVAILABLE_LABEL}
        inputRef={inputRef}
      />
    </div>
  )
}
