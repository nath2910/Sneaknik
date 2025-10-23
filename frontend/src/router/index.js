import { createRouter, createWebHistory } from 'vue-router'

// Lazy-load des pages
const HomePage = () => import('@/pages/homePage.vue')
const StatsPage = () => import('@/pages/statsPage.vue')
const GestionPage = () => import('@/pages/gestionPage.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/stats', name: 'stats', component: StatsPage },
    { path: '/gestion', name: 'gestion', component: GestionPage },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: HomePage }, // simple: renvoie à l'accueil
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router

//Déclare les routes (URL ↔ composant). Par ex. /ventes → snkVente.vue.
