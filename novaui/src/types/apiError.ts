export type ErrorType =
  | 'network'
  | 'notFound'
  | 'validation'
  | 'forbidden'
  | 'server'
  | 'business'
  | 'unauthorized'

export class ApiError extends Error {
  readonly type: ErrorType
  readonly status: number
  readonly code?: string

  constructor(type: ErrorType, status: number, message: string, code?: string) {
    super(message)
    this.name = 'ApiError'
    this.type = type
    this.status = status
    this.code = code
  }
}
