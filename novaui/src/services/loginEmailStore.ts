const LOGIN_EMAIL_KEY = 'nova.login.email'

export const rememberLoginEmail = (email: string): void => {
  try {
    sessionStorage.setItem(LOGIN_EMAIL_KEY, email)
  } catch {
    return
  }
}

export const readLoginEmail = (): string => {
  try {
    return sessionStorage.getItem(LOGIN_EMAIL_KEY) ?? ''
  } catch {
    return ''
  }
}

export const forgetLoginEmail = (): void => {
  try {
    sessionStorage.removeItem(LOGIN_EMAIL_KEY)
  } catch {
    return
  }
}
