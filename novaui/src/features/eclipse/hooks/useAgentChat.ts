import { useCallback, useEffect, useReducer, useRef } from 'react'
import { useApiError } from '../../../hooks/useApiError'
import { ApiError } from '../../../types/apiError'
import { deleteAgentConversation, streamAgentChat } from '../api/agentApi'
import { INTERRUPTED_MESSAGE } from '../api/sseStream'
import { isAbortError } from '../services/abortError'
import { agentChatReducer, INITIAL_AGENT_CHAT } from '../services/agentChatReducer'
import { streamErrorToApiError, unavailabilityOf, type EclipseUnavailability } from '../services/eclipseAvailability'
import type { AgentStreamEvent } from '../types/agent'
import { useAgentActions } from './useAgentActions'

interface StreamEnd {
  last?: AgentStreamEvent
}

const endOf = (end: StreamEnd): ApiError | null => {
  if (end.last?.type === 'error') return streamErrorToApiError(end.last)
  return end.last?.type === 'done' ? null : new ApiError('network', 0, INTERRUPTED_MESSAGE)
}

export const useAgentChat = (onUnavailable: (reason: EclipseUnavailability) => void) => {
  const handleError = useApiError()
  const [state, dispatch] = useReducer(agentChatReducer, INITIAL_AGENT_CHAT)
  const stateRef = useRef(state)
  const controllerRef = useRef<AbortController | null>(null)
  const counter = useRef(0)

  useEffect(() => {
    stateRef.current = state
  }, [state])

  useEffect(() => () => controllerRef.current?.abort(), [])

  const fail = useCallback(
    (error: unknown) => {
      dispatch({ type: 'ended', status: 'stopped' })
      if (isAbortError(error)) return
      const reason = unavailabilityOf(error)
      if (reason) onUnavailable(reason)
      else handleError(error)
    },
    [handleError, onUnavailable],
  )

  const send = useCallback(
    async (text: string, context?: string) => {
      const shown = text.trim()
      if (!shown || controllerRef.current) return
      const controller = new AbortController()
      controllerRef.current = controller
      const id = ++counter.current
      dispatch({ type: 'sent', userId: `user-${id}`, assistantId: `assistant-${id}`, text: shown })
      const end: StreamEnd = {}
      try {
        const message = context ? `${context}\n${shown}` : shown
        await streamAgentChat({ message, conversationId: stateRef.current.conversationId }, (event) => {
          end.last = event
          dispatch({ type: 'event', event })
        }, controller.signal)
        const failure = endOf(end)
        if (failure) fail(failure)
      } catch (error) {
        fail(error)
      } finally {
        controllerRef.current = null
      }
    },
    [fail],
  )

  const stop = useCallback(() => controllerRef.current?.abort(), [])

  const reset = useCallback(() => {
    controllerRef.current?.abort()
    const { conversationId } = stateRef.current
    if (conversationId) deleteAgentConversation(conversationId).catch(() => undefined)
    dispatch({ type: 'reset' })
  }, [])

  const { confirm, cancel } = useAgentActions(stateRef, dispatch, handleError)

  return { ...state, send, stop, reset, confirm, cancel }
}
