<template>
  <!-- conteneur horizontal pour les deux boutons -->
  <div class="flex flex-wrap justify-center gap-3">
    <boutonOnAdd
      @vente-ajoutee="$emit('vente-ajoutee')"
      @vente-supprimee="$emit('vente-supprimee', $event)"
    />
  </div>
</template>

<script setup>
import boutonOnAdd from '@/components/boutonOnAdd.vue'

defineEmits(['vente-ajoutee', 'vente-supprimee'])
</script>
