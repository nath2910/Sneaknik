import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/authStore.js'

const HomePage = () => import('@/pages/homePage.vue')
const StatsPage = () => import('@/pages/statsPage.vue')
const GestionPage = () => import('@/pages/gestionPage.vue')
const AuthPage = () => import('@/pages/AuthPage.vue')
const AccountPage = () => import('@/pages/accountPage.vue')
const AuthCallbackPage = () => import('@/pages/authCallbackPage.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ✅ callback OAuth en premier (avant le catch-all)
    { path: '/auth/callback', name: 'authCallback', component: AuthCallbackPage },

    // auth
    { path: '/auth', name: 'auth', component: AuthPage },

    // ✅ pages protégées, required auth permet de bloque des pages si pas connecte
    { path: '/', name: 'home', component: HomePage, meta: { requiresAuth: true } },
    {
      path: '/stats',
      name: 'stats',
      component: StatsPage,
      meta: { fullBleed: true, requiresAuth: true },
    },
    { path: '/gestion', name: 'gestion', component: GestionPage, meta: { requiresAuth: true } },
    { path: '/compte', name: 'account', component: AccountPage, meta: { requiresAuth: true } },

    // ✅ toujours en dernier
    { path: '/:pathMatch(.*)*', name: 'not-found', redirect: '/' },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth && !auth.token.value) {
    return { name: 'auth', query: { mode: 'login' } }
  }

  if (to.name === 'auth' && auth.token.value) {
    return { name: 'home' }
  }
})

export default router
