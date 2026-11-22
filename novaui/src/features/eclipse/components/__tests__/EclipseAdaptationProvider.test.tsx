import { act, screen } from '@testing-library/react'
import { renderWithRouter } from '../../../../test/renderWithRouter'
import { ADAPTATION_CATALOG } from '../../constants/adaptations'
import { useEclipseAdaptation, type EclipseAdaptationContextValue } from '../../hooks/eclipse-adaptation-context'
import { EclipseAdaptationProvider } from '../EclipseAdaptationProvider'

let adaptation: EclipseAdaptationContextValue

const Probe = () => {
  adaptation = useEclipseAdaptation()
  return <p>{adaptation.preview ? `${adaptation.preview.request} | ${adaptation.preview.summary}` : 'aucun aperçu'}</p>
}

const Shell = () => (
  <EclipseAdaptationProvider>
    <Probe />
  </EclipseAdaptationProvider>
)

const LOTS = ADAPTATION_CATALOG['stock.lots-peremption']

describe('EclipseAdaptationProvider', () => {
  it('shouldStartThePreviewWithCatalogTexts_whenTheUrlCarriesTheAdaptation', async () => {
    await renderWithRouter(Shell, '/stock?eclipse=stock.lots-peremption')

    expect(await screen.findByText(`${LOTS.request} | ${LOTS.summary}`)).toBeInTheDocument()
  })

  it('shouldIgnoreAnUnknownAdaptationInTheUrl', async () => {
    await renderWithRouter(Shell, '/stock?eclipse=stock.inconnue')

    expect(await screen.findByText('aucun aperçu')).toBeInTheDocument()
  })

  it('shouldWriteTheAdaptationInTheUrl_whenAPreviewStarts', async () => {
    const { router } = await renderWithRouter(Shell, '/stock?creer=module')
    await screen.findByText('aucun aperçu')

    await act(async () => adaptation.startPreview({ ...LOTS, request: 'Suivre les lots de riz' }))

    expect(router.state.location.pathname).toBe('/stock')
    expect(router.state.location.search).toEqual({ creer: 'module', eclipse: 'stock.lots-peremption' })
    expect(screen.getByText(`Suivre les lots de riz | ${LOTS.summary}`)).toBeInTheDocument()
  })

  it('shouldRemoveTheAdaptationFromTheUrl_whenThePreviewIsCleared', async () => {
    const { router } = await renderWithRouter(Shell, '/stock?eclipse=stock.lots-peremption')
    await screen.findByText(`${LOTS.request} | ${LOTS.summary}`)

    await act(async () => adaptation.clearPreview('stock'))

    expect(router.state.location.search).toEqual({})
    expect(screen.getByText('aucun aperçu')).toBeInTheDocument()
  })

  it('shouldKeepTheUrl_whenClearingAnotherModule', async () => {
    const { router } = await renderWithRouter(Shell, '/stock?eclipse=stock.lots-peremption')
    await screen.findByText(`${LOTS.request} | ${LOTS.summary}`)

    await act(async () => adaptation.clearPreview('ventes'))

    expect(router.state.location.search).toEqual({ eclipse: 'stock.lots-peremption' })
  })
})
