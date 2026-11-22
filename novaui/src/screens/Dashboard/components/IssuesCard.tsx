import { ChevronRight, CircleCheck, TriangleAlert } from 'lucide-react'
import { cn } from '@/lib/utils'
import { GlassCard } from '../../../components/common/GlassCard'
import type { Issue } from '../../../types/dashboard'

interface IssuesCardProps {
  issues: Issue[]
  loading: boolean
}

const BULLETS: Record<Issue['severity'], string> = {
  critical: 'bg-nova-danger shadow-[0_0_8px_rgba(248,86,109,0.8)]',
  warning: 'bg-[#fb7185] opacity-85',
}

const issuesTitle = (count: number): string =>
  count > 1 ? `${count} points demandent votre attention` : '1 point demande votre attention'

export const IssuesCard = ({ issues, loading }: IssuesCardProps) => {
  const hasIssues = issues.length > 0
  const Icon = hasIssues || loading ? TriangleAlert : CircleCheck
  const title = loading ? 'Analyse en cours…' : hasIssues ? issuesTitle(issues.length) : 'Tout est sous contrôle'

  return (
    <GlassCard variant="accent" className="w-full px-6 pt-4.5 pb-5.5">
      <div className="grid grid-cols-[auto_1fr_auto] items-center gap-6.5 px-3.5 pb-4 max-sm:gap-3.5 max-sm:px-0 max-sm:pb-3.5">
        <Icon className="text-[#f0a8ff] drop-shadow-[0_0_8px_rgba(230,130,255,0.55)]" size={46} strokeWidth={1.5} aria-hidden="true" />
        <h2 className="text-[28px] leading-tight font-medium max-sm:text-xl">{title}</h2>
        <ChevronRight className="text-white" size={30} strokeWidth={1.8} aria-hidden="true" />
      </div>
      {hasIssues && (
        <ul className="m-0 grid list-none gap-3.5 border-t border-[rgba(200,190,255,0.22)] px-3.5 pt-4.5">
          {issues.map((issue) => (
            <li key={issue.id} className="flex items-center gap-5.5 text-[21px] max-sm:gap-3.5 max-sm:text-[17px]">
              <span className={cn("size-3.25 shrink-0 rounded-full", BULLETS[issue.severity])} aria-hidden="true" />
              {issue.label}
            </li>
          ))}
        </ul>
      )}
    </GlassCard>
  )
}
