export interface SseMessage {
  event: string
  data: string
}

const parseBlock = (block: string): SseMessage | null => {
  let event = 'message'
  const data: string[] = []
  for (const line of block.split('\n')) {
    if (line === '' || line.startsWith(':')) continue
    const separator = line.indexOf(':')
    const field = separator < 0 ? line : line.slice(0, separator)
    const raw = separator < 0 ? '' : line.slice(separator + 1)
    const value = raw.startsWith(' ') ? raw.slice(1) : raw
    if (field === 'event') event = value
    else if (field === 'data') data.push(value)
  }
  return data.length > 0 ? { event, data: data.join('\n') } : null
}

export const createSseParser = (onMessage: (message: SseMessage) => void) => {
  let buffer = ''

  const drain = () => {
    let end = buffer.indexOf('\n\n')
    while (end >= 0) {
      const message = parseBlock(buffer.slice(0, end))
      if (message) onMessage(message)
      buffer = buffer.slice(end + 2)
      end = buffer.indexOf('\n\n')
    }
  }

  return {
    push: (chunk: string) => {
      buffer = (buffer + chunk).replace(/\r\n/g, '\n')
      drain()
    },
    flush: () => {
      const message = parseBlock(buffer)
      if (message) onMessage(message)
      buffer = ''
    },
  }
}
