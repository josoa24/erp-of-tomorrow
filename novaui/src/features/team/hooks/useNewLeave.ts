import { EMPTY_LEAVE_FORM, toLeaveRequest, validateLeaveForm } from '../services/leaveForm'
import { useRequestLeave } from './teamMutations'
import { useTeamForm } from './useTeamForm'

export const useNewLeave = (employeeId: number, onDone: () => void) => {
  const form = useTeamForm(EMPTY_LEAVE_FORM, validateLeaveForm, (values) => toLeaveRequest(employeeId, values))
  const { mutate, isPending } = useRequestLeave()

  const submit = () => {
    const request = form.prepare()
    if (request) mutate(request, { onSuccess: onDone })
  }

  return { ...form, submit, saving: isPending }
}
