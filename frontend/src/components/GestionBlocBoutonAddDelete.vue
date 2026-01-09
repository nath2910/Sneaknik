<template>
  <div class="w-full flex justify-end">
    <boutonOnAdd @vente-ajoutee="$emit('vente-ajoutee')" />
  </div>
</template>

<script setup>
import boutonOnAdd from '@/components/GestionBoutonOnAdd.vue'
defineEmits(['vente-ajoutee'])
</script>
