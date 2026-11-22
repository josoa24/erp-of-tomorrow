import { useCallback, useState } from 'react'
import { generatePassword } from '../services/passwordGenerator'

export const useGeneratedPassword = () => {
  const [password, setPassword] = useState('')
  const [visible, setVisible] = useState(false)
  const generate = useCallback(() => {
    setPassword(generatePassword())
    setVisible(true)
  }, [])

  return { password, setPassword, visible, toggleVisible: () => setVisible((current) => !current), generate }
}
