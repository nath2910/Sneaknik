<template>
  <div>
    <!-- Bouton visible uniquement quand le modal est fermé -->
    <button
      v-if="!visible"
      @click="visible = true"
      class="px-6 py-2 min-w-[120px] text-center text-violet-600 border border-violet-600 rounded hover:bg-violet-600 hover:text-white active:bg-indigo-500 focus:outline-none focus:ring"
    >
      Ajouter une paire
    </button>

    <!-- Le modal : il émettra 'close' pour se cacher -->
    <AjoutPaire v-if="visible" @close="visible = false" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AjoutPaire from './AjoutPaire.vue'

const visible = ref(false)
</script>
