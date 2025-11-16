<template>
  <div class="flex gap-4">
    <!-- Bouton ajout -->
    <button
      v-if="!showAdd"
      @click="showAdd = true"
      class="px-6 py-2 min-w-[150px] text-center text-violet-600 border border-violet-600 rounded hover:bg-violet-600 hover:text-white active:bg-indigo-500 focus:outline-none focus:ring"
    >
      Ajouter une paire
    </button>

    <!-- Bouton suppression -->
    <button
      v-if="!showDelete"
      @click="showDelete = true"
      class="px-6 py-2 min-w-[150px] text-center text-red-500 border border-red-500 rounded hover:bg-red-500 hover:text-white active:bg-red-600 focus:outline-none focus:ring"
    >
      Supprimer une paire
    </button>

    <!-- Modals -->
    <AjoutPaire v-if="showAdd" @close="showAdd = false" />
    <SupprimerPaire v-if="showDelete" @close="showDelete = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AjoutPaire from './AjoutPaire.vue'
import SupprimerPaire from './supprimerPaire.vue'

const showAdd = ref(false)
const showDelete = ref(false)
</script>
