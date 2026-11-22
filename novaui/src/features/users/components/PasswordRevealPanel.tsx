import { Check, Copy } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { useCopy } from '../hooks/useCopy'

interface PasswordRevealPanelProps {
  account: string
  email?: string
  password: string
}

export const PasswordRevealPanel = ({ account, email, password }: PasswordRevealPanelProps) => {
  const { copied, copy } = useCopy()

  return (
    <div className="flex flex-col gap-4">
      <dl className="flex flex-col gap-3 rounded-md bg-bg-lilac p-4">
        <div className="flex flex-col gap-0.5">
          <dt className="text-micro text-ink-tertiary">Compte</dt>
          <dd className="text-body-strong break-all text-ink-primary">{email ?? account}</dd>
        </div>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex min-w-0 flex-col gap-0.5">
            <dt className="text-micro text-ink-tertiary">Mot de passe provisoire</dt>
            <dd data-testid="revealed-password" className="font-mono text-body-strong tracking-wide break-all text-ink-primary">{password}</dd>
          </div>
          <Button variant="secondary" size="s" leadingIcon={copied ? <Check aria-hidden="true" /> : <Copy aria-hidden="true" />} onClick={() => void copy(password)}>
            {copied ? 'Copié' : 'Copier'}
          </Button>
        </div>
      </dl>
      <p className="text-body-s text-ink-secondary">
        Ce mot de passe ne sera plus affiché. Transmettez-le à la personne concernée : elle pourra le changer ensuite.
      </p>
    </div>
  )
}
