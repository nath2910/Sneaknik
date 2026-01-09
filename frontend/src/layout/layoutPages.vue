<template>
  <div class="min-h-screen flex flex-col text-gray-900 font-poppins">
    <!-- Header -->
    <header class="sticky top-0 z-40 bg-gray-800 backdrop-blur border-b border-gray-700">
      <div
        class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 h-14 flex items-center justify-between gap-4"
      >
        <!-- Mobile burger -->
        <div class="flex md:hidden">
          <button
            type="button"
            @click="toggleMobileMenu"
            class="text-gray-300 hover:text-white focus:outline-none p-2"
            aria-label="Ouvrir le menu"
          >
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                v-if="!mobileMenuOpen"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"
              />
              <path
                v-else
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        </div>

        <!-- Desktop nav (inchangé) -->
        <nav class="hidden md:flex flex-1 items-center justify-center gap-6">
          <RouterLink :to="'/'" class="nav" :class="isActive('/')">Accueil</RouterLink>
          <RouterLink :to="'/stats'" class="nav" :class="isActive('/stats')">Stats</RouterLink>
          <RouterLink :to="'/gestion'" class="nav" :class="isActive('/gestion')"
            >Gestion</RouterLink
          >
        </nav>

        <!-- User menu -->
        <div class="relative" @click.stop>
          <button
            type="button"
            @click.stop="toggleMenu"
            class="ml-4 h-9 w-9 rounded-full bg-slate-800 flex items-center justify-center border border-slate-600 hover:border-purple-400 focus:outline-none"
            aria-label="Menu utilisateur"
          >
            <span class="text-sm font-semibold">{{ initials }}</span>
          </button>

          <!-- Pop-up -->
          <div
            v-if="menuOpen"
            class="absolute right-0 mt-2 w-56 rounded-lg bg-white text-slate-900 shadow-lg border border-purple-900 z-50"
          >
            <div class="px-4 py-3 border-b border-slate-100">
              <p class="text-sm text-purple-900">
                {{ currentUser ? 'Connecté' : 'Non connecté' }}
              </p>
              <p v-if="currentUser" class="text-sm font-medium text-slate-900 truncate">
                Bienvenue {{ currentUser.firstName }}
              </p>
            </div>

            <div
              class="py-2 [&_button]:w-full [&_button]:text-left [&_button]:px-4 [&_button]:py-2 [&_button]:text-sm [&_button]:transition-colors [&_button:hover]:bg-slate-200 [&_button:active]:bg-slate-200"
            >
              <!-- si pas connecté -->
              <button v-if="!currentUser" type="button" @click="goToAuth('login')">
                Se connecter
              </button>

              <button v-if="!currentUser" type="button" @click="goToAuth('signup')">
                Créer un compte
              </button>

              <!-- si connecté -->
              <button v-if="currentUser" type="button" @click="goToAccount">
                Gérer mon compte
              </button>

              <button
                v-if="currentUser"
                type="button"
                @click="logout"
                class="text-red-600 [&:hover]:bg-red-50 [&:active]:bg-red-100"
              >
                Se déconnecter
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Mobile menu -->
      <div v-if="mobileMenuOpen" class="md:hidden bg-gray-800 border-b border-gray-700">
        <div class="px-2 pt-2 pb-3 space-y-1 sm:px-3">
          <RouterLink
            :to="'/'"
            class="block px-3 py-2 rounded-md text-base font-medium"
            :class="isActiveMobile('/')"
            >Accueil</RouterLink
          >
          <RouterLink
            :to="'/stats'"
            class="block px-3 py-2 rounded-md text-base font-medium"
            :class="isActiveMobile('/stats')"
            >Stats</RouterLink
          >
          <RouterLink
            :to="'/gestion'"
            class="block px-3 py-2 rounded-md text-base font-medium"
            :class="isActiveMobile('/gestion')"
            >Gestion</RouterLink
          >
        </div>
      </div>
    </header>

    <!-- body -->
    <main class="min-h-0 flex-1 px-4 bg-gray-900">
      <div class="min-h-full max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <slot />
      </div>
    </main>

    <!-- Footer -->
    <footer class="border-t bg-gray-800 text-white">
      <div
        class="max-w-screen-2xl mx-auto h-14 px-4 sm:px-6 lg:px-8 flex items-center justify-between text-sm"
      >
        <span class="font-jetbrains-mono">&copy; {{ new Date().getFullYear() }} — Sneaknik</span>
        <div class="flex items-center gap-4">
          <a href="#" class="hover:underline">À propos</a>
          <a href="mailto:nathantalvasson@gmail.com" class="hover:underline">contact</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '@/store/authStore'

const route = useRoute()
const router = useRouter()

const menuOpen = ref(false)
const mobileMenuOpen = ref(false)

const auth = useAuthStore()
const currentUser = auth.user

const initials = computed(() => {
  if (!currentUser.value) return '👤'
  if (!currentUser.value.firstName && !currentUser.value.lastName) return '👤'
  const f = currentUser.value.firstName?.[0] || ''
  const l = currentUser.value.lastName?.[0] || ''
  return (f + l || '👤').toUpperCase()
})

const isActive = (path) => (route.path === path ? 'text-purple-400 font-semibold' : '')

const isActiveMobile = (path) =>
  route.path === path
    ? 'bg-gray-900 text-purple-400'
    : 'text-gray-300 hover:bg-gray-700 hover:text-white'

const closeMenus = () => {
  menuOpen.value = false
  mobileMenuOpen.value = false
}

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value
  // si on ouvre user menu, on ferme mobile
  if (menuOpen.value) mobileMenuOpen.value = false
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
  // si on ouvre mobile, on ferme user menu
  if (mobileMenuOpen.value) menuOpen.value = false
}

const onWindowClick = () => closeMenus()
const onKeyDown = (e) => {
  if (e.key === 'Escape') closeMenus()
}

onMounted(() => {
  window.addEventListener('click', onWindowClick)
  window.addEventListener('keydown', onKeyDown)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', onWindowClick)
  window.removeEventListener('keydown', onKeyDown)
})

watch(
  () => route.fullPath,
  () => closeMenus(),
)

const goToAuth = (mode) => {
  menuOpen.value = false
  router.push({ name: 'auth', query: { mode } })
}

const goToAccount = () => {
  menuOpen.value = false
  router.push({ name: 'account' })
}

const logout = () => {
  auth.logout()
  menuOpen.value = false
  router.push({ name: 'auth', query: { mode: 'login' } })
}
</script>

<style></style>
