import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { renderHook, waitFor } from '@testing-library/react'
import type { ReactNode } from 'react'
import { ApiError } from '../../../../types/apiError'
import { getModuleDefinitions } from '../../api/moduleDefinitionsApi'
import { useModuleDefinitions } from '../useModuleDefinitions'

vi.mock('../../api/moduleDefinitionsApi', () => ({ getModuleDefinitions: vi.fn() }))

const wrapper = ({ children }: { children: ReactNode }) => (
  <QueryClientProvider client={new QueryClient()}>{children}</QueryClientProvider>
)

const LIVRAISONS = { id: 1, slug: 'livraisons', name: 'Livraisons', icon: 'truck', createdAt: '2026-11-20T08:00:00Z' }

describe('useModuleDefinitions', () => {
  it('shouldReturnModules_whenTheServiceAnswers', async () => {
    vi.mocked(getModuleDefinitions).mockResolvedValue([LIVRAISONS])

    const { result } = renderHook(() => useModuleDefinitions(), { wrapper })

    await waitFor(() => expect(result.current.modules).toEqual([LIVRAISONS]))
    expect(result.current.unavailable).toBe(false)
  })

  it('shouldStayEmptyWithoutErrorPage_whenTheServiceIsMissing', async () => {
    vi.mocked(getModuleDefinitions).mockRejectedValue(new ApiError('network', 503, 'Indisponible'))

    const { result } = renderHook(() => useModuleDefinitions(), { wrapper })

    await waitFor(() => expect(result.current.unavailable).toBe(true))
    expect(result.current.modules).toEqual([])
  })
})
