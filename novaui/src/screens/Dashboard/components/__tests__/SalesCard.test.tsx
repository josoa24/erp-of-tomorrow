import { render, screen } from '@testing-library/react'
import { SalesCard } from '../SalesCard'

const normalizedText = () => document.body.textContent?.replace(/\s/g, ' ') ?? ''

describe('SalesCard', () => {
  it('shouldShowRevenueTrendAndChart', () => {
    render(<SalesCard data={{ revenue: 2_840_000, salesCount: 9, trend: 12, daily: [1, 3] }} loading={false} />)

    expect(normalizedText()).toContain('2,84 M Ar')
    expect(normalizedText()).toContain('+12 %')
    expect(screen.getByRole('img', { name: /chiffre d'affaires/ })).toBeInTheDocument()
  })

  it('shouldShowSalesCount_whenNoTrendAvailable', () => {
    render(<SalesCard data={{ revenue: 0, salesCount: 0, trend: null, daily: [] }} loading={false} />)

    expect(screen.getByText('0 vente')).toBeInTheDocument()
  })

  it('shouldShowRestrictedAccess_whenRoleCannotSeeSales', () => {
    render(<SalesCard data={null} loading={false} />)

    expect(screen.getByText('Accès réservé')).toBeInTheDocument()
  })

  it('shouldShowPlaceholder_whileLoading', () => {
    render(<SalesCard data={null} loading />)

    expect(screen.getByText('…')).toBeInTheDocument()
  })
})
