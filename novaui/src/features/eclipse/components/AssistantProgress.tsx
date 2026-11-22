import { Skeleton } from '../../../components/ui/skeleton'
import { Spinner } from '../../../components/ui/spinner'

export const AssistantProgress = ({ label }: { label: string }) => (
  <div role="status" className="flex flex-col gap-2">
    <p className="flex items-center gap-2 text-body-s text-brand-violet">
      <Spinner className="size-4" />
      {label}
    </p>
    <Skeleton className="h-3 w-4/5" />
    <Skeleton className="h-3 w-3/5" />
  </div>
)
