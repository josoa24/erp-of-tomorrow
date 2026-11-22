import { useModuleAdaptation } from '../../eclipse/hooks/useModuleAdaptation'
import type { AdaptationView } from '../types/adaptationView'

export const useTeamAdaptation = (): AdaptationView => {
  const { active, preview } = useModuleAdaptation('equipe')
  return { visible: active || preview !== null, highlighted: preview !== null }
}
