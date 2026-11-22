import type { SseMessage } from '../../services/sseParser'
import { readSseStream } from '../sseStream'

const encoder = new TextEncoder()

const streamOf = (chunks: string[], failure?: unknown) =>
  new ReadableStream<Uint8Array>({
    start(controller) {
      chunks.forEach((chunk) => controller.enqueue(encoder.encode(chunk)))
      if (failure) controller.error(failure)
      else controller.close()
    },
  })

describe('readSseStream', () => {
  it('shouldEmitEveryEvent_acrossChunks', async () => {
    const messages: SseMessage[] = []
    const response = new Response(streamOf(['event: start\ndata: {"conversation_id":"c1"}\n\nevent: to', 'ken\ndata: {"text":"é"}\n\n']))

    await readSseStream(response, (message) => messages.push(message))

    expect(messages.map((m) => m.event)).toEqual(['start', 'token'])
    expect(messages[1].data).toBe('{"text":"é"}')
  })

  it('shouldReportNetworkError_whenStreamBreaksMidway', async () => {
    const response = new Response(streamOf(['event: token\ndata: {}\n\n'], new TypeError('network')))

    await expect(readSseStream(response, vi.fn())).rejects.toMatchObject({ type: 'network' })
  })

  it('shouldKeepAbort_whenCallerCancelled', async () => {
    const response = new Response(streamOf([], new DOMException('Aborted', 'AbortError')))

    await expect(readSseStream(response, vi.fn())).rejects.toMatchObject({ name: 'AbortError' })
  })

  it('shouldFailClearly_whenBodyIsMissing', async () => {
    await expect(readSseStream(new Response(null, { status: 200 }), vi.fn())).rejects.toMatchObject({ type: 'server' })
  })
})
