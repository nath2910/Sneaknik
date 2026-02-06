<template>
  <WidgetCard
    :title="title"
    :subtitle="subtitle"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <div class="h-full flex flex-col gap-2">
      <VChart class="w-full flex-1" :option="option" autoresize />
      <slot />
    </div>
  </WidgetCard>
</template>

<script setup>
import WidgetCard from './WidgetCard.vue'

defineProps({
  title: String,
  subtitle: String,
  accent: { type: String, default: '#8B5CF6' },
  loading: Boolean,
  error: String,
  option: { type: Object, required: true },
})
</script>
