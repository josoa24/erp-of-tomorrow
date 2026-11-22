import type { InteractionRequest, InteractionType } from '../types/interaction'

export interface InteractionFormValues {
  type: InteractionType
  summary: string
  date: string
}

export type InteractionFormErrors = Partial<Record<'summary' | 'date', string>>

export const SUMMARY_MAX_LENGTH = 2000

const MIDDAY_IN_ANTANANARIVO = 'T12:00:00+03:00'

export const emptyInteractionForm = (today: string): InteractionFormValues => ({ type: 'APPEL', summary: '', date: today })

export const validateInteractionForm = (values: InteractionFormValues, today: string): InteractionFormErrors => ({
  ...(values.summary.trim() === '' && { summary: 'Résumez l’échange en quelques mots.' }),
  ...((values.date === '' || values.date > today) && { date: 'Choisissez aujourd’hui ou une date passée.' }),
})

export const toInteractionRequest = (values: InteractionFormValues, today: string): InteractionRequest => ({
  type: values.type,
  summary: values.summary.trim(),
  ...(values.date !== today && { occurredAt: new Date(`${values.date}${MIDDAY_IN_ANTANANARIVO}`).toISOString() }),
})
