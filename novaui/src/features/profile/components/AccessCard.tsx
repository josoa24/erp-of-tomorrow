import { Panel } from '../../../components/ui/panel'
import type { AccessChip } from '../services/profileAccess'
import { SectionTitle } from './SectionTitle'

interface AccessCardProps {
  sentence: string
  accesses: AccessChip[]
}

export const AccessCard = ({ sentence, accesses }: AccessCardProps) => (
  <Panel aria-labelledby="profil-acces" padding="l" className="flex flex-col gap-4">
    <SectionTitle id="profil-acces">Mes accès</SectionTitle>
    <p className="text-body-s text-ink-secondary">{sentence}</p>
    <ul className="flex flex-wrap gap-2">
      {accesses.map(({ label, icon: Icon }) => (
        <li key={label} className="inline-flex items-center gap-2 rounded-pill bg-bg-lilac px-3.5 py-2 text-label text-ink-primary">
          <Icon aria-hidden="true" className="size-4" />
          {label}
        </li>
      ))}
    </ul>
  </Panel>
)
