<template>
  <section class="">
    <h1 class="text-purple-500 text-5xl font-poppins mb-15 ml-55">Sneaknik V5</h1>
    <div class="flex gap-25">
      <br />
      <br />
      <Affichage10 />
      <div class="w-[400px]">
        <calendarTab class="" />
      </div>
    </div>
  </section>
</template>
<script setup lang="ts">
import Affichage10 from '../components/Affichage10.vue'
import calendarTab from '@/components/calendarTab.vue'
</script>
