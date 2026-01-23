<template>
  <div class="min-h-screen bg-slate-950 px-4 py-10 sm:py-14">
    <div class="mx-auto w-full max-w-md">
      <div
        class="rounded-2xl border border-slate-800 bg-gradient-to-b from-slate-900/90 to-slate-900/60 p-6 shadow-2xl sm:rounded-3xl sm:p-8"
      >
        <h1 class="text-xl font-semibold text-slate-100 mb-4">Mot de passe oublie</h1>

        <p class="text-sm text-slate-300 mb-6">
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
            <label for="email" class="block text-sm font-medium text-slate-200">Email</label>
            <input
              id="email"
              type="email"
              v-model="email"
              required
              class="mt-2 block w-full rounded-xl border border-slate-700 bg-slate-950/60 px-3 py-2.5 text-slate-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-emerald-400"
            />
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full inline-flex justify-center items-center gap-2 rounded-xl bg-emerald-500 px-4 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-emerald-400 focus:outline-none focus:ring-2 focus:ring-emerald-400 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {{ loading ? 'Envoi...' : 'Envoyer' }}
          </button>
        </form>

        <div class="mt-5 text-center">
          <router-link class="text-xs text-slate-400 hover:text-slate-200" :to="{ name: 'auth' }">
            Retour a la connexion
          </router-link>
        </div>
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
