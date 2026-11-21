import styles from './Connector.module.css'

interface ConnectorProps {
  side: 'left' | 'right'
  direction: 'down' | 'up'
}

const PATHS = {
  down: 'M0 50 H62 C96 50 118 68 186 82',
  up: 'M0 50 H62 C96 50 118 32 186 18',
}

export const Connector = ({ side, direction }: ConnectorProps) => (
  <svg
    className={`${styles.connector} ${styles[side]}`}
    width="190"
    height="100"
    viewBox="0 0 190 100"
    aria-hidden="true"
  >
    <path d={PATHS[direction]} className={styles.line} />
    <circle cx="66" cy="50" r="4.5" className={styles.dot} />
  </svg>
)
