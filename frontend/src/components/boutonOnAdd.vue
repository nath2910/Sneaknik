<!-- src/components/SnkVentesToolbar.vue -->
<template>
  <div class="flex flex-col gap-4">
    <button
      v-if="!showAdd"
      @click="showAdd = true"
      class="px-6 py-2 min-w-[150px] text-center text-violet-600 border border-violet-600 rounded hover:bg-violet-600 hover:text-white active:bg-indigo-500 focus:outline-none focus:ring"
    >
      Ajouter une paire
    </button>

    <button
      v-if="!showDelete"
      @click="showDelete = true"
      class="px-6 py-2 min-w-[150px] text-center text-red-500 border border-red-500 rounded hover:bg-red-500 hover:text-white active:bg-red-600 focus:outline-none focus:ring"
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
import SupprimerPaire from '@/components/SupprimerPaire.vue'

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
