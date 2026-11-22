import { describe, expect, it } from 'vitest'
import { API_PATH, pwaOptions } from './pwa.config'

describe('pwaOptions', () => {
  it('décrit NOVA comme une application installable en plein écran', () => {
    const manifest = pwaOptions.manifest || {}
    expect(manifest.name).toBe('NOVA')
    expect(manifest.short_name).toBe('NOVA')
    expect(manifest.display).toBe('standalone')
    expect(manifest.start_url).toBe('/')
    expect(manifest.scope).toBe('/')
  })

  it('fournit les icônes 192, 512 et maskable', () => {
    const icons = (pwaOptions.manifest || {}).icons ?? []
    const sizes = icons.map((icon) => `${icon.sizes}:${icon.purpose}`)
    expect(sizes).toEqual(expect.arrayContaining(['192x192:any', '512x512:any', '512x512:maskable']))
  })

  it('ne met aucune réponse en cache à l’exécution', () => {
    expect(pwaOptions.workbox?.runtimeCaching).toBeUndefined()
  })

  it('ne précache que des ressources statiques', () => {
    const patterns = pwaOptions.workbox?.globPatterns ?? []
    expect(patterns.join(',')).not.toMatch(/json/)
  })

  it('exclut /api du repli de navigation', () => {
    expect(pwaOptions.workbox?.navigateFallbackDenylist).toContain(API_PATH)
    expect(API_PATH.test('/api')).toBe(true)
    expect(API_PATH.test('/api/auth/login')).toBe(true)
    expect(API_PATH.test('/apis-externes')).toBe(false)
    expect(API_PATH.test('/stock')).toBe(false)
  })
})
