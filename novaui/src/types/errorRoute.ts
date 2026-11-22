import type { ErrorType } from './apiError'

export interface ErrorRouteState {
  type: ErrorType
  message: string
  from: string
  status?: number
  code?: string
}

declare module '@tanstack/history' {
  interface HistoryState {
    error?: ErrorRouteState
  }
}
