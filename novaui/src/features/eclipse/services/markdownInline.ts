export type InlineNode = { kind: 'text' | 'strong' | 'em' | 'code'; text: string }

const TOKEN = /(\*\*[^*]+\*\*|`[^`]+`|\*[^*\s][^*]*\*)/g

const toNode = (part: string): InlineNode => {
  if (part.startsWith('**') && part.endsWith('**') && part.length > 4) return { kind: 'strong', text: part.slice(2, -2) }
  if (part.startsWith('`') && part.endsWith('`') && part.length > 2) return { kind: 'code', text: part.slice(1, -1) }
  if (part.startsWith('*') && part.endsWith('*') && part.length > 2) return { kind: 'em', text: part.slice(1, -1) }
  return { kind: 'text', text: part }
}

export const parseInline = (text: string): InlineNode[] =>
  text
    .split(TOKEN)
    .filter((part) => part !== '')
    .map(toNode)
