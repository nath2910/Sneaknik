<template>
  <!-- On enveloppe toutes les pages dans ton layout global -->
  <LayoutPages>
    <!-- Le router rend ici la page correspondant à l'URL -->
    <RouterView />
  </LayoutPages>
</template>

<script setup>
import LayoutPages from '@/layout/layoutPages.vue'
</script>
<style></style>
