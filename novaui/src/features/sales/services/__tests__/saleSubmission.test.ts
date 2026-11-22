import { createSale, updateSale, validateSale } from '../../api/salesApi'
import { SALE, saleWith } from '../../test/saleFixtures'
import { submissionMessage, submitSale } from '../saleSubmission'

vi.mock('../../api/salesApi', () => ({ createSale: vi.fn(), updateSale: vi.fn(), validateSale: vi.fn() }))

const REQUEST = { customerId: null, lines: [{ productId: 7, quantity: 1 }], toDeliver: false }
const DRAFT = saleWith({ status: 'DRAFT', paymentStatus: 'DRAFT' })

describe('submitSale', () => {
  afterEach(() => vi.clearAllMocks())

  it('shouldCreateDraftOnly_whenNotValidating', async () => {
    vi.mocked(createSale).mockResolvedValue(DRAFT)

    await expect(submitSale({ saleId: null, request: REQUEST, validate: false })).resolves.toBe(DRAFT)
    expect(validateSale).not.toHaveBeenCalled()
  })

  it('shouldCreateThenValidate_whenValidating', async () => {
    vi.mocked(createSale).mockResolvedValue(DRAFT)
    vi.mocked(validateSale).mockResolvedValue(SALE)

    await expect(submitSale({ saleId: null, request: REQUEST, validate: true })).resolves.toBe(SALE)
    expect(validateSale).toHaveBeenCalledWith(412)
  })

  it('shouldUpdateExistingDraft', async () => {
    vi.mocked(updateSale).mockResolvedValue(DRAFT)

    await submitSale({ saleId: 412, request: REQUEST, validate: false })

    expect(updateSale).toHaveBeenCalledWith(412, REQUEST)
    expect(createSale).not.toHaveBeenCalled()
  })

  it('shouldDescribeOutcome', () => {
    expect(submissionMessage(SALE, { validate: true })).toBe('Vente V-0412 validée')
    expect(submissionMessage(SALE, { validate: false })).toBe('Brouillon V-0412 enregistré')
  })
})
