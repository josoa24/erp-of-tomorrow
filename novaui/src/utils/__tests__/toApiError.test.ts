import { ApiError } from '../../types/apiError'
import { toApiError } from '../toApiError'

describe('toApiError', () => {
  it('shouldReturnSameInstance_whenAlreadyApiError', () => {
    const error = new ApiError('forbidden', 403, 'Interdit')

    expect(toApiError(error)).toBe(error)
  })

  it('shouldWrapErrorMessage_whenPlainError', () => {
    const result = toApiError(new Error('boom'))

    expect(result).toMatchObject({ type: 'server', status: 0, message: 'boom' })
  })

  it('shouldUseDefaultMessage_whenUnknownValue', () => {
    expect(toApiError('oops').message).toBe('Une erreur inattendue est survenue.')
  })
})
