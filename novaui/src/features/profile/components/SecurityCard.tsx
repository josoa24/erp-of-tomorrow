import { LogOut, Lock } from 'lucide-react'
import type { ReactNode } from 'react'
import { Button } from '../../../components/ui/button'
import { Panel } from '../../../components/ui/panel'
import { SectionTitle } from './SectionTitle'

const Line = ({ icon, title, text, action }: { icon: ReactNode; title: string; text: string; action?: ReactNode }) => (
  <div className="flex flex-wrap items-center gap-3">
    <span className="inline-flex size-10 shrink-0 items-center justify-center rounded-pill bg-bg-lilac">{icon}</span>
    <div className="flex min-w-0 flex-1 flex-col">
      <h3 className="text-body-strong text-ink-primary">{title}</h3>
      <p className="text-body-s text-ink-secondary">{text}</p>
    </div>
    {action}
  </div>
)

export const SecurityCard = ({ onLogout }: { onLogout: () => void }) => (
  <Panel aria-labelledby="profil-securite" padding="l" className="flex flex-col gap-5">
    <SectionTitle id="profil-securite">Sécurité</SectionTitle>
    <Line
      icon={<Lock aria-hidden="true" className="size-5" />}
      title="Mot de passe"
      text="Pour le changer, contactez l’administrateur de votre entreprise."
    />
    <Line
      icon={<LogOut aria-hidden="true" className="size-5" />}
      title="Session"
      text="Vous êtes connecté sur ce navigateur."
      action={
        <Button variant="secondary" leadingIcon={<LogOut aria-hidden="true" className="size-4" />} onClick={onLogout}>
          Se déconnecter
        </Button>
      }
    />
  </Panel>
)
