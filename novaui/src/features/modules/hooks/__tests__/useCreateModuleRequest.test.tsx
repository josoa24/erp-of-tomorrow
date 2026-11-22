import { act, screen } from '@testing-library/react'
import { renderWithRouter } from '../../../../test/renderWithRouter'
import { useCreateModuleRequest } from '../useCreateModuleRequest'

let request: ReturnType<typeof useCreateModuleRequest>

const Probe = () => {
  request = useCreateModuleRequest()
  return <p>{request.requested ? 'demandé' : 'fermé'}</p>
}

describe('useCreateModuleRequest', () => {
  it('shouldSetCreateParam_onTheCurrentRoute', async () => {
    const { router } = await renderWithRouter(Probe, '/stock')
    expect(await screen.findByText('fermé')).toBeInTheDocument()

    await act(async () => request.open())

    expect(router.state.location.pathname).toBe('/stock')
    expect(router.state.location.search).toEqual({ creer: 'module' })
    expect(screen.getByText('demandé')).toBeInTheDocument()
  })

  it('shouldRemoveCreateParam_whenClosed', async () => {
    const { router } = await renderWithRouter(Probe, '/ventes?creer=module')
    expect(await screen.findByText('demandé')).toBeInTheDocument()

    await act(async () => request.close())

    expect(router.state.location.search).toEqual({})
  })
})
