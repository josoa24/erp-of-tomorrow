import { useState, type FormEvent } from 'react'
import { PillButton } from '../../../components/common/PillButton'
import { TextField } from '../../../components/common/TextField'
import type { LoginRequest } from '../../../types/auth'
import styles from './LoginForm.module.css'

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
    <form className={styles.form} onSubmit={handleSubmit}>
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
      <label className={styles.remember}>
        <input
          type="checkbox"
          className={styles.checkbox}
          checked={remember}
          onChange={(event) => setRemember(event.target.checked)}
        />
        Se souvenir de moi
      </label>
      <PillButton type="submit" disabled={submitting} className={styles.submit}>
        {submitting ? 'Connexion…' : 'Se connecter'}
      </PillButton>
    </form>
  )
}
