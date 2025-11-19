<!-- src/components/AjoutPaire.vue -->
<template>
  <div class="fixed inset-0 z-40 flex items-center justify-center bg-black/60">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-start justify-between p-5 border-b rounded-t bg-gray-50">
        <div>
          <h3 class="text-xl font-semibold text-gray-900">Ajouter une vente</h3>
          <p class="text-sm text-gray-500 mt-1">
            Renseigne les infos de la paire pour l'ajouter dans ton suivi.
          </p>
        </div>

        <button
          type="button"
          @click="close"
          class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm p-1.5 inline-flex items-center"
        >
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fill-rule="evenodd"
              d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              clip-rule="evenodd"
            ></path>
          </svg>
        </button>
      </div>

      <!-- Erreur -->
      <div
        v-if="error"
        class="mx-6 mt-4 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800"
      >
        ⚠️ {{ error }}
      </div>

      <!-- Succès -->
      <div
        v-if="success"
        class="mx-6 mt-4 rounded-lg border border-green-200 bg-green-50 p-3 text-sm text-green-800"
      >
        ✅ Vente ajoutée avec succès.
      </div>

      <!-- Formulaire -->
      <form class="p-6 space-y-6" @submit.prevent="creerVente">
        <div class="grid grid-cols-6 gap-6">
          <!-- Nom item -->
          <div class="col-span-6 sm:col-span-3">
            <label for="nomItem" class="text-sm font-medium text-gray-900 block mb-2">
              Nom de la paire
            </label>
            <input
              type="text"
              id="nomItem"
              v-model="form.nomItem"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              placeholder="Nike Dunk Low Panda"
              required
            />
          </div>

          <!-- Prix retail -->
          <div class="col-span-6 sm:col-span-3">
            <label for="prixRetail" class="text-sm font-medium text-gray-900 block mb-2">
              Prix Retail (€)
            </label>
            <input
              type="number"
              id="prixRetail"
              v-model.number="form.prixRetail"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              placeholder="110"
              min="0"
              step="0.01"
              required
            />
          </div>

          <!-- Prix resell -->
          <div class="col-span-6 sm:col-span-3">
            <label for="prixResell" class="text-sm font-medium text-gray-900 block mb-2">
              Prix Revente (€)
            </label>
            <input
              type="number"
              id="prixResell"
              v-model.number="form.prixResell"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              placeholder="180"
              min="0"
              step="0.01"
            />
          </div>

          <!-- Argent prélevé -->
          <div class="col-span-6 sm:col-span-3">
            <label for="argentPreleve" class="text-sm font-medium text-gray-900 block mb-2">
              Argent prélevé (€)
            </label>
            <input
              type="number"
              id="argentPreleve"
              v-model.number="form.argentPreleve"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              placeholder="110"
              min="0"
              step="0.01"
            />
          </div>

          <!-- Date -->
          <div class="col-span-6 sm:col-span-3">
            <label for="date" class="text-sm font-medium text-gray-900 block mb-2">
              Date d'achat
            </label>
            <input
              type="date"
              id="date"
              v-model="form.date"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              required
            />
          </div>
        </div>

        <!-- Footer -->
        <div class="pt-4 border-t border-gray-200 flex justify-end space-x-3">
          <button
            class="inline-flex items-center justify-center text-white bg-cyan-600 hover:bg-cyan-700 focus:ring-4 focus:ring-cyan-200 font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-60 disabled:cursor-not-allowed"
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
            <span>{{ loading ? 'Enregistrement...' : 'Enregistrer la vente' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices'

const emit = defineEmits(['close', 'added'])

const getToday = () => new Date().toISOString().split('T')[0]

const emptyForm = () => ({
  nomItem: '',
  prixRetail: null,
  prixResell: null,
  argentPreleve: null,
  date: getToday(),
})

const form = ref(emptyForm())
const loading = ref(false)
const success = ref(false)
const error = ref(null)

const close = () => {
  emit('close')
}

const creerVente = async () => {
  loading.value = true
  success.value = false
  error.value = null

  try {
    const {  } = await SnkVenteServices.ajouter({
      nomItem: form.value.nomItem,
      prixRetail: form.value.prixRetail,
      prixResell: form.value.prixResell,
      argentPreleve: form.value.argentPreleve,
      date: form.value.date,
    })

    success.value = true

    // 👉 on envoie la vente créée au parent
    emit('added')

    // 👉 on laisse le message puis on ferme la modale (form disparaît)
    setTimeout(() => {
      success.value = false
      form.value = emptyForm()
      close()
    }, 1000)
  } catch (err) {
    error.value = err.response?.data?.message || 'Erreur lors de la création de la vente'
    console.error('Erreur:', err)
  } finally {
    loading.value = false
  }
}
</script>
