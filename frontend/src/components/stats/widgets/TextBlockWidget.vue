<template>
  <div class="h-full w-full flex items-start">
    <div :class="textClass" class="w-full whitespace-pre-wrap">
      {{ content }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  content: { type: String, default: 'Ton texte ici...' },
  align: { type: String, default: 'left' }, // left | center | right
})

const textClass = computed(() => {
  const align =
    props.align === 'center' ? 'text-center' : props.align === 'right' ? 'text-right' : 'text-left'
  return `${align} text-base leading-relaxed text-slate-300`
})
</script>
