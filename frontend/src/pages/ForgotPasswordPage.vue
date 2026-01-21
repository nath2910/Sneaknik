<template>
  <div class="flex items-center justify-center min-h-[calc(100vh-3.5rem-3.5rem)]">
    <div class="w-full max-w-md bg-gray-800 rounded-2xl shadow-xl border border-gray-700 p-6">
      <h1 class="text-xl font-semibold text-gray-100 mb-4">Mot de passe oublie</h1>

      <p class="text-sm text-gray-300 mb-4">
        Renseigne ton email pour recevoir un lien de reinitialisation.
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

      <form class="space-y-5" @submit.prevent="submitRequest">
        <div>
          <label for="email" class="block text-sm font-medium text-gray-200">Email</label>
          <input
            id="email"
            type="email"
            v-model="email"
            required
            class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full inline-flex justify-center items-center gap-2 py-2.5 px-4 rounded-lg text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 focus:ring-offset-gray-900 disabled:opacity-60 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Envoi...' : 'Envoyer' }}
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
import { ref } from 'vue'
import AuthService from '@/services/AuthService'

const email = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

const resetMessages = () => {
  error.value = ''
  success.value = ''
}

const submitRequest = async () => {
  resetMessages()
  loading.value = true

  try {
    await AuthService.requestPasswordReset({ email: email.value })
    success.value = "Si un compte existe, un email a ete envoye."
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || 'Erreur lors de la demande.'
  } finally {
    loading.value = false
  }
}
</script>
