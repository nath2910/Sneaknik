import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/authStore.js'

// Lazy-load des pages
const HomePage = () => import('@/pages/homePage.vue')
const StatsPage = () => import('@/pages/statsPage.vue')
const GestionPage = () => import('@/pages/gestionPage.vue')
const AuthPage = () => import('@/pages/AuthPage.vue')
const AccountPage = () => import('@/pages/accountPage.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage, meta: { requiresAuth: true } },

    // ✅ pages protégées
    { path: '/stats', name: 'stats', component: StatsPage, meta: { requiresAuth: true } },
    { path: '/gestion', name: 'gestion', component: GestionPage, meta: { requiresAuth: true } },
    { path: '/compte', name: 'account', component: AccountPage, meta: { requiresAuth: true } },

    // auth
    { path: '/auth', name: 'auth', component: AuthPage },

    { path: '/:pathMatch(.*)*', name: 'not-found', component: HomePage },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

/**
 * ✅ Guard global: si route protégée et pas de token => redirect login
 */
router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.token.value) {
    return { name: 'auth', query: { mode: 'login' } }
  }

  // optionnel: si déjà loggé et tu vas sur /auth -> renvoie home
  if (to.name === 'auth' && auth.token.value) {
    return { name: 'home' }
  }
})

export default router
