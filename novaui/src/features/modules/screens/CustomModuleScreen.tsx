import { useParams } from '@tanstack/react-router'
import { ModuleSkeleton } from '../components/ModuleSkeleton'
import { useModuleDefinitions } from '../hooks/useModuleDefinitions'
import { useModuleDefinition } from '../hooks/useModuleScreenData'
import { ModuleBody } from './ModuleBody'

export const CustomModuleScreen = () => {
  const slug = useParams({ strict: false, select: (params) => params.slug ?? '' })
  const { modules } = useModuleDefinitions()
  const { data: definition } = useModuleDefinition(slug)
  const title = modules.find((module) => module.slug === slug)?.name ?? slug

  if (!definition) return <ModuleSkeleton title={title} />
  return <ModuleBody key={slug} definition={definition} />
}
