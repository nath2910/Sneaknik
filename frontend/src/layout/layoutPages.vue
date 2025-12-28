<template>
  <div class="min-h-screen flex flex-col text-gray-900 font-poppins">
    <!-- Header -->
    <header class="sticky top-0 z-40 bg-gray-800 backdrop-blur border-b">
      <div class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 h-14 flex gap-4">
        <!-- Desktop nav -->
        <nav class="flex flex-1 items-center justify-center gap-6">
          <RouterLink :to="'/'" class="nav" :class="isActive('/')">Accueil</RouterLink>
          <RouterLink :to="'/stats'" class="nav" :class="isActive('/stats')">Stats</RouterLink>
          <RouterLink :to="'/gestion'" class="nav" :class="isActive('/gestion')">
            Gestion
          </RouterLink>
        </nav>

        <!-- Icône user -->
        <div class="relative" @click.stop>
          <button
            @click.stop="toggleMenu"
            class="ml-4 h-9 w-9 rounded-full bg-slate-800 flex items-center justify-center border border-slate-600 hover:border-purple-400 focus:outline-none"
          >
            <span class="text-sm font-semibold">
              {{ initials }}
            </span>
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

            <div class="py-2">
              <!-- si pas connecté -->
              <button
                v-if="!currentUser"
                @click="goToAuth('login')"
                class="w-full text-left px-4 py-2 text-sm hover:bg-slate-100"
              >
                Se connecter
              </button>

              <button
                v-if="!currentUser"
                @click="goToAuth('signup')"
                class="w-full text-left px-4 py-2 text-sm hover:bg-slate-100"
              >
                Créer un compte
              </button>

              <!-- si connecte -->
              <button
                v-if="currentUser"
                @click="goToAccount"
                class="w-full text-left px-4 py-2 text-sm hover:bg-slate-100"
              >
                Gérer mon compte
              </button>

              <button
                v-if="currentUser"
                @click="logout"
                class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
              >
                Se déconnecter
              </button>
            </div>
          </div>
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
import { useAuthStore } from '@/store/AuthStore'

const route = useRoute()
const router = useRouter()
const menuOpen = ref(false)
const auth = useAuthStore()
const currentUser = auth.user

// Fermeture globale du menu
const closeMenu = () => {
  menuOpen.value = false
}

// Fermer quand on clique n'importe où dans la fenêtre
onMounted(() => {
  window.addEventListener('click', closeMenu)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', closeMenu)
})

// Fermer le menu quand la route change
watch(
  () => route.fullPath,
  () => {
    menuOpen.value = false
  },
)

const initials = computed(() => {
  if (!currentUser.value) return '👤'
  if (!currentUser.value.firstName && !currentUser.value.lastName) return '👤'
  const f = currentUser.value.firstName?.[0] || ''
  const l = currentUser.value.lastName?.[0] || ''
  return (f + l || '👤').toUpperCase()
})

const isActive = (path) => {
  return route.path === path ? 'text-purple-400 font-semibold' : ''
}

const toggleMenu = () => {
  menuOpen.value = !menuOpen.value
}
// aller vers la page de connexion/création de compte
const goToAuth = (mode) => {
  menuOpen.value = false
  router.push({ name: 'auth', query: { mode } })
}

// aller vers la page compte
const goToAccount = () => {
  menuOpen.value = false
  router.push({ name: 'account' })
}

const logout = () => {
  auth.logout() // ✅ existe
  menuOpen.value = false
  router.push({ name: 'auth', query: { mode: 'login' } }) // optionnel
}
</script>

<style></style>
