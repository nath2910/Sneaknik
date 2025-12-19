<template>
  <transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4"
    >
      <div class="w-full max-w-md bg-gray-900 rounded-2xl border border-gray-700 shadow-xl p-6">
        <h3 class="text-lg font-semibold text-gray-100 mb-4">Modifier l'item</h3>

        <div v-if="localVente" class="space-y-4">
          <div class="text-sm text-gray-300">
            <p class="font-medium text-gray-100">
              {{ localVente.nomItem || localVente.nom_item }}
            </p>
            <p class="text-xs text-gray-400">Catégorie : {{ localVente.categorie || '—' }}</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-200 mb-1">
              Prix de revente (€)
            </label>
            <input
              v-model="editForm.prixResell"
              type="number"
              min="0"
              class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-200 mb-1"> Date de vente </label>
            <input
              v-model="editForm.dateVente"
              type="date"
              class="mt-1 block w-full px-3 py-2 rounded-lg border border-gray-600 bg-gray-900 text-gray-100 shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-purple-500"
            />
          </div>

          <div class="mt-6 flex justify-end gap-3">
            <button
              type="button"
              class="px-4 py-2 rounded-lg text-sm font-medium text-gray-200 bg-gray-800 border border-gray-600 hover:bg-gray-700"
              @click="close"
            >
              Annuler
            </button>
            <button
              type="button"
              class="px-4 py-2 rounded-lg text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 disabled:opacity-60 disabled:cursor-not-allowed"
              :disabled="saving"
              @click="save"
            >
              {{ saving ? 'Enregistrement...' : 'Enregistrer' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch } from 'vue'
import SnkVenteServices from '@/services/SnkVenteServices'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true,
  },
  vente: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:modelValue', 'saved'])

const saving = ref(false)
const localVente = ref(null)
const editForm = ref({
  prixResell: '',
  dateVente: '',
})

const toInputDate = (value) => {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// sync quand la vente change
watch(
  () => props.vente,
  (val) => {
    localVente.value = val ? { ...val } : null
    if (!val) {
      editForm.value = { prixResell: '', dateVente: '' }
      return
    }

    editForm.value = {
      prixResell: String(val.prixResell ?? val.prix_resell ?? ''),
      dateVente: toInputDate(val.dateVente ?? val.date_vente),
    }
  },
  { immediate: true },
)

const close = () => {
  emit('update:modelValue', false)
}

const save = async () => {
  if (!localVente.value) return
  saving.value = true
  try {
    const payload = {
      ...localVente.value,
      prixResell: Number(editForm.value.prixResell),
      dateVente: editForm.value.dateVente || null,
    }

    const { data } = await SnkVenteServices.updateSnkVente(localVente.value.id, payload)
    const updated = data || payload

    emit('saved', updated)
    close()
  } catch (e) {
    console.error('Erreur lors de la mise à jour de la vente', e)
  } finally {
    saving.value = false
  }
}
</script>
