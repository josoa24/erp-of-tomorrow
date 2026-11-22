import { Button } from '../../../components/ui/button'
import { pageBounds, pageCount } from '../services/usersSearch'

interface UsersPaginationProps {
  page: number
  total: number
  onPage: (page: number) => void
}

export const UsersPagination = ({ page, total, onPage }: UsersPaginationProps) => {
  const pages = pageCount(total)
  const { first, last } = pageBounds(page, total)
  if (pages <= 1) return null

  return (
    <nav aria-label="Pagination des utilisateurs" className="flex items-center justify-between gap-3 px-6 pb-4 text-body-s text-ink-secondary">
      <span>{first} à {last} sur {total}</span>
      <span className="flex gap-2">
        <Button variant="secondary" size="s" disabled={page <= 1} onClick={() => onPage(page - 1)}>Précédent</Button>
        <Button variant="secondary" size="s" disabled={page >= pages} onClick={() => onPage(page + 1)}>Suivant</Button>
      </span>
    </nav>
  )
}
