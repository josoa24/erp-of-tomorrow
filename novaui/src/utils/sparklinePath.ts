export const buildSparklinePath = (values: number[], width: number, height: number, padding = 3): string => {
  if (values.length === 0) return ''
  const points = values.length === 1 ? [values[0], values[0]] : values
  const min = Math.min(...points)
  const range = Math.max(...points) - min || 1
  const stepX = (width - padding * 2) / (points.length - 1)
  const usableHeight = height - padding * 2

  return points
    .map((value, index) => {
      const x = padding + index * stepX
      const y = padding + usableHeight - ((value - min) / range) * usableHeight
      return `${index === 0 ? 'M' : 'L'}${x.toFixed(1)} ${y.toFixed(1)}`
    })
    .join(' ')
}
