import { useEffect } from 'react'
import { clampPage } from '../services/usersSearch'

export const useClampedPage = (page: number, total: number, settled: boolean, setPage: (page: number) => void): boolean => {
  const outOfRange = settled && page !== clampPage(page, total)

  useEffect(() => {
    if (outOfRange) setPage(clampPage(page, total))
  }, [outOfRange, page, total, setPage])

  return outOfRange
}
