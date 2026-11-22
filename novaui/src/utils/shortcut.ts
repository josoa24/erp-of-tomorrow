export type FocusShortcut = 'slash' | 'mod-k'

const EDITABLE_TAGS = ['INPUT', 'TEXTAREA', 'SELECT']

export const isEditableTarget = (target: EventTarget | null): boolean => {
  if (!(target instanceof HTMLElement)) return false
  return EDITABLE_TAGS.includes(target.tagName) || target.isContentEditable === true
}

export const matchesShortcut = (event: KeyboardEvent, shortcut: FocusShortcut): boolean => {
  if (shortcut === 'mod-k') return (event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k'
  return event.key === '/' && !event.ctrlKey && !event.metaKey && !event.altKey && !isEditableTarget(event.target)
}

export const isApplePlatform = (): boolean =>
  typeof navigator !== 'undefined' && /Mac|iPhone|iPad/.test(navigator.platform)

export const modKeyLabel = (): string => (isApplePlatform() ? '⌘ K' : 'Ctrl K')
