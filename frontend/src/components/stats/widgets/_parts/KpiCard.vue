<template>
  <WidgetCard
    :title="title"
    :subtitle="subtitle"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <div class="h-full flex flex-col pb-1" :class="containerClass">
      <div class="flex items-end justify-between gap-3">
        <div class="min-w-0">
          <div class="text-2xl font-bold text-white leading-none truncate">{{ valueText }}</div>
          <div class="mt-2 flex items-center gap-2">
            <span
              v-if="deltaPct != null"
              class="text-[11px] px-2 py-0.5 rounded-full border"
              :class="deltaClass"
            >
              {{ deltaText }}
            </span>
            <span v-if="hint" class="text-[11px] text-white/45 truncate">{{ hint }}</span>
          </div>
        </div>

        <div v-if="spark && spark.length" class="w-28">
          <Sparkline :data="spark" :color="accent" />
        </div>
      </div>

      <slot />
    </div>
  </WidgetCard>
</template>

<script setup>
import { computed } from 'vue'
import WidgetCard from './WidgetCard.vue'
import Sparkline from './Sparkline.vue'

const props = defineProps({
  title: String,
  subtitle: String,
  accent: { type: String, default: '#8B5CF6' },
  loading: Boolean,
  error: String,
  compact: { type: Boolean, default: false },

  valueText: { type: String, default: '--' },
  deltaPct: { type: Number, default: null }, // ex: +12.3 (en %)
  deltaText: { type: String, default: '' },
  deltaClass: { type: String, default: '' },
  hint: { type: String, default: '' },
  spark: { type: Array, default: () => [] },
})

const deltaClass = computed(() => {
  if (props.deltaClass) return props.deltaClass
  if (props.deltaPct == null) return ''
  const up = props.deltaPct >= 0
  return up
    ? 'border-emerald-400/30 bg-emerald-500/10 text-emerald-300'
    : 'border-red-400/30 bg-red-500/10 text-red-300'
})

const containerClass = computed(() =>
  props.compact ? 'justify-start gap-3' : 'justify-between',
)
</script>
