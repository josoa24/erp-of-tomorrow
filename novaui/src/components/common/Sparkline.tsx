import { buildSparklinePath } from '../../utils/sparklinePath'

interface SparklineProps {
  values: number[]
  width?: number
  height?: number
  label: string
}

export const Sparkline = ({ values, width = 132, height = 84, label }: SparklineProps) => {
  const path = buildSparklinePath(values, width, height)
  if (!path) return null

  return (
    <svg
      className="block overflow-visible drop-shadow-[0_0_6px_rgba(45,212,191,0.75)]"
      width={width}
      height={height}
      viewBox={`0 0 ${width} ${height}`}
      role="img"
      aria-label={label}
    >
      <defs>
        <linearGradient id="sparkline-stroke" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stopColor="#14b8a6" />
          <stop offset="100%" stopColor="#5eead4" />
        </linearGradient>
      </defs>
      <path d={path} fill="none" stroke="url(#sparkline-stroke)" strokeWidth={2.4} strokeLinejoin="round" />
    </svg>
  )
}
