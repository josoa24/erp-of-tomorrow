import { useState, type FormEvent } from 'react'
import { Button } from '../../../components/ui/button'
import { Checkbox } from '../../../components/ui/checkbox'
import { Input } from '../../../components/ui/input'
import { PasswordField } from '../../../components/ui/password-field'
import type { LoginRequest } from '../../../types/auth'

interface LoginFormProps {
  submitting: boolean
  onSubmit: (credentials: LoginRequest, remember: boolean) => void
}

export const LoginForm = ({ submitting, onSubmit }: LoginFormProps) => {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [remember, setRemember] = useState(false)

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    onSubmit({ email: email.trim(), password }, remember)
  }

  return (
    <form className="flex w-full flex-col gap-5" onSubmit={handleSubmit}>
      <Input
        label="Adresse e-mail"
        type="email"
        autoComplete="username"
        required
        value={email}
        onChange={(event) => setEmail(event.target.value)}
      />
      <PasswordField required value={password} onChange={(event) => setPassword(event.target.value)} />
      <Checkbox label="Se souvenir de moi" checked={remember} onChange={(event) => setRemember(event.target.checked)} />
      <Button type="submit" disabled={submitting} className="w-full">
        {submitting ? 'Connexion…' : 'Se connecter'}
      </Button>
    </form>
  )
}
