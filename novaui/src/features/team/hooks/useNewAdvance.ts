import { EMPTY_ADVANCE_FORM, toAdvanceRequest, validateAdvanceAmount } from '../services/advanceForm'
import { useRequestAdvance } from './teamMutations'
import { useTeamForm } from './useTeamForm'

export const useNewAdvance = (employeeId: number, onDone: () => void) => {
  const form = useTeamForm(
    EMPTY_ADVANCE_FORM,
    (values) => ({ amount: validateAdvanceAmount(values.amount), reason: undefined }),
    (values) => toAdvanceRequest(employeeId, values),
  )
  const { mutate, isPending } = useRequestAdvance()

  const submit = () => {
    const request = form.prepare()
    if (request) mutate(request, { onSuccess: onDone })
  }

  return { ...form, submit, saving: isPending }
}
