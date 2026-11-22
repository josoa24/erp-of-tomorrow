import { parseBlocks, type MarkdownBlock } from '../services/markdownBlocks'
import { parseInline } from '../services/markdownInline'

const Inline = ({ text }: { text: string }) => (
  <>
    {parseInline(text).map((node, index) => {
      if (node.kind === 'strong') return <strong key={index} className="font-semibold">{node.text}</strong>
      if (node.kind === 'em') return <em key={index}>{node.text}</em>
      if (node.kind === 'code') return <code key={index} className="rounded-sm bg-surface-sunken px-1 text-body-s">{node.text}</code>
      return <span key={index}>{node.text}</span>
    })}
  </>
)

const Table = ({ head, rows }: { head: string[]; rows: string[][] }) => (
  <div className="overflow-x-auto">
    <table className="w-full text-left text-body-s">
      <thead>
        <tr>
          {head.map((cell, index) => (
            <th key={index} scope="col" className="px-2 py-1 text-caption text-ink-secondary uppercase"><Inline text={cell} /></th>
          ))}
        </tr>
      </thead>
      <tbody>
        {rows.map((row, rowIndex) => (
          <tr key={rowIndex} className="border-t border-line-subtle">
            {row.map((cell, index) => (
              <td key={index} className="px-2 py-1"><Inline text={cell} /></td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  </div>
)

const Block = ({ block }: { block: MarkdownBlock }) => {
  if (block.kind === 'heading') return <h3 className="text-body-strong text-ink-primary"><Inline text={block.text} /></h3>
  if (block.kind === 'table') return <Table head={block.head} rows={block.rows} />
  if (block.kind === 'list') {
    const List = block.ordered ? 'ol' : 'ul'
    return (
      <List className={`flex flex-col gap-1 pl-5 ${block.ordered ? 'list-decimal' : 'list-disc'}`}>
        {block.items.map((item, index) => (
          <li key={index}>
            <Inline text={item.text} />
            {item.children.length > 0 && (
              <ul className="mt-1 flex list-disc flex-col gap-1 pl-5">
                {item.children.map((child, childIndex) => (
                  <li key={childIndex}><Inline text={child} /></li>
                ))}
              </ul>
            )}
          </li>
        ))}
      </List>
    )
  }
  return <p><Inline text={block.text} /></p>
}

export const MarkdownText = ({ text }: { text: string }) => (
  <div className="flex flex-col gap-2 text-body text-ink-primary">
    {parseBlocks(text).map((block, index) => (
      <Block key={index} block={block} />
    ))}
  </div>
)
