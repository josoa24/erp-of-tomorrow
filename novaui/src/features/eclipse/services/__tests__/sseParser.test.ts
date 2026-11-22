import { createSseParser, type SseMessage } from '../sseParser'

const collect = () => {
  const messages: SseMessage[] = []
  return { messages, parser: createSseParser((message) => messages.push(message)) }
}

describe('createSseParser', () => {
  it('shouldEmitEvent_whenBlockIsComplete', () => {
    const { messages, parser } = collect()

    parser.push('event: token\ndata: {"text":"Bon"}\n\n')

    expect(messages).toEqual([{ event: 'token', data: '{"text":"Bon"}' }])
  })

  it('shouldWaitForTheEnd_whenEventIsSplitAcrossChunks', () => {
    const { messages, parser } = collect()

    parser.push('event: to')
    parser.push('ken\ndata: {"te')
    expect(messages).toEqual([])
    parser.push('xt":"jour"}\n\nevent: done\ndata: {}\n\n')

    expect(messages.map((m) => m.event)).toEqual(['token', 'done'])
    expect(messages[0].data).toBe('{"text":"jour"}')
  })

  it('shouldJoinMultipleDataLines_andDefaultToMessageEvent', () => {
    const { messages, parser } = collect()

    parser.push('data: ligne 1\ndata: ligne 2\n\n')

    expect(messages).toEqual([{ event: 'message', data: 'ligne 1\nligne 2' }])
  })

  it('shouldIgnoreCommentsAndEmptyBlocks_andAcceptCrlf', () => {
    const { messages, parser } = collect()

    parser.push(': ping\r\n\r\nevent: start\r\ndata: {}\r\n\r\n')

    expect(messages).toEqual([{ event: 'start', data: '{}' }])
  })

  it('shouldEmitLastEvent_onFlush_whenStreamEndsWithoutBlankLine', () => {
    const { messages, parser } = collect()

    parser.push('event: done\ndata: {"response":"ok"}')
    parser.flush()

    expect(messages).toEqual([{ event: 'done', data: '{"response":"ok"}' }])
  })
})
