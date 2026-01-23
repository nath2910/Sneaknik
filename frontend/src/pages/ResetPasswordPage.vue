<template>
  <div class="min-h-screen bg-slate-950 px-4 py-10 sm:py-14">
    <div class="mx-auto w-full max-w-md">
      <div
        class="rounded-2xl border border-slate-800 bg-gradient-to-b from-slate-900/90 to-slate-900/60 p-6 shadow-2xl sm:rounded-3xl sm:p-8"
      >
        <h1 class="text-xl font-semibold text-slate-100 mb-4">Nouveau mot de passe</h1>

        <p class="text-sm text-slate-300 mb-6">
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
            <label for="newPassword" class="block text-sm font-medium text-slate-200"
              >Nouveau mot de passe</label
            >
            <div class="relative mt-2">
              <input
                :type="showPassword ? 'text' : 'password'"
                id="newPassword"
                v-model="newPassword"
                required
                class="block w-full rounded-xl border border-slate-700 bg-slate-950/60 px-3 py-2.5 pr-10 text-slate-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-emerald-400"
              />
              <button
                type="button"
                class="absolute inset-y-0 right-2 flex items-center text-slate-400 hover:text-slate-200"
                @click="showPassword = !showPassword"
              >
                <span class="text-xs font-medium">
                  {{ showPassword ? 'Masquer' : 'Afficher' }}
                </span>
              </button>
            </div>
          </div>

          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-slate-200"
              >Confirmer le mot de passe</label
            >
            <div class="relative mt-2">
              <input
                :type="showConfirmPassword ? 'text' : 'password'"
                id="confirmPassword"
                v-model="confirmPassword"
                required
                class="block w-full rounded-xl border border-slate-700 bg-slate-950/60 px-3 py-2.5 pr-10 text-slate-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-400 focus:border-emerald-400"
              />
              <button
                type="button"
                class="absolute inset-y-0 right-2 flex items-center text-slate-400 hover:text-slate-200"
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
            class="w-full inline-flex justify-center items-center gap-2 rounded-xl bg-emerald-500 px-4 py-2.5 text-sm font-semibold text-slate-950 transition hover:bg-emerald-400 focus:outline-none focus:ring-2 focus:ring-emerald-400 disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {{ loading ? 'Modification...' : 'Modifier le mot de passe' }}
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
