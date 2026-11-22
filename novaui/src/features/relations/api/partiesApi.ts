import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import type { PartyRef } from '../types/contact'
import type { Interaction, InteractionRequest, TimelineEntry } from '../types/interaction'

const partyPath = ({ kind, partyId }: PartyRef): string => `/api/crm/parties/${kind}/${partyId}`

export const recordInteraction = (party: PartyRef, request: InteractionRequest): Promise<Interaction> =>
  httpPost<Interaction>(`${partyPath(party)}/interactions`, request)

export const getTimeline = (party: PartyRef, options: RequestOptions = {}): Promise<TimelineEntry[]> =>
  httpGet<TimelineEntry[]>(`${partyPath(party)}/timeline`, options)
