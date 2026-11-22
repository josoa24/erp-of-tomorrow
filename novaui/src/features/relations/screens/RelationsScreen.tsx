import { useMemo } from 'react'
import { DataTable } from '../../../components/module/DataTable'
import { ListCard } from '../../../components/module/ListCard'
import { ModulePage } from '../../../components/module/ModulePage'
import { Button } from '../../../components/ui/button'
import { contactActions, contactKey } from '../components/contactActions'
import { contactColumns } from '../components/contactColumns'
import { ContactsEmptyState } from '../components/ContactsEmptyState'
import { RelationsFilters } from '../components/RelationsFilters'
import { useContactCounts } from '../hooks/useContactCounts'
import { useContactDialogs } from '../hooks/useContactDialogs'
import { useContacts } from '../hooks/useContacts'
import { useContactSummary } from '../hooks/useContactSummary'
import { useRelationsSearch } from '../hooks/useRelationsSearch'
import { toContactQuery, visibleFilter } from '../services/contactQuery'
import { relationsKpis, relationsSubtitle } from '../services/relationsKpis'
import { GENERIC_VIEW } from '../types/adaptationView'
import { RelationsDialogs } from './RelationsDialogs'

export const RelationsScreen = () => {
  const adaptation = GENERIC_VIEW
  const search = useRelationsSearch()
  const filter = visibleFilter(search.filter, adaptation.visible)
  const { contacts, loading } = useContacts(toContactQuery(filter, search.query))
  const counts = useContactCounts(search.query)
  const summary = useContactSummary()
  const { dialog, openCreate, openInteraction, openTimeline, close } = useContactDialogs()
  const columns = useMemo(() => contactColumns(adaptation), [adaptation])
  const rowActions = useMemo(() => contactActions(openInteraction, openTimeline), [openInteraction, openTimeline])

  return (
    <ModulePage
      title="Relations"
      subtitle={relationsSubtitle(summary)}
      search={{ placeholder: 'Rechercher un contact…', defaultValue: search.query, onSearch: search.setQuery }}
      primaryAction={<Button onClick={openCreate}>Nouveau contact</Button>}
      kpis={relationsKpis(summary, adaptation)}
      kpiLabel="Indicateurs des relations"
    >
      <ListCard
        label="Contacts"
        toolbar={<RelationsFilters active={filter} counts={counts} adaptation={adaptation} onChange={search.setFilter} />}
      >
        <DataTable
          caption="Contacts triés par dernier échange"
          columns={columns}
          rows={contacts}
          getRowKey={contactKey}
          getRowLabel={(contact) => contact.name}
          rowActions={rowActions}
          loading={loading}
          empty={<ContactsEmptyState query={search.query} filtered={filter !== undefined} onCreate={openCreate} />}
        />
      </ListCard>
      <RelationsDialogs dialog={dialog} adaptation={adaptation} onRecord={openInteraction} onClose={close} />
    </ModulePage>
  )
}
