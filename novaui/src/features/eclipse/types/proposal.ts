export type EclipseOutcome = 'ADAPTATION' | 'ALREADY_ACTIVE' | 'QUESTION' | 'UNSUPPORTED' | 'CREATE_MODULE'

export interface EclipseProposalResponse {
  outcome: EclipseOutcome
  adaptationKey?: string
  request: string
  title?: string
  summary?: string
  message?: string
  canApply: boolean
  targetModuleKey?: string
}
