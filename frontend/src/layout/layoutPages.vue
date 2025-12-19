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
            <img
              v-if="avatarUrl"
              :src="avatarUrl"
              alt="User"
              class="h-8 w-8 rounded-full object-cover"
            />
            <span v-else class="text-sm font-semibold">
              {{ initials }}
            </span>
          </button>

          <!-- Pop-up -->
          <div
            v-if="menuOpen"
            class="absolute right-0 mt-2 w-56 rounded-lg bg-white text-slate-900 shadow-lg border border-slate-200 z-50"
          >
            <div class="px-4 py-3 border-b border-slate-100">
              <p class="text-sm text-slate-500">
                {{ currentUser ? 'Connecté en tant que' : 'Non connecté' }}
              </p>
              <p v-if="currentUser" class="text-sm font-medium text-slate-900 truncate">
                {{ currentUser.email }}
              </p>
            </div>

            <div class="py-2">
              <!-- 👉 seulement si PAS connecté -->
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

              <!-- 👉 seulement si CONNECTÉ -->
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

      <!-- Mobile drawer -->
      <transition name="fade">
        <div v-show="open" class="md:hidden border-t bg-white">
          <nav class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-3 space-y-1">
            <RouterLink
              @click="open = false"
              :to="'/'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/')"
            >
              Accueil
            </RouterLink>
            <RouterLink
              @click="open = false"
              :to="'/stats'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/stats')"
            >
              Stats
            </RouterLink>
            <RouterLink
              @click="open = false"
              :to="'/gestion'"
              class="block px-3 py-2 rounded hover:bg-gray-50"
              :class="isActive('/gestion')"
            >
              Gestion
            </RouterLink>
            <div class="h-px bg-gray-200 my-2"></div>
            <RouterLink
              @click="open = false"
              :to="{ name: 'auth', query: { mode: 'login' } }"
              class="block px-3 py-2 rounded hover:bg-gray-50"
            >
              Login
            </RouterLink>
            <RouterLink
              @click="open = false"
              :to="{ name: 'auth', query: { mode: 'signup' } }"
              class="block px-3 py-2 rounded hover:bg-gray-50"
            >
              Créer un compte
            </RouterLink>
          </nav>
        </div>
      </transition>
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

// store global d'auth
const { user, setUser } = useAuthStore()
const currentUser = user // alias réactif

// ...

const menuOpen = ref(false)
const open = ref(false) // mobile drawer

// avatar custom (laisse vide pour l’instant)
const avatarUrl = ref('')

// Fermeture globale du menu
const closeMenu = () => {
  menuOpen.value = false
}

// Fermer quand on clique n'importe où dans la fenêtre (sauf à l'intérieur du menu grâce à @click.stop)
onMounted(() => {
  window.addEventListener('click', closeMenu)
})

onBeforeUnmount(() => {
  window.removeEventListener('click', closeMenu)
})

// Fermer le menu quand la route change (tu vas sur /auth, /stats, etc.)
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
  setUser(null)
  menuOpen.value = false
}
</script>

<style></style>
