import { httpGet, httpPost, type RequestOptions } from '../../../services/api/httpClient'
import { buildQuery } from '../../../services/api/queryString'
import type { Contact, ContactCounts, ContactQuery, ContactSummary, NewContactRequest } from '../types/contact'

export const CONTACTS_PATH = '/api/crm/contacts'

const onlyWhenTrue = (flag?: boolean): true | undefined => (flag ? true : undefined)

export const getContacts = (query: ContactQuery, options: RequestOptions = {}): Promise<Contact[]> =>
  httpGet<Contact[]>(
    `${CONTACTS_PATH}${buildQuery({
      kind: query.kind,
      search: query.search,
      followUp: onlyWhenTrue(query.followUp),
      professional: onlyWhenTrue(query.professional),
    })}`,
    options,
  )

export const getContactCounts = (search: string, options: RequestOptions = {}): Promise<ContactCounts> =>
  httpGet<ContactCounts>(`${CONTACTS_PATH}/counts${buildQuery({ search })}`, options)

export const getContactSummary = (options: RequestOptions = {}): Promise<ContactSummary> =>
  httpGet<ContactSummary>(`${CONTACTS_PATH}/summary`, options)

export const createContact = (request: NewContactRequest): Promise<Contact> =>
  httpPost<Contact>(CONTACTS_PATH, request)
