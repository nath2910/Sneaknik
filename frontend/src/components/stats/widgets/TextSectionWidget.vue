<template>
  <div class="h-full w-full flex items-center">
    <div :class="contentClass" class="w-full whitespace-pre-wrap">
      {{ content }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  content: { type: String, default: 'Section title' },
  variant: { type: String, default: 'title' }, // title | note | muted
  align: { type: String, default: 'left' }, // left | center | right
})

const contentClass = computed(() => {
  const align =
    props.align === 'center' ? 'text-center' : props.align === 'right' ? 'text-right' : 'text-left'

  if (props.variant === 'muted') {
    return `${align} text-xs uppercase tracking-[0.3em] text-slate-400`
  }
  if (props.variant === 'note') {
    return `${align} text-sm text-slate-300`
  }
  return `${align} text-2xl font-semibold text-slate-100`
})
</script>
