import { ShieldCheck } from 'lucide-react'
import { Avatar } from '../../../components/ui/avatar'
import { Panel } from '../../../components/ui/panel'
import { StatusPill } from '../../../components/ui/status-pill'
import { SectionTitle } from './SectionTitle'

interface IdentityCardProps {
  name: string
  email: string
  roleLabel: string
}

const Row = ({ label, value }: { label: string; value: string }) => (
  <div className="flex items-baseline justify-between gap-4">
    <dt className="text-micro text-ink-secondary">{label}</dt>
    <dd className="text-body-strong break-all text-right text-ink-primary">{value}</dd>
  </div>
)

export const IdentityCard = ({ name, email, roleLabel }: IdentityCardProps) => (
  <Panel aria-labelledby="profil-identite" padding="l" className="flex flex-col gap-5">
    <SectionTitle id="profil-identite">Identité</SectionTitle>
    <div className="flex items-center gap-4">
      <Avatar name={name} size="l" decorative />
      <div className="flex min-w-0 flex-col items-start gap-1">
        <p className="text-heading break-words text-ink-primary">{name}</p>
        <StatusPill label={roleLabel} icon={ShieldCheck} />
      </div>
    </div>
    <dl className="flex flex-col gap-4">
      <Row label="E-mail de connexion" value={email} />
      <Row label="Rôle" value={roleLabel} />
    </dl>
    <p className="text-micro text-ink-tertiary">Ces informations sont gérées par l’administrateur de votre entreprise.</p>
  </Panel>
)
