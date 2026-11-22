import { render, screen } from '@testing-library/react'
import type { Issue } from '../../../../types/dashboard'
import { IssuesCard } from '../IssuesCard'

const issues: Issue[] = [
  { id: 'a', label: '2 produits en risque de rupture', severity: 'critical' },
  { id: 'b', label: 'Ventes en baisse', severity: 'warning' },
]

describe('IssuesCard', () => {
  it('shouldListIssuesWithCountInTitle', () => {
    render(<IssuesCard issues={issues} loading={false} />)

    expect(screen.getByRole('heading', { name: '2 points demandent votre attention' })).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })

  it('shouldUseSingular_whenOneIssue', () => {
    render(<IssuesCard issues={[issues[0]]} loading={false} />)

    expect(screen.getByRole('heading', { name: '1 point demande votre attention' })).toBeInTheDocument()
  })

  it('shouldReassure_whenNoIssue', () => {
    render(<IssuesCard issues={[]} loading={false} />)

    expect(screen.getByRole('heading', { name: 'Tout est sous contrôle' })).toBeInTheDocument()
    expect(screen.queryByRole('list')).not.toBeInTheDocument()
  })

  it('shouldShowPendingTitle_whileLoading', () => {
    render(<IssuesCard issues={[]} loading />)

    expect(screen.getByRole('heading', { name: 'Analyse en cours…' })).toBeInTheDocument()
  })
})
