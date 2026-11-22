import { CalendarDays, LayoutGrid, Truck } from 'lucide-react'
import { moduleIconFor } from '../moduleIcon'

describe('moduleIconFor', () => {
  it('shouldMapKebabCaseNames_toLucideIcons', () => {
    expect(moduleIconFor('truck')).toBe(Truck)
    expect(moduleIconFor('calendar-days')).toBe(CalendarDays)
  })

  it('shouldFallBackToLayoutGrid_whenNameIsUnknown', () => {
    expect(moduleIconFor('rocket')).toBe(LayoutGrid)
  })
})
