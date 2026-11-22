export const TEAM_FILTERS = ['tous', 'presents', 'conges', 'avances'] as const
export type TeamFilter = (typeof TEAM_FILTERS)[number]
export const LEAVE_REQUESTS_VIEW = 'demandes'

export interface TeamSearch {
  filtre?: Exclude<TeamFilter, 'tous'>
  vue?: typeof LEAVE_REQUESTS_VIEW
}

const isNarrowingFilter = (value: unknown): value is Exclude<TeamFilter, 'tous'> =>
  value === 'presents' || value === 'conges' || value === 'avances'

export const validateTeamSearch = (search: Record<string, unknown>): TeamSearch => ({
  ...(isNarrowingFilter(search.filtre) && { filtre: search.filtre }),
  ...(search.vue === LEAVE_REQUESTS_VIEW && { vue: LEAVE_REQUESTS_VIEW }),
})

export const effectiveFilter = (filtre: TeamSearch['filtre'], adapted: boolean): TeamFilter => {
  if (!filtre) return 'tous'
  return filtre === 'avances' && !adapted ? 'tous' : filtre
}
