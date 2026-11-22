const ALPHABET = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789'
export const GENERATED_PASSWORD_LENGTH = 12
export const MIN_PASSWORD_LENGTH = 8

export const generatePassword = (length = GENERATED_PASSWORD_LENGTH): string => {
  const limit = Math.floor(0x100000000 / ALPHABET.length) * ALPHABET.length
  const picked: string[] = []
  while (picked.length < length) {
    const [value] = crypto.getRandomValues(new Uint32Array(1))
    if (value < limit) picked.push(ALPHABET[value % ALPHABET.length])
  }
  return picked.join('')
}
