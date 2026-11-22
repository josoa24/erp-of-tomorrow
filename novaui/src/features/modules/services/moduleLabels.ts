import type { ModuleDefinition } from '../types/moduleDefinition'

const FEMININE_ENDINGS = ['ion', 'ie', 'ure', 'ée', 'té', 'se', 'ine']
const NEW_RECORD_FALLBACK = 'Nouvel élément'
const RECENT_MINUTES = 5

const singular = (name: string): string => {
  const lower = name.trim().toLocaleLowerCase('fr-FR')
  return lower.endsWith('s') ? lower.slice(0, -1) : lower
}

export const newRecordLabel = (name: string): string => {
  const word = singular(name)
  return FEMININE_ENDINGS.some((ending) => word.endsWith(ending)) ? `Nouvelle ${word}` : NEW_RECORD_FALLBACK
}

export const emptyTitle = (name: string): string => `Aucune ${singular(name)} pour l’instant`

export const searchPlaceholder = (definition: ModuleDefinition): string => {
  const labels = definition.columns
    .filter((column) => column.type === 'TEXT' || column.type === 'REFERENCE')
    .slice(0, 2)
    .map((column) => column.label.toLocaleLowerCase('fr-FR'))
  return labels.length === 0 ? 'Rechercher…' : `Rechercher ${labels.map((label) => `un ${label}`).join(', ')}…`
}

export const moduleSubtitle = (definition: ModuleDefinition, total: number | undefined, now = Date.now()): string => {
  const ageMinutes = (now - Date.parse(definition.createdAt)) / 60_000
  if (ageMinutes >= 0 && ageMinutes < RECENT_MINUTES) return 'Module créé à l’instant'
  const lower = definition.name.toLocaleLowerCase('fr-FR')
  return total === undefined ? 'Aujourd’hui' : `Aujourd’hui · ${total} ${total > 1 ? lower : singular(definition.name)}`
}

export const dockPlaceholder = (name: string): string =>
  `Demandez ou adaptez « ${name} »… ex. « tournées de demain par quartier »`
