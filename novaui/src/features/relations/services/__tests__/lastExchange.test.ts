import { businessDaysBetween, toBusinessIsoDate } from '../businessDay'
import { formatLastExchange, NO_EXCHANGE_LABEL } from '../lastExchange'

const NOW = new Date('2026-11-22T08:00:00Z')

describe('businessDay', () => {
  it('shouldUseAntananarivoCalendarDay_whenUtcIsStillYesterday', () => {
    expect(toBusinessIsoDate(new Date('2026-11-21T22:30:00Z'))).toBe('2026-11-22')
  })

  it('shouldCountCalendarDaysBetweenInstants', () => {
    expect(businessDaysBetween(new Date('2026-11-19T22:00:00Z'), NOW)).toBe(2)
  })
})

describe('formatLastExchange', () => {
  it.each([
    ['2026-11-22T06:00:00Z', 'aujourd’hui'],
    ['2026-11-21T09:00:00Z', 'hier'],
    ['2026-11-20T09:00:00Z', 'il y a 2 jours'],
    ['2026-10-19T09:00:00Z', 'il y a 34 jours'],
    ['2026-08-01T09:00:00Z', 'il y a 3 mois'],
    ['2024-10-01T09:00:00Z', 'il y a 2 ans'],
  ])('shouldDescribe_%s', (instant, expected) => {
    expect(formatLastExchange(instant, NOW)).toBe(expected)
  })

  it('shouldSayNoExchange_whenContactHasNone', () => {
    expect(formatLastExchange(null, NOW)).toBe(NO_EXCHANGE_LABEL)
  })

  it('shouldSayToday_whenServerClockIsSlightlyAhead', () => {
    expect(formatLastExchange('2026-11-23T08:00:00Z', NOW)).toBe('aujourd’hui')
  })
})
