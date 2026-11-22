import { EclipseAssistant } from './EclipseAssistant'

const HERO_PLACEHOLDER = 'Demandez, adaptez un écran ou créez un module…'

export const EclipseHeroBar = () => (
  <EclipseAssistant variant="night" placeholder={HERO_PLACEHOLDER} moduleKey="accueil" placement="below" />
)
