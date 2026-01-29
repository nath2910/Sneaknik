<template>
  <div class="rounded-2xl border border-slate-800/70 bg-slate-900/70 px-4 py-3 text-left">
    <p class="text-[0.7rem] uppercase tracking-wide text-slate-400">
      {{ label }}
    </p>
    <p class="mt-1 text-lg font-semibold" :class="valueClasses">
      <slot>{{ value }}</slot>
    </p>
    <p v-if="$slots.footer" class="mt-1 text-[0.7rem] text-slate-500">
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

const valueClasses = computed(() => {
  switch (props.tone) {
    case 'success':
      return 'text-emerald-300'
    case 'danger':
      return 'text-rose-300'
    case 'accent':
      return 'text-amber-200'
    default:
      return 'text-slate-100'
  }
})
</script>
