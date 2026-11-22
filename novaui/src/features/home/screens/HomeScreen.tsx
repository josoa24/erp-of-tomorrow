import { useState } from 'react'
import { EclipseBar } from '../../../components/eclipse/EclipseBar'
import { KpiStrip } from '../../../components/module/KpiStrip'
import { ECLIPSE_UNAVAILABLE_LABEL } from '../../../constants/eclipsePlaceholders'
import { useCurrentUser } from '../../../hooks/useCurrentUser'
import { DecisionList } from '../components/DecisionList'
import { OrderDialog } from '../components/OrderDialog'
import { useDecisionActions } from '../hooks/useDecisionActions'
import { useHomeData } from '../hooks/useHomeData'
import { leavesDecision, orderDecision, suggestionDecision } from '../services/homeDecisions'
import { attentionLine, firstName } from '../services/homeGreeting'
import { SUGGESTION_MODULES } from '../services/suggestionRoutes'
import { canAccessModule } from '../../../utils/roleAccess'
import { homeIndicators } from '../services/homeIndicatorSet'

export const HomeScreen = () => {
  const user = useCurrentUser()
  const [question, setQuestion] = useState('')
  const data = useHomeData(user?.role ?? 'CRM')
  const replenishment = data.replenishment.data?.[0]
  const allowed = (data.suggestions.data ?? []).filter((item) => {
    const module = SUGGESTION_MODULES[item.moduleKey]
    return module !== undefined && canAccessModule(user?.role ?? 'CRM', module)
  })
  const suggestion = allowed[0]
  const decisions = [
    ...orderDecision(replenishment),
    ...leavesDecision(data.leaves.data),
    ...suggestionDecision(allowed),
  ]
  const { act, confirming, closeConfirm } = useDecisionActions(replenishment, suggestion)
  if (!user) return null
  const indicators = homeIndicators(user.role, data)

  return (
    <div className="mx-auto flex w-full max-w-[960px] flex-col gap-6">
      <header className="flex flex-col gap-2">
        <h1 className="text-display-l text-ink-primary">{`Bonjour, ${firstName(user.displayName)}.`}</h1>
        <p className="text-body text-ink-secondary">{attentionLine(decisions.length)}</p>
      </header>
      <EclipseBar
        variant="night"
        placeholder="Demandez, adaptez un écran ou créez un module…"
        value={question}
        onChange={setQuestion}
        onSubmit={() => setQuestion('')}
        status="unavailable"
        unavailableLabel={ECLIPSE_UNAVAILABLE_LABEL}
      />
      {indicators.length > 0 && <KpiStrip items={indicators} label="Indicateurs du jour" />}
      <DecisionList decisions={decisions} onAct={act} />
      {confirming && replenishment && <OrderDialog item={replenishment} onClose={closeConfirm} />}
    </div>
  )
}
