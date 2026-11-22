import { EclipseMark } from '../../../components/common/EclipseLogo'
import { AI_NAME, AI_STATUS, AI_TAGLINE } from '../../../constants/appInfo'
import { OrbNetwork } from './OrbNetwork'
import { OrbPlatform } from './OrbPlatform'

export const AiOrb = () => (
  <div className="relative aspect-square w-[var(--orb-size,400px)]">
    <div
      className="pointer-events-none absolute -inset-[45%] mask-[linear-gradient(195deg,#000_48%,transparent_70%)]"
      aria-hidden="true"
    >
      <div className="absolute top-[9.5%] left-[24.7%] aspect-square w-[65.3%] rounded-full bg-[radial-gradient(circle_at_42%_58%,#0a0b2c_62%,#05061c_100%)] shadow-eclipse" />
    </div>
    <OrbPlatform />
    <div className="absolute inset-0 overflow-hidden rounded-full border-2 border-[rgba(200,214,255,0.85)] bg-orb shadow-orb">
      <OrbNetwork />
      <div className="relative flex h-full flex-col items-center justify-center pt-2.5 text-center">
        <EclipseMark size={78} />
        <p className="mt-4.5 text-[31px] font-semibold tracking-[0.2em] uppercase max-sm:text-[22px]">
          {AI_NAME}
        </p>
        <p className="mt-2 text-[22px] max-sm:text-base">{AI_TAGLINE}</p>
        <p className="mt-2.5 text-[17px] text-[#dfe2ff] max-sm:text-[13px]">
          <span aria-hidden="true">·</span> {AI_STATUS} <span aria-hidden="true">·</span>
        </p>
      </div>
    </div>
  </div>
)
