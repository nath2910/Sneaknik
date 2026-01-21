<template>
  <WidgetCard
    :title="title"
    :subtitle="subtitle"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <VChart class="w-full h-full" :option="option" autoresize />
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
