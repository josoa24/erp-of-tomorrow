export const TEAM_QUERY_KEY = ['team'] as const

export const TEAM_KEYS = {
  summary: [...TEAM_QUERY_KEY, 'summary'],
  counts: [...TEAM_QUERY_KEY, 'counts'],
  employees: (search: string) => [...TEAM_QUERY_KEY, 'employees', search],
  departments: [...TEAM_QUERY_KEY, 'departments'],
  pendingLeaves: [...TEAM_QUERY_KEY, 'leaves', 'DRAFT'],
  advanceSummary: [...TEAM_QUERY_KEY, 'advances', 'summary'],
  draftAdvances: (employeeId: number) => [...TEAM_QUERY_KEY, 'advances', employeeId, 'DRAFT'],
} as const
