import { emptyInteractionForm, toInteractionRequest, validateInteractionForm } from '../interactionForm'
import { formatTimelineDate } from '../timelineFormat'

const TODAY = '2026-11-22'

describe('interactionForm', () => {
  it('shouldStartWithACallToday', () => {
    expect(emptyInteractionForm(TODAY)).toEqual({ type: 'APPEL', summary: '', date: TODAY })
  })

  it('shouldRequireSummary', () => {
    expect(validateInteractionForm(emptyInteractionForm(TODAY), TODAY)).toHaveProperty('summary')
  })

  it('shouldRejectFutureDate', () => {
    const values = { type: 'NOTE' as const, summary: 'Rappeler', date: '2026-11-23' }

    expect(validateInteractionForm(values, TODAY)).toEqual({ date: expect.any(String) })
  })

  it('shouldLetServerTimestamp_whenExchangeHappenedToday', () => {
    const request = toInteractionRequest({ type: 'APPEL', summary: ' Commande de riz ', date: TODAY }, TODAY)

    expect(request).toEqual({ type: 'APPEL', summary: 'Commande de riz' })
  })

  it('shouldSendMiddayInAntananarivo_whenExchangeHappenedEarlier', () => {
    const request = toInteractionRequest({ type: 'VISITE', summary: 'Visite du dépôt', date: '2026-11-18' }, TODAY)

    expect(request.occurredAt).toBe('2026-11-18T09:00:00.000Z')
  })

  it('shouldFormatTimelineDateInFrench', () => {
    expect(formatTimelineDate('2026-11-18T09:00:00Z')).toBe('18 nov. 2026')
  })
})
