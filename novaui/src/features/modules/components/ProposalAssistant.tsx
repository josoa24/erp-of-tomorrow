import { Sparkles } from 'lucide-react'
import { useState } from 'react'
import { Button } from '../../../components/ui/button'
import { ChoiceChip } from '../../../components/ui/choice-chip'
import { Input } from '../../../components/ui/input'

const REFINE_PLACEHOLDER = 'Ex. Ajoute une colonne Priorité, retire le montant…'

interface ProposalAssistantProps {
  explanation?: string
  suggestions: string[]
  changes: string[]
  refining: boolean
  onRefine: (instruction: string) => void
}

export const ProposalAssistant = ({ explanation, suggestions, changes, refining, onRefine }: ProposalAssistantProps) => {
  const [instruction, setInstruction] = useState('')
  const send = (text: string) => {
    if (text.trim() === '' || refining) return
    onRefine(text.trim())
    setInstruction('')
  }

  return (
    <section aria-label="Conception du module" className="flex flex-col gap-3 rounded-xl border border-line-subtle p-4">
      {explanation && <p className="text-body-s text-ink-secondary">{explanation}</p>}
      {changes.length > 0 && (
        <ul className="flex flex-col gap-1 text-body-s text-ink-primary">
          {changes.map((change) => (
            <li key={change}>✓ {change}</li>
          ))}
        </ul>
      )}
      <form
        className="flex items-end gap-2"
        onSubmit={(event) => {
          event.preventDefault()
          send(instruction)
        }}
      >
        <Input
          label="Ajuster avec Eclipse"
          placeholder={REFINE_PLACEHOLDER}
          maxLength={500}
          value={instruction}
          containerClassName="flex-1"
          onChange={(event) => setInstruction(event.target.value)}
        />
        <Button type="submit" variant="eclipse" leadingIcon={<Sparkles />} loading={refining} disabled={instruction.trim() === ''}>
          Ajuster
        </Button>
      </form>
      {suggestions.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {suggestions.map((suggestion) => (
            <ChoiceChip key={suggestion} label={suggestion} selected={false} disabled={refining} onClick={() => send(suggestion)} />
          ))}
        </div>
      )}
    </section>
  )
}
