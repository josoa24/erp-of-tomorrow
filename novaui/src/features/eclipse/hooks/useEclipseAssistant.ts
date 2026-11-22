import { useCallback, useEffect, useState } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { toApiError } from '../../../utils/toApiError'
import { proposeAdaptation } from '../api/proposeApi'
import { ECLIPSE_RETRY_DELAY_MS, unavailabilityOf, type EclipseUnavailability } from '../services/eclipseAvailability'
import { answerOf, toPreview } from '../services/proposalOutcome'
import { useEclipseAdaptation } from './eclipse-adaptation-context'
import { useAgentChat } from './useAgentChat'

export const useEclipseAssistant = (moduleKey: string) => {
  const { startPreview } = useEclipseAdaptation()
  const handleError = useApiError()
  const [unavailability, setUnavailability] = useState<EclipseUnavailability | null>(null)
  const chat = useAgentChat(setUnavailability)
  const [open, setOpen] = useState(false)
  const [notice, setNotice] = useState<string | null>(null)
  const [proposing, setProposing] = useState(false)

  useEffect(() => {
    if (!unavailability) return
    const timer = window.setTimeout(() => setUnavailability(null), ECLIPSE_RETRY_DELAY_MS)
    return () => window.clearTimeout(timer)
  }, [unavailability])

  const submit = useCallback(
    async (text: string) => {
      setOpen(true)
      setNotice(null)
      setProposing(true)
      try {
        const response = await proposeAdaptation(moduleKey, text)
        const preview = toPreview(response)
        if (preview) {
          setOpen(false)
          startPreview(preview)
        } else if (response.outcome === 'QUESTION') {
          setProposing(false)
          await chat.send(text)
        } else {
          setNotice(answerOf(response))
        }
      } catch (error) {
        const reason = unavailabilityOf(error)
        if (reason) setUnavailability(reason)
        else handleError(toApiError(error))
      } finally {
        setProposing(false)
      }
    },
    [moduleKey, startPreview, handleError, chat],
  )

  const status = unavailability ? 'unavailable' : proposing || chat.busy ? 'streaming' : 'idle'
  const visible = open && (chat.messages.length > 0 || notice !== null || unavailability !== null)

  return { chat, submit, status, unavailability, notice, visible, close: () => setOpen(false) } as const
}
