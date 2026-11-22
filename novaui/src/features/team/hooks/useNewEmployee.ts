import { useState } from 'react'
import { toBusinessIsoDate } from '../../relations/services/businessDay'
import { EMPTY_EMPLOYEE_FORM, toEmployeeRequest, validateEmployeeForm } from '../services/employeeForm'
import { useCreateEmployee } from './teamMutations'
import { useDepartments } from './teamQueries'
import { useTeamForm } from './useTeamForm'

export const useNewEmployee = (onDone: () => void) => {
  const [today] = useState(() => toBusinessIsoDate(new Date()))
  const form = useTeamForm(EMPTY_EMPLOYEE_FORM, (values) => validateEmployeeForm(values, today), toEmployeeRequest)
  const { data: departments } = useDepartments(true)
  const { mutate, isPending } = useCreateEmployee()

  const submit = () => {
    const request = form.prepare()
    if (request) mutate(request, { onSuccess: onDone })
  }

  return { ...form, today, departments: departments ?? [], submit, saving: isPending }
}
