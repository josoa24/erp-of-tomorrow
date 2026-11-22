import { useParams } from '@tanstack/react-router'
import { IntegrationPending } from '../../../components/module/IntegrationPending'
import { ModulePage } from '../../../components/module/ModulePage'
import { useModuleDefinitions } from '../hooks/useModuleDefinitions'

export const CustomModuleScreen = () => {
  const slug = useParams({ strict: false, select: (params) => params.slug ?? '' })
  const { modules } = useModuleDefinitions()
  const title = modules.find((module) => module.slug === slug)?.name ?? slug

  return (
    <ModulePage title={title}>
      <IntegrationPending label={title} />
    </ModulePage>
  )
}
