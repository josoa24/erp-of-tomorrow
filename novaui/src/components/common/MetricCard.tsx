import type { LucideIcon } from 'lucide-react'
import type { ReactNode } from 'react'
import { GlassCard } from './GlassCard'

interface MetricCardProps {
  icon: LucideIcon
  label: string
  value: ReactNode
  caption?: string
  badge?: ReactNode
  chart?: ReactNode
}

export const MetricCard = ({ icon: Icon, label, value, caption, badge, chart }: MetricCardProps) => (
  <GlassCard className="grid min-h-43 w-full grid-cols-[74px_minmax(0,1fr)] items-start gap-x-6 px-5.5 pt-5 pb-5.5 max-sm:grid-cols-[56px_1fr] max-sm:gap-x-4.5">
    <span className="grid size-18.5 place-items-center rounded-2xl border border-white/8 bg-linear-[145deg] from-white/10 to-white/3 text-[#eceeff] shadow-[0_8px_20px_rgba(0,0,0,0.25)] max-sm:size-14">
      <Icon size={34} strokeWidth={1.4} aria-hidden="true" />
    </span>
    <div className="min-w-0 pt-1">
      <h2 className="text-[25px] font-medium max-sm:text-xl">{label}</h2>
      <p className="mt-1.5 text-[38px] font-semibold tracking-[-0.01em] whitespace-nowrap max-sm:text-[30px]">
        {value}
      </p>
      {(caption || badge) && (
        <div className="mt-1.5 flex flex-wrap items-center justify-between gap-x-4 gap-y-2.5">
          {caption && <span className="text-lg text-[#e3e4ff]">{caption}</span>}
          {badge}
        </div>
      )}
    </div>
    {chart && <div className="col-start-3 row-start-1 mt-4.5 self-center max-sm:hidden">{chart}</div>}
  </GlassCard>
)
