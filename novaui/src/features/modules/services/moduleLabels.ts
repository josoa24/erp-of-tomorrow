import type { ModuleDefinition } from '../types/moduleDefinition'

const FEMININE_ENDINGS = ['ion', 'son', 'ie', 'ure', 'ée', 'té', 'se', 'ine']
const NEW_RECORD_FALLBACK = 'Nouvel élément'
const RECENT_MINUTES = 5

const singularWord = (word: string): string => (word.endsWith('s') ? word.slice(0, -1) : word)

const singular = (name: string): string => {
  const [first = '', ...rest] = name.trim().toLocaleLowerCase('fr-FR').split(/\s+/)
  return [singularWord(first), ...rest].join(' ')
}

const firstWord = (name: string): string => singular(name).split(' ')[0] ?? ''

const isFeminine = (name: string): boolean => FEMININE_ENDINGS.some((ending) => firstWord(name).endsWith(ending))

export const allLabel = (name: string): string => (isFeminine(name) ? 'Toutes' : 'Tous')

export const newRecordLabel = (name: string): string => {
  return isFeminine(name) ? `Nouvelle ${singular(name)}` : NEW_RECORD_FALLBACK
}

export const emptyTitle = (name: string): string => `Aucune ${singular(name)} pour l’instant`

export const searchPlaceholder = (definition: ModuleDefinition): string => {
  const reference = definition.columns.find((column) => column.reference === 'CUSTOMER') ?? definition.columns.find((column) => column.reference)
  const text = definition.columns.find((column) => column.type === 'TEXT')
  const labels = [reference, text].filter((column) => column !== undefined).map((column) => column.label.toLocaleLowerCase('fr-FR'))
  return labels.length === 0 ? 'Rechercher…' : `Rechercher ${labels.map((label) => `un ${label}`).join(', ')}…`
}

export const moduleSubtitle = (definition: ModuleDefinition, total: number | undefined, now = Date.now()): string => {
  const ageMinutes = (now - Date.parse(definition.createdAt)) / 60_000
  if (ageMinutes >= 0 && ageMinutes < RECENT_MINUTES) return 'Module créé à l’instant'
  const lower = definition.name.toLocaleLowerCase('fr-FR')
  return total === undefined ? 'Aujourd’hui' : `Aujourd’hui · ${total} ${total > 1 ? lower : singular(definition.name)}`
}

export const dockPlaceholder = (name: string): string =>
  `Demandez ou adaptez « ${name} »… ex. « ce qui reste à traiter aujourd’hui »`
