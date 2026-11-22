import { Sparkles } from 'lucide-react'
import { Button } from '../../../components/ui/button'
import { ChoiceChip } from '../../../components/ui/choice-chip'
import { Dialog } from '../../../components/ui/dialog'
import { Textarea } from '../../../components/ui/textarea'
import { useBreakpoint } from '../../../hooks/useBreakpoint'
import { DESCRIPTION_MAX, MODULE_EXAMPLES } from '../constants/moduleExamples'
import { EclipseOrb } from './EclipseOrb'

const FORM_ID = 'creer-module-decrire'

interface CreateModuleDescribeDialogProps {
  description: string
  preparing: boolean
  onChange: (value: string) => void
  onSubmit: () => void
  onClose: () => void
}

export const CreateModuleDescribeDialog = ({ description, preparing, onChange, onSubmit, onClose }: CreateModuleDescribeDialogProps) => {
  const placement = useBreakpoint() === 'mobile' ? 'sheet' : 'center'
  const empty = description.trim() === ''

  return (
    <Dialog
      open
      onClose={onClose}
      placement={placement}
      icon={<EclipseOrb />}
      title="Créer un module"
      description="Décrivez ce que vous voulez suivre. Eclipse prépare le module, rien n’est créé sans votre accord."
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={preparing}>Annuler</Button>
          <Button type="submit" form={FORM_ID} variant="eclipse" leadingIcon={<Sparkles />} loading={preparing} disabled={empty}>
            Préparer avec Eclipse
          </Button>
        </>
      }
    >
      <form
        id={FORM_ID}
        className="flex flex-col gap-5"
        onSubmit={(event) => {
          event.preventDefault()
          if (!empty) onSubmit()
        }}
      >
        <Textarea
          label="Votre besoin"
          rows={4}
          maxLength={DESCRIPTION_MAX}
          value={description}
          helper="En une phrase, avec vos mots."
          onChange={(event) => onChange(event.target.value)}
        />
        <div className="flex flex-col gap-2">
          <span className="text-caption text-ink-secondary uppercase">Exemples</span>
          <div className="flex flex-wrap gap-2">
            {MODULE_EXAMPLES.map((example) => (
              <ChoiceChip
                key={example.label}
                label={example.label}
                icon={example.icon}
                selected={description === example.text}
                onClick={() => onChange(example.text)}
              />
            ))}
          </div>
        </div>
      </form>
    </Dialog>
  )
}
