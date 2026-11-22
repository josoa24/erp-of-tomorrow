import type { VitePWAOptions } from 'vite-plugin-pwa'

// Couleurs de la marque : nuit NOVA (--color-surface-night) et fond (--color-bg-canvas).
const NIGHT = '#17123a'
const CANVAS = '#f3f0fb'

// Toute réponse du backend passe par /api : jamais en cache, jamais remplacée par le shell.
export const API_PATH = /^\/api(\/|$)/

export const pwaOptions: Partial<VitePWAOptions> = {
  registerType: 'autoUpdate',
  // Les icônes de public/ sont déjà précachées par globPatterns : pas de doublon.
  includeManifestIcons: false,
  manifest: {
    id: '/',
    name: 'NOVA',
    short_name: 'NOVA',
    description: 'Espace de gestion intelligent pour les entreprises',
    lang: 'fr',
    start_url: '/',
    scope: '/',
    display: 'standalone',
    theme_color: NIGHT,
    background_color: CANVAS,
    icons: [
      { src: 'pwa-192x192.png', sizes: '192x192', type: 'image/png', purpose: 'any' },
      { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png', purpose: 'any' },
      { src: 'maskable-icon-512x512.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' },
      { src: 'icon.svg', sizes: 'any', type: 'image/svg+xml', purpose: 'any' },
    ],
  },
  workbox: {
    // Précache du shell statique uniquement ; aucune règle runtimeCaching : rien d'autre n'est mis en cache.
    globPatterns: ['**/*.{js,css,html,svg,png,woff2}'],
    navigateFallback: 'index.html',
    navigateFallbackDenylist: [API_PATH],
    cleanupOutdatedCaches: true,
    clientsClaim: true,
    skipWaiting: true,
  },
}
