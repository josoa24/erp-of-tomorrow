import { Copy, Eye, EyeOff } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { IconButton } from '../../../components/ui/icon-button'
import { Input } from '../../../components/ui/input'
import { useCopy } from '../hooks/useCopy'
import { MIN_PASSWORD_LENGTH } from '../services/passwordGenerator'

interface GeneratedPasswordFieldProps {
  label?: string
  value: string
  visible: boolean
  error?: string
  onChange: (value: string) => void
  onToggleVisible: () => void
  onGenerate: () => void
}

export const GeneratedPasswordField = ({ label = 'Mot de passe provisoire', value, visible, error, onChange, onToggleVisible, onGenerate }: GeneratedPasswordFieldProps) => {
  const { copied, copy } = useCopy()

  return (
    <div className="flex flex-col gap-2">
      <Input
        label={label}
        required
        type={visible ? 'text' : 'password'}
        autoComplete="new-password"
        spellCheck={false}
        placeholder={`Au moins ${MIN_PASSWORD_LENGTH} caractères`}
        value={value}
        invalid={error !== undefined}
        helper={error ?? (copied ? 'Mot de passe copié' : undefined)}
        onChange={(event) => onChange(event.target.value)}
      />
      <div className="flex items-center gap-2">
        <Button variant="secondary" size="s" onClick={onGenerate}>Générer</Button>
        <IconButton size="s" aria-label={visible ? 'Masquer le mot de passe' : 'Afficher le mot de passe'} aria-pressed={visible} onClick={onToggleVisible}>
          {visible ? <EyeOff /> : <Eye />}
        </IconButton>
        <IconButton size="s" aria-label="Copier le mot de passe" disabled={value === ''} onClick={() => void copy(value)}>
          <Copy />
        </IconButton>
      </div>
    </div>
  )
}
