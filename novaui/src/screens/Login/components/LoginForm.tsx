import { useState, type FormEvent } from 'react'
import { PillButton } from '../../../components/common/PillButton'
import { TextField } from '../../../components/common/TextField'
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
    <form className="flex w-full flex-col gap-3" onSubmit={handleSubmit}>
      <TextField
        label="Adresse e-mail"
        type="email"
        autoComplete="username"
        required
        value={email}
        onChange={(event) => setEmail(event.target.value)}
      />
      <TextField
        label="Mot de passe"
        type="password"
        autoComplete="current-password"
        required
        value={password}
        onChange={(event) => setPassword(event.target.value)}
      />
      <label className="mt-2 mb-3 inline-flex cursor-pointer items-center gap-2.5 text-[17px] select-none">
        <input
          type="checkbox"
          className="m-0 grid size-5.5 cursor-pointer appearance-none place-content-center rounded-[5px] border-[1.5px] border-[rgba(220,222,255,0.8)] bg-transparent after:h-1.5 after:w-2.5 after:translate-x-px after:-translate-y-px after:-rotate-45 after:border-b-2 after:border-l-2 after:border-[#0b0c2e] after:opacity-0 after:content-[''] checked:border-white checked:bg-white checked:after:opacity-100"
          checked={remember}
          onChange={(event) => setRemember(event.target.checked)}
        />
        Se souvenir de moi
      </label>
      <PillButton type="submit" disabled={submitting} className="mt-0.5">
        {submitting ? 'Connexion…' : 'Se connecter'}
      </PillButton>
    </form>
  )
}
