export interface JwtPayload {
  sub?: string
  role?: string
  exp?: number
}

const decodeBase64Url = (value: string): string => {
  const base64 = value.replace(/-/g, '+').replace(/_/g, '/')
  const bytes = Uint8Array.from(atob(base64), (char) => char.charCodeAt(0))
  return new TextDecoder().decode(bytes)
}

export const decodeJwtPayload = (token: string): JwtPayload | null => {
  const payload = token.split('.')[1]
  if (!payload) return null
  try {
    return JSON.parse(decodeBase64Url(payload)) as JwtPayload
  } catch {
    return null
  }
}

export const isTokenExpired = (token: string, now = Date.now()): boolean => {
  const payload = decodeJwtPayload(token)
  if (!payload) return true
  return payload.exp !== undefined && payload.exp * 1000 <= now
}
