import type { AdaptationActivation } from '../../types/adaptation'

export const LOTS_ACTIVATION: AdaptationActivation = {
  moduleKey: 'stock',
  adaptationKey: 'stock.lots-peremption',
  activatedBy: 1,
  activatedAt: '2026-11-22T06:00:00Z',
}

export const ADMIN_SESSION = { token: 't', email: 'hery.rabe@boutique-tana.mg', role: 'ADMIN' } as const
