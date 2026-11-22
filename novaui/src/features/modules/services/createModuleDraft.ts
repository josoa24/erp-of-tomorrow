import { useSyncExternalStore } from 'react'

let draft = ''
const listeners = new Set<() => void>()

const subscribe = (listener: () => void): (() => void) => {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export const setCreateModuleDraft = (text: string): void => {
  draft = text
  listeners.forEach((listener) => listener())
}

export const useCreateModuleDraft = (): string => useSyncExternalStore(subscribe, () => draft)
