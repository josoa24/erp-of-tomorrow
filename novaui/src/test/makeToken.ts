const toBase64Url = (value: object): string => {
  const bytes = new TextEncoder().encode(JSON.stringify(value))
  return btoa(String.fromCharCode(...bytes)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
}

export const makeToken = (payload: object): string =>
  `${toBase64Url({ alg: 'HS256' })}.${toBase64Url(payload)}.signature`

export const futureExp = (): number => Math.floor(Date.now() / 1000) + 3600
