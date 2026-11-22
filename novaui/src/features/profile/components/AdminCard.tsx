import { UserCog } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { Panel } from '../../../components/ui/panel'
import { SectionTitle } from './SectionTitle'

export const AdminCard = ({ onOpen }: { onOpen: () => void }) => (
  <Panel aria-labelledby="profil-administration" padding="l" className="flex flex-col gap-5">
    <SectionTitle id="profil-administration">Administration</SectionTitle>
    <div className="flex flex-wrap items-center gap-3">
      <span className="inline-flex size-10 shrink-0 items-center justify-center rounded-pill bg-bg-lilac">
        <UserCog aria-hidden="true" className="size-5" />
      </span>
      <div className="flex min-w-0 flex-1 flex-col">
        <h3 className="text-body-strong text-ink-primary">Gestion des utilisateurs</h3>
        <p className="text-body-s text-ink-secondary">Créez des comptes, changez les rôles et réinitialisez les mots de passe.</p>
      </div>
      <Button variant="secondary" onClick={onOpen}>Gérer les utilisateurs</Button>
    </div>
  </Panel>
)
