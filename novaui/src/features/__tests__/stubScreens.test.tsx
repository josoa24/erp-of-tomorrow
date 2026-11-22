import { render, screen } from '@testing-library/react'
import type { ComponentType } from 'react'
import { ProfileScreen } from '../profile/screens/ProfileScreen'
import { RelationsScreen } from '../relations/screens/RelationsScreen'
import { SalesScreen } from '../sales/screens/SalesScreen'
import { StockScreen } from '../stock/screens/StockScreen'
import { TeamScreen } from '../team/screens/TeamScreen'

describe('feature stub screens', () => {
  it.each([
    ['Ventes', SalesScreen],
    ['Stock', StockScreen],
    ['Équipe', TeamScreen],
    ['Relations', RelationsScreen],
    ['Mon profil', ProfileScreen],
  ] as [string, ComponentType][])('shouldRenderModulePageAndPendingState_for_%s', (title, Screen) => {
    render(<Screen />)

    expect(screen.getByRole('heading', { level: 1, name: title })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Écran en cours d’intégration' })).toBeInTheDocument()
  })
})
