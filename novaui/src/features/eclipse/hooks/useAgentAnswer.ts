import { useCallback, useRef } from 'react'
import { streamAgentChat } from '../api/agentApi'
import { nextAnswer } from '../services/agentAnswer'

export const useAgentAnswer = (onAnswer: (text: string) => void) => {
  const conversationId = useRef<string | null>(null)

  return useCallback(
    async (message: string) => {
      let text = ''
      await streamAgentChat({ message, conversationId: conversationId.current }, (event) => {
        if (event.type === 'start') conversationId.current = event.conversationId
        text = nextAnswer(text, event)
        onAnswer(text)
      })
    },
    [onAnswer],
  )
}
