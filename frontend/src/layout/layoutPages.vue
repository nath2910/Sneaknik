<template>
  <div class="h-screen flex flex-col font-poppins text-slate-100 bg-slate-950">
    <!-- Header -->
    <header
      class="z-50 transition-all duration-300 ease-out"
      :class="
        compactNav
          ? 'fixed top-3 left-0 right-0 bg-transparent border-transparent'
          : 'bg-gray-800/90 backdrop-blur border-b border-gray-700'
      "
    >
      <div
        class="max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 h-14 grid grid-cols-[auto_1fr_auto] md:grid-cols-[1fr_auto_1fr] items-center gap-4"
      >
        <!-- LEFT (mobile burger / desktop spacer) -->
        <div class="flex items-center justify-start">
          <div class="flex md:hidden">
            <button
              type="button"
              @click.stop="toggleMobileMenu"
              class="text-gray-300 hover:text-white focus:outline-none p-2 rounded-xl hover:bg-white/5 transition"
              aria-label="Ouvrir le menu"
              :aria-expanded="mobileMenuOpen"
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
        </div>

        <!-- CENTER (desktop nav) -->
        <Transition
          enter-active-class="transition duration-300 ease-out"
          enter-from-class="opacity-0 -translate-y-1 scale-95"
          enter-to-class="opacity-100 translate-y-0 scale-100"
          leave-active-class="transition duration-200 ease-in"
          leave-from-class="opacity-100 translate-y-0 scale-100"
          leave-to-class="opacity-0 -translate-y-1 scale-95"
        >
          <nav
            v-motion
            :initial="false"
            :variants="navVariants"
            :animate="compactNav ? 'compact' : 'normal'"
            :transition="navSpring"
            class="hidden md:flex items-center justify-center"
            v-show="!mobileMenuOpen"
            :class="
              compactNav
                ? 'pointer-events-auto px-2 py-1 rounded-full bg-slate-950/40 border border-white/10 shadow-[0_10px_30px_rgba(0,0,0,0.35)] backdrop-blur-md'
                : 'w-full gap-8'
            "
          >
            <RouterLink to="/" :class="compactNav ? compactLink('/') : normalLink('/')">
              Accueil
            </RouterLink>

            <RouterLink
              to="/stats"
              :class="compactNav ? compactLink('/stats') : normalLink('/stats')"
            >
              Stats
            </RouterLink>

            <RouterLink
              to="/gestion"
              :class="compactNav ? compactLink('/gestion') : normalLink('/gestion')"
            >
              Gestion
            </RouterLink>
          </nav>
        </Transition>

        <!-- RIGHT (user menu) — ✅ conservé -->
        <div class="flex items-center justify-end pointer-events-auto">
          <div class="relative" @click.stop>
            <button
              type="button"
              @click.stop="toggleUserMenu"
              class="h-9 w-9 rounded-full flex items-center justify-center border transition focus:outline-none"
              :class="
                compactNav
                  ? 'bg-gray-900/55 border-white/10 hover:border-violet-300/40 shadow-lg backdrop-blur'
                  : 'bg-slate-800 border-slate-600 hover:border-purple-400'
              "
              aria-label="Menu utilisateur"
              :aria-expanded="menuOpen"
            >
              <span class="text-sm font-semibold text-white">{{ initials }}</span>
            </button>

            <!-- Pop-up (inchangé) -->
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
                <button v-if="!currentUser" type="button" @click="goToAuth('login')">
                  Se connecter
                </button>

                <button v-if="!currentUser" type="button" @click="goToAuth('signup')">
                  Créer un compte
                </button>

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
      </div>

      <!-- Mobile menu (option esthétique sur stats : panel flottant) -->
      <div v-if="mobileMenuOpen" class="md:hidden" @click.stop>
        <div :class="compactNav ? 'px-4 pb-3' : 'bg-gray-800 border-b border-gray-700'">
          <div
            :class="
              compactNav
                ? 'mt-2 rounded-2xl bg-gray-900/70 border border-white/10 backdrop-blur p-2'
                : 'px-2 pt-2 pb-3 space-y-1 sm:px-3'
            "
          >
            <RouterLink
              to="/"
              class="block px-3 py-2 rounded-md text-base font-medium"
              :class="isActiveMobile('/')"
              @click="closeMenus"
            >
              Accueil
            </RouterLink>

            <RouterLink
              to="/stats"
              class="block px-3 py-2 rounded-md text-base font-medium"
              :class="isActiveMobile('/stats')"
              @click="closeMenus"
            >
              Stats
            </RouterLink>

            <RouterLink
              to="/gestion"
              class="block px-3 py-2 rounded-md text-base font-medium"
              :class="isActiveMobile('/gestion')"
              @click="closeMenus"
            >
              Gestion
            </RouterLink>
          </div>
        </div>
      </div>
    </header>

    <!-- Body -->
    <main class="bg-gray-900 flex-1 min-h-0">
      <!-- FULL BLEED (stats) -->
      <div v-if="route.meta.fullBleed" class="relative h-full w-full overflow-hidden">
        <slot />
      </div>

      <!-- PAGES NORMALES (scroll) -->
      <div v-else class="h-full overflow-auto">
        <div class="min-h-full max-w-screen-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <slot />
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer v-if="!route.meta.fullBleed" class="border-t bg-gray-800 text-white">
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

const compactNav = computed(() => !!route.meta.fullBleed)

// Spring “iOS-like”
const navSpring = {
  type: 'spring',
  stiffness: 420,
  damping: 42,
  mass: 0.95,
}

// Deux états (normal vs compact)
const navVariants = {
  normal: {
    opacity: 1,
    y: 0,
    scale: 1,
    filter: 'blur(0px)',
  },
  compact: {
    opacity: 1,
    y: 0,
    scale: 0.975,
    rotateZ: 0.0001, // hack micro pour lisser certaines transitions GPU
  },
}

const compactLink = (path) => {
  const active = route.path === path
  return [
    'px-4 py-2 rounded-full text-sm font-medium transition-colors duration-200',
    active ? 'bg-white/10 text-violet-200' : 'text-white/80 hover:text-white hover:bg-white/5',
  ]
}

const menuOpen = ref(false)
const mobileMenuOpen = ref(false)

const auth = useAuthStore()

const currentUser = computed(() => {
  const u = auth.user
  return u && typeof u === 'object' && 'value' in u ? u.value : u
})

const initials = computed(() => {
  const u = currentUser.value
  if (!u) return '👤'
  const f = u.firstName?.[0] || ''
  const l = u.lastName?.[0] || ''
  return (f + l || '👤').toUpperCase()
})

const isActiveMobile = (path) =>
  route.path === path
    ? 'bg-gray-900 text-purple-400'
    : 'text-gray-300 hover:bg-gray-700 hover:text-white'

const closeMenus = () => {
  menuOpen.value = false
  mobileMenuOpen.value = false
}

const toggleUserMenu = () => {
  menuOpen.value = !menuOpen.value
  if (menuOpen.value) mobileMenuOpen.value = false
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
  if (mobileMenuOpen.value) menuOpen.value = false
}

// ✅ ferme seulement si clic en dehors du header
const onWindowClick = (e) => {
  if (e.target.closest('header')) return
  closeMenus()
}

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

// ✅ ferme quand tu changes de page
watch(
  () => route.fullPath,
  () => closeMenus(),
)

const goToAuth = (mode) => {
  closeMenus()
  router.push({ name: 'auth', query: { mode } })
}

const goToAccount = () => {
  closeMenus()
  router.push({ name: 'account' })
}

const logout = () => {
  auth.logout()
  closeMenus()
  router.push({ name: 'auth', query: { mode: 'login' } })
}

watch(
  () => route.meta.fullBleed,
  (v) => {
    document.body.classList.toggle('no-scroll', !!v)
  },
  { immediate: true },
)

const normalLink = (path) => {
  const active = route.path === path
  return [
    'px-4 py-2 rounded-full text-sm font-medium transition-colors duration-200',
    active ? 'bg-white/10 text-purple-300' : 'text-gray-200 hover:text-white hover:bg-white/5',
  ]
}
</script>

<style>
.toolbar {
  animation: toolbarIn 280ms cubic-bezier(0.2, 0.9, 0.2, 1) both;
  top: 76px;
}

@keyframes toolbarIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

html,
body,
#app {
  height: 100%;
}


</style>
