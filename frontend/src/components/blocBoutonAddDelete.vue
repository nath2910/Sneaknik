<template>
  <div class="w-full flex justify-end">
    <boutonOnAdd @vente-ajoutee="$emit('vente-ajoutee')" />
  </div>
</template>

<script setup>
import boutonOnAdd from '@/components/boutonOnAdd.vue'
defineEmits(['vente-ajoutee'])
</script>
