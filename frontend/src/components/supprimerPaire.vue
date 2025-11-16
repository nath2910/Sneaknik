<template>
  <!-- Overlay pleine page -->
  <div class="fixed inset-0 z-40 flex items-center justify-center bg-black/60">
    <!-- Boîte centrale -->
    <div class="bg-white rounded-lg shadow-xl w-full max-w-md mx-4">
      <!-- Header -->
      <div class="flex items-start justify-between p-5 border-b rounded-t bg-gray-50">
        <div>
          <h3 class="text-xl font-semibold text-gray-900">Supprimer une vente</h3>
          <p class="text-sm text-gray-500 mt-1">Entre l'ID de la vente que tu veux supprimer.</p>
        </div>

        <!-- Croix de fermeture -->
        <button
          type="button"
          @click="close"
          class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 inline-flex items-center"
        >
          <svg
            class="w-5 h-5"
            fill="currentColor"
            viewBox="0 0 20 20"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              fill-rule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clip-rule="evenodd"
            ></path>
          </svg>
        </button>
      </div>

      <!-- Messages -->
      <div
        v-if="success"
        class="mx-6 mt-4 rounded-lg border border-green-200 bg-green-50 p-3 text-sm text-green-800"
      >
        ✅ Vente supprimée avec succès !
      </div>
      <div
        v-if="error"
        class="mx-6 mt-4 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800"
      >
        ⚠️ {{ error }}
      </div>

      <!-- Formulaire -->
      <form class="p-6 space-y-6" @submit.prevent="supprimerVente">
        <div>
          <label for="venteId" class="text-sm font-medium text-gray-900 block mb-2">
            ID de la vente
          </label>
          <input
            type="number"
            id="venteId"
            v-model.number="venteId"
            class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-red-500 focus:border-red-500 block w-full p-2.5"
            placeholder="Ex : 12"
            min="1"
            required
          />
        </div>

        <p class="text-xs text-gray-500">
          ⚠️ Cette action est définitive. Vérifie bien l’ID avant de supprimer.
        </p>

        <!-- Footer -->
        <div class="pt-4 border-t border-gray-200 flex justify-end space-x-3">
          <button
            type="button"
            class="px-5 py-2.5 text-sm rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100"
            @click="close"
            :disabled="loading"
          >
            Annuler
          </button>

          <button
            class="inline-flex items-center justify-center text-white bg-red-600 hover:bg-red-700 focus:ring-4 focus:ring-red-200 font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-60 disabled:cursor-not-allowed"
            type="submit"
            :disabled="loading"
          >
            <svg
              v-if="loading"
              class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              ></circle>
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8v4l3.5-3.5L12 0v4a8 8 0 100 16v-4l-3.5 3.5L12 24v-4a8 8 0 01-8-8z"
              ></path>
            </svg>
            <span>{{ loading ? 'Suppression...' : 'Supprimer la vente' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const emit = defineEmits(['close'])

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const venteId = ref(null)
const loading = ref(false)
const success = ref(false)
const error = ref(null)

const close = () => {
  emit('close')
}

const supprimerVente = async () => {
  if (!venteId.value) return

  loading.value = true
  success.value = false
  error.value = null

  try {
    await axios.delete(`${API_BASE_URL}/snkVente/${venteId.value}`)

    success.value = true
    venteId.value = null

    // Option : fermer automatique après succès
    setTimeout(() => {
      success.value = false
      emit('close')
    }, 1200)
  } catch (err) {
    console.error('Erreur suppression:', err)
    if (err.response?.status === 404) {
      error.value = `Aucune vente trouvée avec l'ID ${venteId.value}`
    } else {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression'
    }
  } finally {
    loading.value = false
  }
}
</script>
