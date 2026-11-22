import { Panel } from '../../../components/ui/panel'

interface AccessCardProps {
  sentence: string
  accesses: string[]
}

export const AccessCard = ({ sentence, accesses }: AccessCardProps) => (
  <Panel aria-labelledby="profil-acces" padding="xl" className="flex flex-col gap-4">
    <h2 id="profil-acces" className="text-heading text-ink-primary">Mes accès</h2>
    <p className="text-body text-ink-secondary">{sentence}</p>
    <ul className="flex flex-wrap gap-2">
      {accesses.map((access) => (
        <li key={access} className="rounded-pill bg-bg-lilac px-3 py-1 text-label text-ink-primary">
          {access}
        </li>
      ))}
    </ul>
  </Panel>
)
