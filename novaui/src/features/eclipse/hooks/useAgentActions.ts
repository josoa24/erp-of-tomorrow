import { useCallback, useRef, type Dispatch, type RefObject } from 'react'
import { cancelAgentAction, confirmAgentAction } from '../api/agentApi'
import type { AgentChatAction } from '../services/agentChatReducer'
import type { AgentChatState } from '../types/agent'

type Verb = 'confirm' | 'cancel'

const PROGRESS = { confirm: 'confirming', cancel: 'cancelling' } as const
const OUTCOME = { confirm: 'done', cancel: 'cancelled' } as const

export const useAgentActions = (
  stateRef: RefObject<AgentChatState>,
  dispatch: Dispatch<AgentChatAction>,
  handleError: (error: unknown) => void,
) => {
  const deciding = useRef(new Set<string>())

  const decide = useCallback(
    async (actionId: string, verb: Verb) => {
      const { conversationId, actions } = stateRef.current
      if (!conversationId || actions[actionId]?.status !== 'pending' || deciding.current.has(actionId)) return
      deciding.current.add(actionId)
      dispatch({ type: 'actionChanged', actionId, status: PROGRESS[verb] })
      try {
        const call = verb === 'confirm' ? confirmAgentAction : cancelAgentAction
        const result = await call(conversationId, actionId)
        dispatch({ type: 'actionChanged', actionId, status: OUTCOME[verb], detail: result?.message })
      } catch (error) {
        dispatch({ type: 'actionChanged', actionId, status: 'failed' })
        handleError(error)
      } finally {
        deciding.current.delete(actionId)
      }
    },
    [stateRef, dispatch, handleError],
  )

  const confirm = useCallback((actionId: string) => decide(actionId, 'confirm'), [decide])
  const cancel = useCallback((actionId: string) => decide(actionId, 'cancel'), [decide])

  return { confirm, cancel }
}
