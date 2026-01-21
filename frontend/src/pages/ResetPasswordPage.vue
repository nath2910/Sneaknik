<template>
  <div class="flex items-center justify-center min-h-[calc(100vh-3.5rem-3.5rem)]">
    <div class="w-full max-w-md bg-gray-800 rounded-2xl shadow-xl border border-gray-700 p-6">
      <h1 class="text-xl font-semibold text-gray-100 mb-4">Nouveau mot de passe</h1>

      <p class="text-sm text-gray-300 mb-4">
        Choisis un nouveau mot de passe pour ton compte.
      </p>

      <div
        v-if="error"
        class="mb-4 rounded-lg border border-red-500/70 bg-red-500/10 p-3 text-sm text-red-200"
      >
        {{ error }}
      </div>
      <div
        v-if="success"
        class="mb-4 rounded-lg border border-emerald-500/70 bg-emerald-500/10 p-3 text-sm text-emerald-200"
      >
        {{ success }}
      </div>

      <form class="space-y-5" @submit.prevent="submitReset">
        <div>
          <label for="newPassword" class="block text-sm font-medium text-gray-200"
            >Nouveau mot de passe</label
          >
          <div class="relative mt-1">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="newPassword"
              v-model="newPassword"
              required
              class="block w-full pr-10 px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
            <button
              type="button"
              class="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-gray-200"
              @click="showPassword = !showPassword"
            >
              <span class="text-xs font-medium">
                {{ showPassword ? 'Masquer' : 'Afficher' }}
              </span>
            </button>
          </div>
        </div>

        <div>
          <label for="confirmPassword" class="block text-sm font-medium text-gray-200"
            >Confirmer le mot de passe</label
          >
          <div class="relative mt-1">
            <input
              :type="showConfirmPassword ? 'text' : 'password'"
              id="confirmPassword"
              v-model="confirmPassword"
              required
              class="block w-full pr-10 px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
            <button
              type="button"
              class="absolute inset-y-0 right-2 flex items-center text-gray-400 hover:text-gray-200"
              @click="showConfirmPassword = !showConfirmPassword"
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
          {{ loading ? 'Modification...' : 'Modifier le mot de passe' }}
        </button>
      </form>

      <div class="mt-4 text-center">
        <router-link class="text-xs text-gray-400 hover:text-gray-200" :to="{ name: 'auth' }">
          Retour a la connexion
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AuthService from '@/services/AuthService'

const route = useRoute()
const router = useRouter()
const token = ref(route.query.token || '')

watch(
  () => route.query.token,
  (value) => {
    token.value = value || ''
  },
)

const newPassword = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref('')

const resetMessages = () => {
  error.value = ''
  success.value = ''
}

const submitReset = async () => {
  resetMessages()

  if (!token.value) {
    error.value = "Lien invalide ou manquant."
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    error.value = 'Les mots de passe ne correspondent pas.'
    return
  }

  loading.value = true
  try {
    await AuthService.resetPassword({
      token: token.value,
      newPassword: newPassword.value,
    })
    success.value = 'Mot de passe modifie avec succes.'
    setTimeout(() => {
      router.push({ name: 'auth' })
    }, 2500)
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || 'Erreur lors de la modification.'
  } finally {
    loading.value = false
  }
}
</script>
