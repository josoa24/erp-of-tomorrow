import { ToastProvider } from '../../../../components/ui/toast-provider'
import { renderWithRouter } from '../../../../test/renderWithRouter'
import { EclipseAdaptationProvider } from '../../components/EclipseAdaptationProvider'
import type { AdaptationActivation, AdaptationModuleKey } from '../../types/adaptation'
import { useModuleAdaptation, type ModuleAdaptation } from '../useModuleAdaptation'

export const LOTS_ACTIVATION: AdaptationActivation = {
  moduleKey: 'stock',
  adaptationKey: 'stock.lots-peremption',
  activatedBy: 1,
  activatedAt: '2026-11-22T06:00:00Z',
}

export const ADMIN_SESSION = { token: 't', email: 'hery.rabe@boutique-tana.mg', role: 'ADMIN' } as const

export const renderModuleAdaptation = async (moduleKey: AdaptationModuleKey, path: string) => {
  const latest: { current?: ModuleAdaptation } = {}

  const Probe = () => {
    const adaptation = useModuleAdaptation(moduleKey)
    latest.current = adaptation
    return (
      <>
        <p>{adaptation.preview ? `aperçu : ${adaptation.preview.request}` : 'aucun aperçu'}</p>
        <p>{adaptation.active ? 'adaptation active' : 'adaptation inactive'}</p>
      </>
    )
  }

  const Shell = () => (
    <ToastProvider>
      <EclipseAdaptationProvider>
        <Probe />
      </EclipseAdaptationProvider>
    </ToastProvider>
  )

  const view = await renderWithRouter(Shell, path)
  return { ...view, latest }
}
