import { APP_NAME } from '../../constants/appInfo'
import styles from './EclipseLogo.module.css'

interface EclipseMarkProps {
  size?: number
}

export const EclipseMark = ({ size = 48 }: EclipseMarkProps) => (
  <svg width={size} height={size} viewBox="0 0 48 48" aria-hidden="true" className={styles.mark}>
    <defs>
      <linearGradient id="eclipse-mark-fill" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stopColor="#f1ecff" />
        <stop offset="45%" stopColor="#b79bff" />
        <stop offset="100%" stopColor="#6a3df0" />
      </linearGradient>
      <mask id="eclipse-mark-mask">
        <rect width="48" height="48" fill="#fff" />
        <circle cx="30" cy="21" r="16" fill="#000" />
      </mask>
    </defs>
    <circle cx="24" cy="24" r="21" fill="url(#eclipse-mark-fill)" mask="url(#eclipse-mark-mask)" />
  </svg>
)

export const EclipseLogo = () => (
  <div className={styles.logo}>
    <EclipseMark />
    <span className={styles.wordmark}>{APP_NAME}</span>
  </div>
)
