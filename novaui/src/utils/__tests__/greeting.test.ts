import { getGreeting } from '../greeting'

describe('getGreeting', () => {
  it('shouldSayBonjour_duringTheDay', () => {
    expect(getGreeting(9)).toBe('Bonjour.')
  })

  it('shouldSayBonsoir_inTheEvening', () => {
    expect(getGreeting(20)).toBe('Bonsoir.')
  })

  it('shouldSayBonsoir_atNight', () => {
    expect(getGreeting(2)).toBe('Bonsoir.')
  })
})
