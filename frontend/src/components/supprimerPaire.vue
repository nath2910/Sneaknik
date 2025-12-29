<!-- src/components/SupprimerPaire.vue -->
<template>
  <div class="fixed inset-0 z-40 flex items-center justify-center bg-black/60">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-md mx-4 p-5">
      <h3 class="text-xl font-semibold text-gray-900 mb-2">Supprimer une paire</h3>

      <div
        v-if="success"
        class="mb-3 rounded border border-green-200 bg-green-50 px-3 py-2 text-sm text-green-800"
      >
        ✅ Vente supprimée avec succès.
      </div>

      <div
        v-if="error"
        class="mb-3 rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-800"
      >
        ⚠️ {{ error }}
      </div>

      <form @submit.prevent="supprimerVente" class="space-y-4">
        <div>
          <label for="venteId" class="block text-sm font-medium text-gray-900 mb-1">
            ID de la vente
          </label>
          <input
            id="venteId"
            type="number"
            v-model.number="venteId"
            min="1"
            class="w-full rounded border border-gray-300 px-2 py-1.5 text-sm"
            placeholder="Ex : 12"
            required
          />
        </div>

        <p class="text-xs text-gray-500">
          ⚠️ Action définitive. Vérifie bien l’ID avant de supprimer.
        </p>

        <div class="flex justify-end gap-2 pt-2">
          <button
            type="button"
            class="px-4 py-1.5 text-sm rounded border border-gray-300 text-gray-700 hover:bg-gray-100"
            @click="close"
            :disabled="loading"
          >
            Annuler
          </button>

          <button
            type="submit"
            class="px-4 py-1.5 text-sm rounded bg-red-600 text-white hover:bg-red-700 disabled:opacity-60"
            :disabled="loading || !venteId"
          >
            {{ loading ? 'Suppression...' : 'Supprimer' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'

const emit = defineEmits(['close', 'deleted'])

const venteId = ref(null)
const loading = ref(false)
const success = ref(false)
const error = ref('')

const resetState = () => {
  venteId.value = null
  loading.value = false
  success.value = false
  error.value = ''
}

const close = () => {
  resetState()
  emit('close')
}

const supprimerVente = async () => {
  if (!venteId.value) return

  loading.value = true
  success.value = false
  error.value = ''

  try {
    await SnkVenteServices.supprimer(venteId.value)

    //  on informe le parent quel ID a été supprimé
    emit('deleted', venteId.value)

    success.value = true

    //  on laisse le message puis on ferme (form disparaît)
    setTimeout(() => {
      close()
    }, 1000)
  } catch (err) {
    console.error('Erreur suppression:', err)
    if (err.response?.status === 404) {
      error.value = `Aucune vente trouvée avec l'ID ${venteId.value}`
    } else {
      error.value = 'Erreur lors de la suppression'
    }
  } finally {
    loading.value = false
  }
}
</script>
