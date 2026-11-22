import { ShieldCheck } from 'lucide-react'
import { Avatar } from '../../../components/ui/avatar'
import { Panel } from '../../../components/ui/panel'
import { StatusPill } from '../../../components/ui/status-pill'

interface IdentityCardProps {
  name: string
  email: string
  roleLabel: string
}

export const IdentityCard = ({ name, email, roleLabel }: IdentityCardProps) => (
  <Panel aria-labelledby="profil-identite" padding="xl" className="flex flex-col gap-5">
    <h2 id="profil-identite" className="text-heading text-ink-primary">Identité</h2>
    <div className="flex items-center gap-4">
      <Avatar name={name} size="l" decorative />
      <div className="flex min-w-0 flex-col items-start gap-1">
        <p className="text-title break-words text-ink-primary">{name}</p>
        <StatusPill label={roleLabel} icon={ShieldCheck} />
      </div>
    </div>
    <dl className="grid gap-3 sm:grid-cols-2">
      <div>
        <dt className="text-caption text-ink-secondary uppercase">E-mail de connexion</dt>
        <dd className="text-body break-all text-ink-primary">{email}</dd>
      </div>
      <div>
        <dt className="text-caption text-ink-secondary uppercase">Rôle</dt>
        <dd className="text-body text-ink-primary">{roleLabel}</dd>
      </div>
    </dl>
    <p className="text-body-s text-ink-tertiary">Ces informations sont gérées par l’administrateur de votre entreprise.</p>
  </Panel>
)
