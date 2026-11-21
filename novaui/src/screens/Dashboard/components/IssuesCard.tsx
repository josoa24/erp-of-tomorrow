import { ChevronRight, CircleCheck, TriangleAlert } from 'lucide-react'
import { GlassCard } from '../../../components/common/GlassCard'
import type { Issue } from '../../../types/dashboard'
import styles from './IssuesCard.module.css'

interface IssuesCardProps {
  issues: Issue[]
  loading: boolean
}

const issuesTitle = (count: number): string =>
  count > 1 ? `${count} points demandent votre attention` : '1 point demande votre attention'

export const IssuesCard = ({ issues, loading }: IssuesCardProps) => {
  const hasIssues = issues.length > 0
  const Icon = hasIssues || loading ? TriangleAlert : CircleCheck
  const title = loading ? 'Analyse en cours…' : hasIssues ? issuesTitle(issues.length) : 'Tout est sous contrôle'

  return (
    <GlassCard variant="accent" className={styles.card}>
      <div className={styles.header}>
        <Icon className={styles.icon} size={46} strokeWidth={1.5} aria-hidden="true" />
        <h2 className={styles.title}>{title}</h2>
        <ChevronRight className={styles.chevron} size={30} strokeWidth={1.8} aria-hidden="true" />
      </div>
      {hasIssues && (
        <ul className={styles.list}>
          {issues.map((issue) => (
            <li key={issue.id} className={styles.item}>
              <span className={`${styles.bullet} ${styles[issue.severity]}`} aria-hidden="true" />
              {issue.label}
            </li>
          ))}
        </ul>
      )}
    </GlassCard>
  )
}
