import { useModuleAdaptation } from '../../features/eclipse/hooks/useModuleAdaptation'
import { useRouteAdaptationModule } from '../../features/eclipse/hooks/useRouteAdaptationModule'
import type { AdaptationModuleKey } from '../../features/eclipse/types/adaptation'
import type { Role } from '../../types/auth'
import { AdaptationBar } from '../module/AdaptationBar'
import { BottomBar } from './BottomBar'
import { EclipseDock } from './EclipseDock'

interface ShellBottomAreaProps {
  mobile: boolean
  roles?: readonly Role[]
}

const DockAndNavigation = ({ mobile, roles }: ShellBottomAreaProps) => (
  <>
    <EclipseDock mobile={mobile} />
    {mobile && roles && <BottomBar roles={roles} />}
  </>
)

const AdaptableBottomArea = ({ moduleKey, ...props }: ShellBottomAreaProps & { moduleKey: AdaptationModuleKey }) => {
  const { preview, canApply, isApplying, apply, cancel } = useModuleAdaptation(moduleKey)

  if (!preview) return <DockAndNavigation {...props} />

  return (
    <AdaptationBar
      request={preview.request}
      summary={preview.summary}
      canApply={canApply}
      applying={isApplying}
      onApply={apply}
      onCancel={cancel}
    />
  )
}

export const ShellBottomArea = (props: ShellBottomAreaProps) => {
  const moduleKey = useRouteAdaptationModule()
  return moduleKey ? <AdaptableBottomArea moduleKey={moduleKey} {...props} /> : <DockAndNavigation {...props} />
}
