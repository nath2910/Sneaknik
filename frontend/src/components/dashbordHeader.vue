<template>
  <section
    class="rounded-3xl bg-gray-800/90 border border-gray-700 px-8 py-6 text-white shadow-lg flex flex-col gap-2"
  >
    <p v-if="subtitle" class="text-xs uppercase tracking-[0.2em] text-purple-300/80">
      {{ subtitle }}
    </p>
    <h1 class="text-2xl md:text-3xl font-semibold text-white">
      {{ title }}
    </h1>
    <p v-if="description" class="text-sm md:text-base text-gray-200/90">
      {{ description }}
    </p>
    <!-- Slot pour ajouter du contenu en plus si besoin -->
    <slot />
  </section>
</template>

<script setup lang="ts">
interface Props {
  subtitle?: string
  title: string
  description?: string
}

defineProps<Props>()
</script>
