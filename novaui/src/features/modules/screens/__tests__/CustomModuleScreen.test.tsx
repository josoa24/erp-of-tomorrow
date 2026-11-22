import { render, screen } from '@testing-library/react'
import { useModuleDefinitions } from '../../hooks/useModuleDefinitions'
import { CustomModuleScreen } from '../CustomModuleScreen'

vi.mock('@tanstack/react-router', () => ({
  useParams: ({ select }: { select: (params: { slug?: string }) => string }) => select({ slug: 'livraisons' }),
}))
vi.mock('../../hooks/useModuleDefinitions', () => ({ useModuleDefinitions: vi.fn() }))

describe('CustomModuleScreen', () => {
  it('shouldUseModuleName_asTitle', () => {
    vi.mocked(useModuleDefinitions).mockReturnValue({
      modules: [{ id: 1, slug: 'livraisons', name: 'Livraisons', icon: 'truck', createdAt: '2026-11-20T08:00:00Z' }],
      loading: false,
      unavailable: false,
    })

    render(<CustomModuleScreen />)

    expect(screen.getByRole('heading', { level: 1, name: 'Livraisons' })).toBeInTheDocument()
    expect(screen.getByText('Écran en cours d’intégration')).toBeInTheDocument()
  })

  it('shouldFallBackToSlug_whenDefinitionIsUnknown', () => {
    vi.mocked(useModuleDefinitions).mockReturnValue({ modules: [], loading: false, unavailable: true })

    render(<CustomModuleScreen />)

    expect(screen.getByRole('heading', { level: 1, name: 'livraisons' })).toBeInTheDocument()
  })
})
