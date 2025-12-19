<template>
  <div class="rounded-2xl px-4 py-3 border text-left" :class="toneClasses">
    <p class="text-[0.7rem] uppercase tracking-wide text-gray-400">
      {{ label }}
    </p>
    <p class="mt-1 text-lg font-semibold" :class="valueClasses">
      <slot>{{ value }}</slot>
    </p>
    <p v-if="$slots.footer" class="mt-1 text-[0.7rem] text-gray-500">
      <slot name="footer" />
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  label: string
  value?: string | number
  tone?: 'default' | 'success' | 'danger' | 'accent'
}

const props = defineProps<Props>()

const toneClasses = computed(() => {
  switch (props.tone) {
    case 'success':
    case 'danger':
    case 'accent':
      return 'bg-gray-900/70 border-gray-700/80'
    default:
      return 'bg-gray-800 border-gray-700'
  }
})

const valueClasses = computed(() => {
  switch (props.tone) {
    case 'success':
      return 'text-emerald-400'
    case 'danger':
      return 'text-red-400'
    case 'accent':
      return 'text-purple-400'
    default:
      return 'text-gray-100'
  }
})
</script>
