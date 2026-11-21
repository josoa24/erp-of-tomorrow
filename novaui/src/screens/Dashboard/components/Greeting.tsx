import { getGreeting } from '../../../utils/greeting'
import styles from './Greeting.module.css'

export const Greeting = () => (
  <div className={styles.greeting}>
    <h1 className={styles.title}>{getGreeting(new Date().getHours())}</h1>
    <p className={styles.subtitle}>Votre activité est vivante et en mouvement.</p>
  </div>
)
