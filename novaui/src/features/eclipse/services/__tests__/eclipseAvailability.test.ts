import { ApiError } from '../../../../types/apiError'
import { streamErrorToApiError, unavailabilityOf } from '../eclipseAvailability'

describe('eclipseAvailability', () => {
  it('shouldReportRateLimit_whenServerAnswers429', () => {
    expect(unavailabilityOf(new ApiError('validation', 429, 'Trop de requêtes'))).toBe('rateLimited')
  })

  it('shouldReportUnavailable_whenServerAnswers503', () => {
    expect(unavailabilityOf(new ApiError('network', 503, 'Gemini indisponible'))).toBe('unavailable')
  })

  it('shouldLeaveOtherErrorsToTheErrorPage', () => {
    expect(unavailabilityOf(new ApiError('forbidden', 403, 'Interdit'))).toBeNull()
    expect(unavailabilityOf(new ApiError('server', 500, 'Erreur'))).toBeNull()
    expect(unavailabilityOf(new Error('inattendu'))).toBeNull()
  })

  it('shouldTurnGeminiStreamErrors_intoUnavailability', () => {
    expect(unavailabilityOf(streamErrorToApiError({ code: 'GEMINI_RATE_LIMIT', message: 'Quota' }))).toBe('rateLimited')
    expect(unavailabilityOf(streamErrorToApiError({ code: 'GEMINI_TIMEOUT', message: 'Trop long' }))).toBe('unavailable')
  })

  it('shouldTurnInternalStreamErrors_intoServerError', () => {
    const error = streamErrorToApiError({ code: 'INTERNAL_ERROR', message: 'Erreur interne.' })

    expect(error).toMatchObject({ type: 'server', status: 500, code: 'INTERNAL_ERROR', message: 'Erreur interne.' })
  })
})
