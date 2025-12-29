<!-- src/components/boutonOnAdd.vue -->
<template>
  <div class="flex flex-wrap gap-3">
    <!-- BTN AJOUTER -->
    <button
      v-if="!showAdd"
      @click="showAdd = true"
      class="px-6 py-2 min-w-[150px] text-center rounded border border-green-600 bg-transparent text-green-600 cursor-pointer transition-colors transition-transform duration-150 hover:bg-green-600 hover:text-white hover:-translate-y-0.5 hover:shadow-lg active:translate-y-0 active:shadow-md focus:outline-none focus:ring focus:ring-violet-500/50"
    >
      Ajouter une paire
    </button>

    <!-- BTN SUPPRIMER -->
    <button
      v-if="!showDelete"
      @click="showDelete = true"
      class="px-6 py-2 min-w-[150px] text-center rounded border border-red-500 bg-transparent text-red-500 cursor-pointer transition-colors transition-transform duration-150 hover:bg-red-500 hover:text-white hover:-translate-y-0.5 hover:shadow-lg active:translate-y-0 active:shadow-md focus:outline-none focus:ring focus:ring-red-500/50"
    >
      Supprimer une paire
    </button>

    <!-- Modale ajout -->
    <AjoutPaire v-if="showAdd" @close="showAdd = false" @added="handleAdded" />

    <!-- Modale suppression -->
    <SupprimerPaire v-if="showDelete" @close="showDelete = false" @deleted="handleDeleted" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AjoutPaire from '@/components/AjoutPaire.vue'
import SupprimerPaire from '@/components/supprimerPaire.vue'

const emit = defineEmits(['vente-ajoutee', 'vente-supprimee'])

const showAdd = ref(false)
const showDelete = ref(false)

const handleAdded = () => {
  showAdd.value = false
  emit('vente-ajoutee')
}

const handleDeleted = (id) => {
  showDelete.value = false
  emit('vente-supprimee', id)
}
</script>
