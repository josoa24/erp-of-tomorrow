export type MarkdownBlock =
  | { kind: 'paragraph'; text: string }
  | { kind: 'heading'; text: string }
  | { kind: 'list'; ordered: boolean; items: string[] }
  | { kind: 'table'; head: string[]; rows: string[][] }

const BULLET = /^\s*[-*•]\s+(.*)$/
const NUMBERED = /^\s*\d+[.)]\s+(.*)$/
const HEADING = /^\s*#{1,6}\s+(.*)$/
const TABLE_ROW = /^\s*\|(.+)\|\s*$/
const TABLE_RULE = /^\s*\|?[\s:|-]+\|[\s:|-]*$/

const cells = (line: string): string[] => line.replace(/^\s*\|/, '').replace(/\|\s*$/, '').split('|').map((cell) => cell.trim())

const isTable = (lines: string[], index: number): boolean =>
  TABLE_ROW.test(lines[index] ?? '') && TABLE_RULE.test(lines[index + 1] ?? '')

const takeTable = (lines: string[], start: number): [MarkdownBlock, number] => {
  const rows: string[][] = []
  let index = start + 2
  while (index < lines.length && TABLE_ROW.test(lines[index] ?? '')) rows.push(cells(lines[index++] ?? ''))
  return [{ kind: 'table', head: cells(lines[start] ?? ''), rows }, index]
}

const takeList = (lines: string[], start: number, pattern: RegExp): [MarkdownBlock, number] => {
  const items: string[] = []
  let index = start
  while (index < lines.length && pattern.test(lines[index] ?? '')) items.push((lines[index++] ?? '').replace(pattern, '$1'))
  return [{ kind: 'list', ordered: pattern === NUMBERED, items }, index]
}

const takeParagraph = (lines: string[], start: number): [MarkdownBlock, number] => {
  const parts: string[] = []
  let index = start
  while (index < lines.length && (lines[index] ?? '').trim() !== '' && !BULLET.test(lines[index] ?? '') && !NUMBERED.test(lines[index] ?? '') && !HEADING.test(lines[index] ?? '') && !isTable(lines, index)) {
    parts.push((lines[index++] ?? '').trim())
  }
  return [{ kind: 'paragraph', text: parts.join(' ') }, Math.max(index, start + 1)]
}

const nextBlock = (lines: string[], index: number): [MarkdownBlock, number] => {
  const line = lines[index] ?? ''
  if (HEADING.test(line)) return [{ kind: 'heading', text: line.replace(HEADING, '$1') }, index + 1]
  if (isTable(lines, index)) return takeTable(lines, index)
  if (BULLET.test(line)) return takeList(lines, index, BULLET)
  if (NUMBERED.test(line)) return takeList(lines, index, NUMBERED)
  return takeParagraph(lines, index)
}

export const parseBlocks = (markdown: string): MarkdownBlock[] => {
  const lines = markdown.replace(/\r\n/g, '\n').split('\n')
  const blocks: MarkdownBlock[] = []
  let index = 0
  while (index < lines.length) {
    if ((lines[index] ?? '').trim() === '') {
      index += 1
      continue
    }
    const [block, next] = nextBlock(lines, index)
    blocks.push(block)
    index = next
  }
  return blocks
}
