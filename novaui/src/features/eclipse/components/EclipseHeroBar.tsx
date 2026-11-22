import { useState } from 'react'
import { EclipseBar } from '../../../components/eclipse/EclipseBar'
import { ECLIPSE_UNAVAILABLE_LABEL } from '../../../constants/eclipsePlaceholders'
import { useEclipseRequest } from '../hooks/useEclipseRequest'
import { EclipseAnswer } from './EclipseAnswer'

const HERO_PLACEHOLDER = 'Demandez, adaptez un écran ou créez un module…'

export const EclipseHeroBar = () => {
  const [value, setValue] = useState('')
  const { submit, pending, answer, unavailable, dismiss } = useEclipseRequest('accueil')

  return (
    <div>
      {answer && <EclipseAnswer text={answer} onDismiss={dismiss} />}
      <EclipseBar
        variant="night"
        placeholder={HERO_PLACEHOLDER}
        value={value}
        onChange={setValue}
        onSubmit={(text) => {
          setValue('')
          void submit(text)
        }}
        status={unavailable ? 'unavailable' : pending ? 'streaming' : 'idle'}
        unavailableLabel={ECLIPSE_UNAVAILABLE_LABEL}
      />
    </div>
  )
}
