<template>
  <div class="flex gap-4">
    <button
      @click="visible = !visible"
      class="px-6 py-2 min-w-[120px] text-center text-violet-600 border border-violet-600 rounded hover:bg-violet-600 hover:text-white active:bg-indigo-500 focus:outline-none focus:ring"
    >
      {{ visible ? 'Masquer le composant' : 'Ajouter Paire' }}
    </button>

    <!-- Le composant ou bloc n’apparaît que si visible = true -->
    <MonComposant v-if="visible" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import MonComposant from './AjoutPaire.vue'

const visible = ref(false)
</script>
