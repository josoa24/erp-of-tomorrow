import { RoleBadge } from '../../../components/module/RoleBadge'
import { ROLE_LABELS } from '../../../constants/roleLabels'
import type { Role } from '../../../types/auth'
import { Avatar } from '../../../components/ui/avatar'
import { Panel } from '../../../components/ui/panel'
import { SectionTitle } from './SectionTitle'

interface IdentityCardProps {
  name: string
  email: string
  roles: Role[]
}

const Row = ({ label, value }: { label: string; value: string }) => (
  <div className="flex items-baseline justify-between gap-4">
    <dt className="text-micro text-ink-secondary">{label}</dt>
    <dd className="text-body-strong break-all text-right text-ink-primary">{value}</dd>
  </div>
)

export const IdentityCard = ({ name, email, roles }: IdentityCardProps) => (
  <Panel aria-labelledby="profil-identite" padding="l" className="flex flex-col gap-5">
    <SectionTitle id="profil-identite">Identité</SectionTitle>
    <div className="flex items-center gap-4">
      <Avatar name={name} size="l" decorative />
      <div className="flex min-w-0 flex-col items-start gap-1">
        <p className="text-heading break-words text-ink-primary">{name}</p>
        <div className="flex flex-wrap gap-1">
          {roles.map((role) => (
            <RoleBadge key={role} role={role} />
          ))}
        </div>
      </div>
    </div>
    <dl className="flex flex-col gap-4">
      <Row label="E-mail de connexion" value={email} />
      <Row label={roles.length > 1 ? 'Rôles' : 'Rôle'} value={roles.map((role) => ROLE_LABELS[role]).join(', ')} />
    </dl>
    <p className="text-micro text-ink-tertiary">Ces informations sont gérées par l’administrateur de votre entreprise.</p>
  </Panel>
)
