import { EclipseMark } from '../../../components/common/EclipseLogo'
import { AI_NAME, AI_STATUS, AI_TAGLINE } from '../../../constants/appInfo'
import { OrbNetwork } from './OrbNetwork'
import { OrbPlatform } from './OrbPlatform'
import styles from './AiOrb.module.css'

export const AiOrb = () => (
  <div className={styles.wrapper}>
    <div className={styles.eclipseZone} aria-hidden="true">
      <div className={styles.eclipse} />
    </div>
    <OrbPlatform />
    <div className={styles.orb}>
      <OrbNetwork />
      <div className={styles.text}>
        <EclipseMark size={78} />
        <p className={styles.name}>{AI_NAME}</p>
        <p className={styles.tagline}>{AI_TAGLINE}</p>
        <p className={styles.status}>
          <span aria-hidden="true">·</span> {AI_STATUS} <span aria-hidden="true">·</span>
        </p>
      </div>
    </div>
  </div>
)
