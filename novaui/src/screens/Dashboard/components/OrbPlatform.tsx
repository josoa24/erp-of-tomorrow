import styles from './OrbPlatform.module.css'

export const OrbPlatform = () => (
  <div className={styles.platform} aria-hidden="true">
    <div className={styles.beam} />
    <div className={styles.outerRing} />
    <div className={styles.ring} />
    <div className={styles.core} />
    <div className={styles.bubble}>
      <span className={styles.bar} />
      <span className={styles.bar} />
    </div>
  </div>
)
