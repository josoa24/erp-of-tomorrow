export const ROUTES = {
  home: '/',
  login: '/connexion',
  error: '/erreur',
  sales: '/ventes',
  stock: '/stock',
  team: '/equipe',
  relations: '/relations',
  profile: '/profil',
  users: '/utilisateurs',
  customModule: '/modules/$slug',
} as const

export type ModuleRoutePath = (typeof ROUTES)['home' | 'sales' | 'stock' | 'team' | 'relations']
