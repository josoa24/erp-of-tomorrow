import type { DashboardData } from '../../../types/dashboard'
import { AiOrb } from './AiOrb'
import { Connector } from './Connector'
import { HrCard } from './HrCard'
import { IssuesCard } from './IssuesCard'
import { SalesCard } from './SalesCard'
import { StockCard } from './StockCard'

interface DashboardStageProps {
  data: DashboardData | null
}

const STAGE =
  "mt-11.5 grid items-center justify-center gap-y-15 pb-60 [grid-template-areas:'sales_c1_orb_c3_hr'_'stock_c2_orb_c4_issues'] grid-cols-[minmax(300px,390px)_130px_var(--orb-size,400px)_130px_minmax(300px,450px)] " +
  "max-[1400px]:[--orb-size:330px] max-[1400px]:grid-cols-[minmax(280px,360px)_90px_var(--orb-size)_90px_minmax(280px,400px)] " +
  "max-[1100px]:[--orb-size:min(340px,80vw)] max-[1100px]:grid-cols-2 max-[1100px]:gap-6 max-[1100px]:pb-10 max-[1100px]:[grid-template-areas:'orb_orb'_'sales_hr'_'stock_issues'] " +
  "max-[720px]:grid-cols-[minmax(0,1fr)] max-[720px]:[grid-template-areas:'orb'_'sales'_'stock'_'hr'_'issues']"

const SLOT = 'relative z-2'
const LINK = 'z-0 max-[1400px]:hidden'

export const DashboardStage = ({ data }: DashboardStageProps) => {
  const loading = data === null

  return (
    <div className={STAGE}>
      <div className={`${SLOT} [grid-area:sales] ml-6.5 max-[1100px]:m-0`}>
        <SalesCard data={data?.sales ?? null} loading={loading} />
      </div>
      <div className={`${SLOT} [grid-area:stock]`}>
        <StockCard data={data?.stock ?? null} loading={loading} />
      </div>
      <div className={`${LINK} [grid-area:c1]`}>
        <Connector side="left" direction="down" />
      </div>
      <div className={`${LINK} [grid-area:c2]`}>
        <Connector side="left" direction="up" />
      </div>
      <div className="relative z-1 [grid-area:orb] max-[1100px]:mb-7.5 max-[1100px]:justify-self-center">
        <AiOrb />
      </div>
      <div className={`${LINK} [grid-area:c3]`}>
        <Connector side="right" direction="down" />
      </div>
      <div className={`${LINK} [grid-area:c4]`}>
        <Connector side="right" direction="up" />
      </div>
      <div className={`${SLOT} [grid-area:hr] mr-10 max-[1100px]:m-0`}>
        <HrCard data={data?.hr ?? null} loading={loading} />
      </div>
      <div className={`${SLOT} [grid-area:issues]`}>
        <IssuesCard issues={data?.issues ?? []} loading={loading} />
      </div>
    </div>
  )
}
