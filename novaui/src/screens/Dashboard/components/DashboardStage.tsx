import type { DashboardData } from '../../../types/dashboard'
import { AiOrb } from './AiOrb'
import { Connector } from './Connector'
import { HrCard } from './HrCard'
import { IssuesCard } from './IssuesCard'
import { SalesCard } from './SalesCard'
import { StockCard } from './StockCard'
import styles from './DashboardStage.module.css'

interface DashboardStageProps {
  data: DashboardData | null
}

export const DashboardStage = ({ data }: DashboardStageProps) => {
  const loading = data === null

  return (
    <div className={styles.stage}>
      <div className={`${styles.slot} ${styles.sales}`}>
        <SalesCard data={data?.sales ?? null} loading={loading} />
      </div>
      <div className={`${styles.slot} ${styles.stock}`}>
        <StockCard data={data?.stock ?? null} loading={loading} />
      </div>
      <div className={`${styles.link} ${styles.c1}`}>
        <Connector side="left" direction="down" />
      </div>
      <div className={`${styles.link} ${styles.c2}`}>
        <Connector side="left" direction="up" />
      </div>
      <div className={styles.orb}>
        <AiOrb />
      </div>
      <div className={`${styles.link} ${styles.c3}`}>
        <Connector side="right" direction="down" />
      </div>
      <div className={`${styles.link} ${styles.c4}`}>
        <Connector side="right" direction="up" />
      </div>
      <div className={`${styles.slot} ${styles.hr}`}>
        <HrCard data={data?.hr ?? null} loading={loading} />
      </div>
      <div className={`${styles.slot} ${styles.issues}`}>
        <IssuesCard issues={data?.issues ?? []} loading={loading} />
      </div>
    </div>
  )
}
