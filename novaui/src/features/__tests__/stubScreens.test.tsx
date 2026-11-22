import { render, screen } from '@testing-library/react'
import type { ComponentType } from 'react'
import { ProfileScreen } from '../profile/screens/ProfileScreen'
import { SalesScreen } from '../sales/screens/SalesScreen'

describe('feature stub screens', () => {
  it.each([
    ['Ventes', SalesScreen],
<<<<<<< Updated upstream
    ['Équipe', TeamScreen],
    ['Mon profil', ProfileScreen],
=======
>>>>>>> Stashed changes
  ] as [string, ComponentType][])('shouldRenderModulePageAndPendingState_for_%s', (title, Screen) => {
    render(<Screen />)

    expect(screen.getByRole('heading', { level: 1, name: title })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Écran en cours d’intégration' })).toBeInTheDocument()
  })
})
