<template>
  <div class="flex justify-center">
    <div class="w-full max-w-2xl bg-gray-800 rounded-xl shadow-lg p-6 text-gray-100">
      <h1 class="text-2xl font-bold mb-6">Mon compte</h1>

      <div class="space-y-8">
        <!-- Infos utilisateur -->
        <section>
          <h2 class="text-lg font-semibold mb-3">Informations du profil</h2>
          <div class="space-y-1 text-sm">
            <p>
              <span class="font-medium text-gray-300">Prénom :</span> {{ currentUser.firstName }}
            </p>
            <p><span class="font-medium text-gray-300">Nom :</span> {{ currentUser.lastName }}</p>
            <p><span class="font-medium text-gray-300">Email :</span> {{ currentUser.email }}</p>
          </div>
        </section>

        <div class="border-t border-gray-700"></div>

        <!-- Changement de mot de passe -->
        <section>
          <h2 class="text-lg font-semibold mb-3">Modifier mon mot de passe</h2>

          <div
            v-if="error"
            class="mb-3 rounded-lg border border-red-400 bg-red-50 p-3 text-sm text-red-800"
          >
            ⚠️ {{ error }}
          </div>
          <div
            v-if="success"
            class="mb-3 rounded-lg border border-green-400 bg-green-50 p-3 text-sm text-green-800"
          >
            ✅ {{ success }}
          </div>

          <form class="space-y-4" @submit.prevent="submitChangePassword">
            <div>
              <label class="block text-sm font-medium mb-1">Mot de passe actuel</label>
              <input
                v-model="form.currentPassword"
                type="password"
                required
                class="mt-1 block w-full px-3 py-2 rounded-md border border-gray-600 bg-gray-900 text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1">Nouveau mot de passe</label>
              <input
                v-model="form.newPassword"
                type="password"
                required
                class="mt-1 block w-full px-3 py-2 rounded-md border border-gray-600 bg-gray-900 text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-1"
                >Confirmer le nouveau mot de passe</label
              >
              <input
                v-model="form.confirmPassword"
                type="password"
                required
                class="mt-1 block w-full px-3 py-2 rounded-md border border-gray-600 bg-gray-900 text-gray-100 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
              />
            </div>

            <button
              type="submit"
              :disabled="loading"
              class="inline-flex items-center px-4 py-2 rounded-md bg-purple-600 hover:bg-purple-700 text-sm font-medium text-white disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {{ loading ? 'Modification...' : 'Mettre à jour le mot de passe' }}
            </button>
          </form>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/store/authStore'
import AuthService from '@/services/AuthService'

const { user } = useAuthStore()
const currentUser = user

const form = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const loading = ref(false)
const error = ref('')
const success = ref('')

const resetMessages = () => {
  error.value = ''
  success.value = ''
}

const submitChangePassword = async () => {
  resetMessages()

  if (form.value.newPassword !== form.value.confirmPassword) {
    error.value = 'Les nouveaux mots de passe ne correspondent pas.'
    return
  }
  if (form.value.newPassword.length < 6) {
    error.value = 'Le nouveau mot de passe doit faire au moins 6 caractères.'
    return
  }

  loading.value = true
  try {
    // 👉 à adapter selon ton endpoint Spring Boot
    await AuthService.changePassword({
      currentPassword: form.value.currentPassword,
      newPassword: form.value.newPassword,
    })

    success.value = 'Mot de passe modifié avec succès ✅'
    form.value.currentPassword = ''
    form.value.newPassword = ''
    form.value.confirmPassword = ''
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || 'Erreur lors de la modification du mot de passe.'
  } finally {
    loading.value = false
  }
}


</script>
