import { ModulePage } from '../../../components/module/ModulePage'
import { Panel } from '../../../components/ui/panel'
import { Skeleton } from '../../../components/ui/skeleton'

const ROWS = [0, 1, 2, 3]

export const ModuleSkeleton = ({ title }: { title: string }) => (
  <ModulePage title={title}>
    <Panel aria-busy="true" aria-label={`Chargement de ${title}`} className="flex flex-col gap-3">
      {ROWS.map((row) => (
        <Skeleton key={row} className="h-12 w-full" />
      ))}
    </Panel>
  </ModulePage>
)
