import { useState, type FormEvent } from 'react'
import { Button } from '../../../components/ui/button'
import { Input } from '../../../components/ui/input'
import { PasswordField } from '../../../components/ui/password-field'
import type { LoginRequest } from '../../../types/auth'

interface LoginFormProps {
  initialEmail?: string
  submitting: boolean
  onSubmit: (credentials: LoginRequest) => void
}

export const LoginForm = ({ initialEmail = '', submitting, onSubmit }: LoginFormProps) => {
  const [email, setEmail] = useState(initialEmail)
  const [password, setPassword] = useState('')
  const complete = email.trim() !== '' && password !== ''

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!complete || submitting) return
    onSubmit({ email: email.trim(), password })
  }

  return (
    <form className="flex w-full flex-col gap-5" onSubmit={handleSubmit} noValidate>
      <Input
        label="Adresse e-mail"
        type="email"
        autoComplete="username"
        placeholder="prenom@entreprise.mg"
        required
        value={email}
        onChange={(event) => setEmail(event.target.value)}
      />
      <PasswordField
        required
        placeholder="Votre mot de passe"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
      />
      <Button type="submit" disabled={!complete} loading={submitting} className="w-full">
        Se connecter
      </Button>
    </form>
  )
}
