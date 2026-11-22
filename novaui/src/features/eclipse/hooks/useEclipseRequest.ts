import { useCallback, useState } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { toApiError } from '../../../utils/toApiError'
import { proposeAdaptation } from '../api/proposeApi'
import { answerOf, toPreview } from '../services/proposalOutcome'
import { unavailabilityOf } from '../services/eclipseAvailability'
import { useCreateModuleRequest } from '../../modules/hooks/useCreateModuleRequest'
import { setCreateModuleDraft } from '../../modules/services/createModuleDraft'
import { useAgentAnswer } from './useAgentAnswer'
import { useEclipseAdaptation } from './eclipse-adaptation-context'

export const useEclipseRequest = (moduleKey: string) => {
  const { startPreview } = useEclipseAdaptation()
  const handleError = useApiError()
  const [pending, setPending] = useState(false)
  const [answer, setAnswer] = useState<string | null>(null)
  const [unavailable, setUnavailable] = useState(false)
  const ask = useAgentAnswer(setAnswer)
  const { open: openCreateModule } = useCreateModuleRequest()

  const submit = useCallback(
    async (text: string) => {
      setPending(true)
      setAnswer(null)
      try {
        const response = await proposeAdaptation(moduleKey, text)
        const preview = toPreview(response)
        if (preview) startPreview(preview)
        else if (response.outcome === 'CREATE_MODULE') {
          setCreateModuleDraft(text)
          openCreateModule()
        } else if (response.outcome === 'QUESTION') await ask(text)
        else setAnswer(answerOf(response))
      } catch (error) {
        if (unavailabilityOf(error)) setUnavailable(true)
        else handleError(toApiError(error))
      } finally {
        setPending(false)
      }
    },
    [moduleKey, startPreview, handleError, ask, openCreateModule],
  )

  return { submit, pending, answer, unavailable, dismiss: () => setAnswer(null) }
}
