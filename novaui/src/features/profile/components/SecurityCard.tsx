import { Button } from '../../../components/ui/button'
import { Panel } from '../../../components/ui/panel'

export const SecurityCard = ({ onLogout }: { onLogout: () => void }) => (
  <Panel aria-labelledby="profil-securite" padding="xl" className="flex flex-col gap-5">
    <h2 id="profil-securite" className="text-heading text-ink-primary">Sécurité</h2>
    <div className="flex flex-col gap-1">
      <h3 className="text-label text-ink-primary">Mot de passe</h3>
      <p className="text-body text-ink-secondary">Pour le changer, contactez l’administrateur de votre entreprise.</p>
    </div>
    <div className="flex flex-wrap items-center justify-between gap-3">
      <div className="flex flex-col gap-1">
        <h3 className="text-label text-ink-primary">Session</h3>
        <p className="text-body text-ink-secondary">Vous êtes connecté sur ce navigateur.</p>
      </div>
      <Button variant="secondary" onClick={onLogout}>Se déconnecter</Button>
    </div>
  </Panel>
)
