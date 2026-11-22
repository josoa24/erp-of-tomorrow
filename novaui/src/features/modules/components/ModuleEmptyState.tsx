import { SearchX, Sparkles } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { EmptyState } from '../../../components/ui/empty-state'
import { emptyTitle, newRecordLabel } from '../services/moduleLabels'
import { moduleIconFor } from '../services/moduleIcon'

interface ModuleEmptyStateProps {
  name: string
  icon: string
  query: string
  filtered: boolean
  importable: number
  importing: boolean
  onImport: () => void
  onCreate: () => void
}

export const ModuleEmptyState = ({ name, icon, query, filtered, importable, importing, onImport, onCreate }: ModuleEmptyStateProps) => {
  if (query !== '') {
    return <EmptyState icon={SearchX} title={`Aucun résultat pour « ${query} »`} description="Essayez un autre mot ou effacez la recherche." />
  }
  if (filtered) {
    return <EmptyState icon={moduleIconFor(icon)} title="Rien dans ce statut" description="Choisissez « Tous » pour tout revoir." />
  }
  return (
    <EmptyState
      icon={moduleIconFor(icon)}
      title={emptyTitle(name)}
      description={
        importable > 0
          ? `Eclipse a trouvé ${importable} ${importable > 1 ? 'ventes' : 'vente'} de cette semaine marquées « à livrer ».`
          : 'Ajoutez le premier enregistrement pour démarrer le suivi.'
      }
      actions={
        <>
          {importable > 0 && (
            <Button variant="eclipse" leadingIcon={<Sparkles />} loading={importing} onClick={onImport}>
              {`Reprendre les ${importable} ${importable > 1 ? 'ventes' : 'vente'}`}
            </Button>
          )}
          <Button variant={importable > 0 ? 'secondary' : 'primary'} onClick={onCreate}>
            {newRecordLabel(name)}
          </Button>
        </>
      }
    />
  )
}
