export const firstName = (displayName: string): string => displayName.trim().split(/\s+/)[0] ?? displayName

export const attentionLine = (count: number): string =>
  count === 0
    ? 'Rien ne demande votre attention aujourd’hui.'
    : `${count} ${count > 1 ? 'choses demandent' : 'chose demande'} votre attention aujourd’hui.`
