import { Sparkles } from 'lucide-react'
import { eclipseOrbVariants } from '../../../components/eclipse/eclipse-bar-variants'

export const EclipseAvatar = () => (
  <span aria-hidden="true" className={`${eclipseOrbVariants({ variant: 'light' })} size-9`}>
    <Sparkles />
  </span>
)
