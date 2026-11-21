import { NAV_ITEMS } from '../../../constants/navItems'
import styles from './NavPill.module.css'

const ACTIVE_ID = 'home'

export const NavPill = () => (
  <nav className={styles.nav} aria-label="Navigation principale">
    {NAV_ITEMS.map(({ id, label, icon: Icon, available }) => (
      <button
        key={id}
        type="button"
        className={`${styles.item} ${id === ACTIVE_ID ? styles.active : ''}`}
        aria-label={label}
        aria-current={id === ACTIVE_ID ? 'page' : undefined}
        title={available ? label : `${label} (bientôt disponible)`}
        disabled={!available}
      >
        <Icon size={30} strokeWidth={1.5} aria-hidden="true" />
      </button>
    ))}
  </nav>
)
