import { AUTO_SEND_KEY } from '../constants/voiceConfig'

export const readAutoSend = (): boolean => {
  try {
    return localStorage.getItem(AUTO_SEND_KEY) !== 'off'
  } catch {
    return true
  }
}

export const writeAutoSend = (enabled: boolean): void => {
  try {
    localStorage.setItem(AUTO_SEND_KEY, enabled ? 'on' : 'off')
  } catch {
    return
  }
}
