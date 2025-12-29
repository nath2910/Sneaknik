<template>
  <div v-if="modelValue" class="fixed inset-0 z-40 flex items-center justify-center bg-black/60">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-start justify-between p-5 border-b rounded-t bg-gray-50">
        <div>
          <h3 class="text-xl font-semibold text-gray-900">Modifier une vente</h3>
          <p class="text-sm text-gray-500 mt-1">Modifie les infos de la paire puis enregistre.</p>
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
        ✅ Modifications enregistrées.
      </div>

      <!-- Formulaire -->
      <form class="p-6 space-y-6" @submit.prevent="save">
        <div class="grid grid-cols-6 gap-6">
          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Nom de l'item</label>
            <input
              type="text"
              v-model="form.nomItem"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              required
            />
          </div>

          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Prix Retail (€)</label>
            <input
              type="number"
              v-model.number="form.prixRetail"
              min="0"
              step="0.01"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              required
            />
          </div>

          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Prix Revente (€)</label>
            <input
              type="number"
              v-model.number="form.prixResell"
              min="0"
              step="0.01"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
            />
          </div>

          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Date d'achat</label>
            <input
              type="date"
              v-model="form.dateAchat"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
              required
            />
          </div>

          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Date de vente</label>
            <input
              type="date"
              v-model="form.dateVente"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
            />
          </div>

          <div class="col-span-6 sm:col-span-3">
            <label class="text-sm font-medium text-gray-900 block mb-2">Catégorie</label>
            <input
              type="text"
              v-model="form.categorie"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
            />
          </div>

          <div class="col-span-6">
            <label class="text-sm font-medium text-gray-900 block mb-2">Description</label>
            <textarea
              v-model="form.description"
              rows="3"
              class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-cyan-600 focus:border-cyan-600 block w-full p-2.5"
            ></textarea>
          </div>
        </div>

        <div class="pt-4 border-t border-gray-200 flex justify-end space-x-3">
          <button
            class="inline-flex items-center justify-center text-white bg-cyan-600 hover:bg-cyan-700 focus:ring-4 focus:ring-cyan-200 font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-60 disabled:cursor-not-allowed"
            type="submit"
            :disabled="loading"
          >
            <span>{{ loading ? 'Enregistrement...' : 'Enregistrer les modifications' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'

const props = defineProps({
  modelValue: { type: Boolean, default: false }, // v-model
  vente: { type: Object, default: null },
})

const emit = defineEmits(['update:modelValue', 'saved'])

const loading = ref(false)
const success = ref(false)
const error = ref(null)

const form = ref({
  id: null,
  nomItem: '',
  prixRetail: null,
  prixResell: null,
  dateAchat: '',
  dateVente: null,
  description: '',
  categorie: '',
})

// quand on reçoit une vente à éditer => on pré-remplit
watch(
  () => props.vente,
  (v) => {
    if (!v) return
    form.value = {
      id: v.id,
      nomItem: v.nomItem ?? v.nom_item ?? '',
      prixRetail: v.prixRetail ?? v.prix_retail ?? null,
      prixResell: v.prixResell ?? v.prix_resell ?? null,
      dateAchat: (v.dateAchat ?? v.date_achat ?? '').slice(0, 10),
      dateVente:
        (v.dateVente ?? v.date_vente ?? '') ? (v.dateVente ?? v.date_vente).slice(0, 10) : null,
      description: v.description ?? '',
      categorie: v.categorie ?? '',
    }
    success.value = false
    error.value = null
  },
  { immediate: true },
)

const close = () => {
  emit('update:modelValue', false)
}

const save = async () => {
  loading.value = true
  success.value = false
  error.value = null

  try {
    const { data } = await SnkVenteServices.update(form.value.id, {
      nomItem: form.value.nomItem,
      prixRetail: form.value.prixRetail,
      prixResell: form.value.prixResell,
      dateAchat: form.value.dateAchat,
      dateVente: form.value.dateVente,
      description: form.value.description,
      categorie: form.value.categorie,
    })

    success.value = true
    emit('saved', data)

    setTimeout(() => {
      close()
      success.value = false
    }, 600)
  } catch (err) {
    error.value = err.response?.data?.message || 'Erreur lors de la modification'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>
