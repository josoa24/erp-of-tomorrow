import { EMPTY_EMPLOYEE_FORM, toEmployeeRequest, validateEmployeeForm, type EmployeeFormValues } from '../employeeForm'

const TODAY = '2026-11-22'

const filled: EmployeeFormValues = {
  firstName: ' Rasoa ',
  lastName: 'Andria',
  email: 'rasoa.andria@boutique-tana.mg',
  position: '',
  departmentId: '2',
  contractType: 'CDD',
  hireDate: '2026-11-22',
  baseSalary: '650 000',
}

describe('employeeForm', () => {
  it('shouldRequireMandatoryFields_whenEmpty', () => {
    const errors = validateEmployeeForm(EMPTY_EMPLOYEE_FORM, TODAY)

    expect(errors).toMatchObject({
      firstName: 'Champ obligatoire',
      lastName: 'Champ obligatoire',
      email: 'Champ obligatoire',
      departmentId: 'Champ obligatoire',
      hireDate: 'Champ obligatoire',
      baseSalary: 'Champ obligatoire',
    })
    expect(errors.position).toBeUndefined()
  })

  it('shouldAcceptArrivalToday', () => {
    expect(Object.values(validateEmployeeForm(filled, TODAY)).filter(Boolean)).toEqual([])
  })

  it('shouldRefuseArrivalInTheFuture', () => {
    expect(validateEmployeeForm({ ...filled, hireDate: '2026-11-23' }, TODAY).hireDate).toMatch(/aujourd/)
  })

  it('shouldRefuseMalformedEmailAndSalary', () => {
    const errors = validateEmployeeForm({ ...filled, email: 'rasoa', baseSalary: '-5' }, TODAY)

    expect(errors.email).toMatch(/invalide/)
    expect(errors.baseSalary).toMatch(/montant/)
  })

  it('shouldBuildRequest_withTrimmedValuesAndOptionalFieldsOmitted', () => {
    expect(toEmployeeRequest({ ...filled, contractType: '' })).toEqual({
      firstName: 'Rasoa',
      lastName: 'Andria',
      email: 'rasoa.andria@boutique-tana.mg',
      position: undefined,
      departmentId: 2,
      hireDate: '2026-11-22',
      baseSalary: 650000,
      contractType: undefined,
    })
  })
})
