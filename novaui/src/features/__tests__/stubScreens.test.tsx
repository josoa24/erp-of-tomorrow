import { render, screen } from '@testing-library/react'
import type { ComponentType } from 'react'
import { TeamScreen } from '../team/screens/TeamScreen'

describe('feature stub screens', () => {
  it.each([
    ['Équipe', TeamScreen],
  ] as [string, ComponentType][])('shouldRenderModulePageAndPendingState_for_%s', (title, Screen) => {
    render(<Screen />)

    expect(screen.getByRole('heading', { level: 1, name: title })).toBeInTheDocument()
    expect(screen.getByRole('heading', { name: 'Écran en cours d’intégration' })).toBeInTheDocument()
  })
})
