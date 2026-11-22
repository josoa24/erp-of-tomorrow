type Listener = () => void

const minWidthOf = (query: string): number => Number(/min-width:\s*(\d+)px/.exec(query)?.[1] ?? 0)

export const mockViewport = (initialWidth: number) => {
  let width = initialWidth
  const listeners = new Set<Listener>()
  vi.stubGlobal('matchMedia', (query: string) => ({
    get matches() {
      return width >= minWidthOf(query)
    },
    media: query,
    addEventListener: (_: string, listener: Listener) => listeners.add(listener),
    removeEventListener: (_: string, listener: Listener) => listeners.delete(listener),
  }))
  return {
    resize: (next: number) => {
      width = next
      listeners.forEach((listener) => listener())
    },
  }
}
