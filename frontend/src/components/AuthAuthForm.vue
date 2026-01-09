<template>
  <div class="flex items-center justify-center min-h-[calc(100vh-3.5rem-3.5rem)]">
    <div class="w-full max-w-xl bg-gray-800 rounded-2xl shadow-xl border border-gray-700 p-6">
      <!-- Header + toggle -->
      <div class="flex flex-col items-center mb-6">
        <h1 class="text-xl font-semibold text-gray-100 mb-3">
          {{ mode === 'login' ? 'Connexion à Sneaknik' : 'Créer un compte' }}
        </h1>

        <div class="inline-flex items-center rounded-full bg-gray-900 p-1 border border-gray-700">
          <button
            class="px-4 py-1.5 text-sm font-medium rounded-full transition"
            :class="
              mode === 'login'
                ? 'bg-purple-600 text-white shadow'
                : 'text-gray-300 hover:text-white'
            "
            @click="setMode('login')"
          >
            Connexion
          </button>
          <button
            class="px-4 py-1.5 text-sm font-medium rounded-full transition"
            :class="
              mode === 'signup'
                ? 'bg-purple-600 text-white shadow'
                : 'text-gray-300 hover:text-white'
            "
            @click="setMode('signup')"
          >
            Inscription
          </button>
        </div>
      </div>

      <!-- Messages -->
      <div
        v-if="error"
        class="mb-4 rounded-lg border border-red-500/70 bg-red-500/10 p-3 text-sm text-red-200"
      >
        ⚠️ {{ error }}
      </div>
      <div
        v-if="success"
        class="mb-4 rounded-lg border border-emerald-500/70 bg-emerald-500/10 p-3 text-sm text-emerald-200"
      >
        ✅ {{ success }}
      </div>

      <!-- Login Form -->
      <form v-if="mode === 'login'" class="space-y-5" @submit.prevent="submitLogin">
        <div>
          <label for="loginEmail" class="block text-sm font-medium text-gray-200">Email</label>
          <input
            type="email"
            id="loginEmail"
            v-model="loginForm.email"
            required
            class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
          />
        </div>

        <div>
          <label for="loginPassword" class="block text-sm font-medium text-gray-200"
            >Mot de passe</label
          >

          <div class="relative mt-1">
            <input
              :type="showLoginPassword ? 'text' : 'password'"
              id="loginPassword"
              v-model="loginForm.password"
              required
              class="block w-full pr-10 px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />

            <button
              type="button"
              class="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-gray-200"
              @click="showLoginPassword = !showLoginPassword"
              :aria-label="
                showLoginPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'
              "
            >
              <span class="text-xs font-medium">
                {{ showLoginPassword ? 'Masquer' : 'Afficher' }}
              </span>
            </button>
          </div>
          <br />
          <button
            type="button"
            @click="loginWithGoogle"
            class="w-full inline-flex justify-center items-center gap-2 py-2.5 px-4 rounded-lg text-sm font-medium text-gray-100 bg-gray-900 border border-gray-700 hover:bg-gray-700/40 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 focus:ring-offset-gray-900"
          >
            <span>Continuer avec Google</span>
          </button>
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full inline-flex justify-center items-center gap-2 py-2.5 px-4 rounded-lg text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 focus:ring-offset-gray-900 disabled:opacity-60 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Connexion...' : 'Se connecter' }}
        </button>
      </form>

      <!-- Signup Form -->
      <form v-else class="space-y-5" @submit.prevent="submitSignup">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label for="firstName" class="block text-sm font-medium text-gray-200">Prénom</label>
            <input
              type="text"
              id="firstName"
              v-model="signupForm.firstName"
              required
              class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
          </div>
          <div>
            <label for="lastName" class="block text-sm font-medium text-gray-200">Nom</label>
            <input
              type="text"
              id="lastName"
              v-model="signupForm.lastName"
              required
              class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
          </div>
        </div>
        <!-- email -->
        <div>
          <label for="signupEmail" class="block text-sm font-medium text-gray-200">Email</label>
          <input
            type="email"
            id="signupEmail"
            v-model="signupForm.email"
            required
            class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
          />
        </div>
        <!-- mdp premier -->
        <div>
          <label for="loginPassword" class="block text-sm font-medium text-gray-200"
            >Mot de passe</label
          >

          <div class="relative mt-1">
            <input
              :type="showLoginPassword ? 'text' : 'password'"
              id="loginPassword"
              v-model="signupFormForm.password"
              required
              class="block w-full pr-10 px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />

            <button
              type="button"
              class="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-gray-200"
              @click="showLoginPassword = !showLoginPassword"
              :aria-label="
                showLoginPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'
              "
            >
              <span class="text-xs font-medium">
                {{ showLoginPassword ? 'Masquer' : 'Afficher' }}
              </span>
            </button>
          </div>
        </div>
        <!-- mdp deuxieme -->
        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-gray-200">
            Confirmer le mot de passe
          </label>

          <div class="relative mt-1">
            <input
              :type="showConfirmPassword ? 'text' : 'password'"
              id="confirmPassword"
              v-model="signupForm.confirmPassword"
              required
              class="block w-full pr-10 px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />

            <button
              type="button"
              class="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-gray-200"
              @click="showConfirmPassword = !showConfirmPassword"
              :aria-label="
                showConfirmPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'
              "
            >
              <span class="text-xs font-medium">
                {{ showConfirmPassword ? 'Masquer' : 'Afficher' }}
              </span>
            </button>
          </div>
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full inline-flex justify-center items-center gap-2 py-2.5 px-4 rounded-lg text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 focus:ring-offset-gray-900 disabled:opacity-60 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Création du compte...' : 'Créer mon compte' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthService from '@/services/AuthService'
import { useAuthStore } from '@/store/authStore'

//mdp cache
const showLoginPassword = ref(false)
const showSignupPassword = ref(false)
const showConfirmPassword = ref(false)

// routing
const route = useRoute()
const router = useRouter()

// store d'auth
const { setAuth } = useAuthStore()

// mode: login / signup (on lit la query ?mode=)
const mode = ref(route.query.mode === 'signup' ? 'signup' : 'login')

const setMode = (m) => {
  mode.value = m
  // on garde l’URL propre: /auth?mode=login|signup
  router.replace({ name: 'auth', query: { mode: m } })
}

watch(
  () => route.query.mode,
  (m) => {
    if (m === 'signup' || m === 'login') {
      mode.value = m
    }
  },
)

// états UI
const loading = ref(false)
const error = ref('')
const success = ref('')

// formulaires
const loginForm = ref({
  email: '',
  password: '',
  remember: false,
})

const signupForm = ref({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  acceptTerms: false,
})

const resetMessages = () => {
  error.value = ''
  success.value = ''
}

// LOGIN
const submitLogin = async () => {
  resetMessages()
  loading.value = true

  try {
    const { data } = await AuthService.login({
      email: loginForm.value.email,
      password: loginForm.value.password,
    })

    setAuth(data) // car data = { user, token }

    success.value = 'Connexion réussie ✅'
    setTimeout(() => {
      router.push('/')
    }, 500)
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || 'Email ou mot de passe invalide.'
  } finally {
    loading.value = false
  }
}

// SIGNUP
const submitSignup = async () => {
  resetMessages()

  if (signupForm.value.password !== signupForm.value.confirmPassword) {
    error.value = 'Les mots de passe ne correspondent pas.'
    return
  }

  loading.value = true
  try {
    await AuthService.register({
      email: signupForm.value.email,
      firstName: signupForm.value.firstName,
      lastName: signupForm.value.lastName,
      password: signupForm.value.password,
    })

    success.value = 'Compte créé avec succès 🎉'
    mode.value = 'login'
    router.replace({ name: 'auth', query: { mode: 'login' } })
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || 'Erreur lors de la création du compte.'
  } finally {
    loading.value = false
  }
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loginWithGoogle = () => {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/google`
}
</script>
