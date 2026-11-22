export const isAbortError = (error: unknown): boolean =>
  (error instanceof DOMException || error instanceof Error) && error.name === 'AbortError'
