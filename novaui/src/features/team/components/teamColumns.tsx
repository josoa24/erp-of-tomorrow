import type { DataTableColumn } from '../../../components/module/types'
import { EMPTY_VALUE, formatAmount, formatArrival, formatContract, formatMonthAdvance } from '../services/teamFormat'
import type { AdaptationView } from '../types/adaptationView'
import type { Employee } from '../types/employee'
import { EmployeeCell } from './EmployeeCell'
import { EmployeeStatusPill } from './EmployeeStatusPill'

const adaptedColumns = (isNew: boolean): DataTableColumn<Employee>[] => [
  {
    key: 'advance',
    header: 'Avance du mois',
    width: 190,
    isNew,
    render: (employee) => formatMonthAdvance(employee.monthAdvance, employee.monthAdvancePending),
  },
  { key: 'cap', header: 'Plafond 30 %', width: 150, isNew, render: (employee) => formatAmount(employee.advanceCap) },
]

export const teamColumns = ({ visible, highlighted }: AdaptationView): DataTableColumn<Employee>[] => [
  { key: 'employee', header: 'Employé', width: visible ? 280 : 360, render: (employee) => <EmployeeCell employee={employee} /> },
  { key: 'department', header: 'Service', width: visible ? 150 : 220, render: (employee) => employee.departmentName ?? EMPTY_VALUE },
  { key: 'contract', header: 'Contrat', width: visible ? 110 : 160, render: (employee) => formatContract(employee.contractType) },
  { key: 'arrival', header: 'Arrivée', width: visible ? 120 : 180, render: (employee) => formatArrival(employee.hireDate) },
  ...(visible ? adaptedColumns(highlighted) : []),
  { key: 'status', header: 'Statut', width: 150, render: (employee) => <EmployeeStatusPill status={employee.status} /> },
]
