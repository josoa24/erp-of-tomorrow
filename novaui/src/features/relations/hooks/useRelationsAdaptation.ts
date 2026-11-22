import { useModuleAdaptation } from '../../eclipse/hooks/useModuleAdaptation'
import type { AdaptationView } from '../types/adaptationView'

export const useRelationsAdaptation = (): AdaptationView => {
  const { active, preview } = useModuleAdaptation('relations')
  return { visible: active || preview !== null, highlighted: preview !== null }
}
