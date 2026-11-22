export const ROUTES = {
  home: '/',
  login: '/connexion',
  error: '/erreur',
  sales: '/ventes',
  stock: '/stock',
  stockOrders: '/stock/commandes',
  team: '/equipe',
  relations: '/relations',
  profile: '/profil',
  customModule: '/modules/$slug',
} as const

export type ModuleRoutePath = (typeof ROUTES)['home' | 'sales' | 'stock' | 'team' | 'relations']
