import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import type { ErrorRouteState } from '../../../types/errorRoute'
import { ErrorScreen } from '../ErrorScreen'

const renderAt = (state?: ErrorRouteState) =>
  render(
    <MemoryRouter initialEntries={[{ pathname: '/erreur', state }]}>
      <Routes>
        <Route path="/erreur" element={<ErrorScreen />} />
        <Route path="/connexion" element={<p>Page de connexion</p>} />
        <Route path="/" element={<p>Accueil</p>} />
      </Routes>
    </MemoryRouter>,
  )

describe('ErrorScreen', () => {
  it('shouldShowTitleForTypeAndApiMessage', () => {
    renderAt({ type: 'forbidden', message: 'Module interdit', from: '/' })

    expect(screen.getByRole('heading', { name: 'Accès refusé' })).toBeInTheDocument()
    expect(screen.getByText('Module interdit')).toBeInTheDocument()
  })

  it('shouldShowNotFound_whenNoStateIsGiven', () => {
    renderAt()

    expect(screen.getByRole('heading', { name: 'Page introuvable' })).toBeInTheDocument()
  })

  it('shouldGoBackToOrigin_whenRetrying', async () => {
    renderAt({ type: 'unauthorized', message: 'Refusé', from: '/connexion' })

    await userEvent.click(screen.getByRole('button', { name: 'Réessayer' }))

    expect(screen.getByText('Page de connexion')).toBeInTheDocument()
  })

  it('shouldGoHome_whenClickingBack', async () => {
    renderAt({ type: 'server', message: 'Panne', from: '/connexion' })

    await userEvent.click(screen.getByRole('button', { name: 'Retour' }))

    expect(screen.getByText('Accueil')).toBeInTheDocument()
  })
})
