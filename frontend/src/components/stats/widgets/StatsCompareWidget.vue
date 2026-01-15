<template>
  <div class="h-full flex flex-col">
    <p class="text-xs text-white/60 mb-2">Comparatif vs période précédente</p>

    <div class="text-3xl font-semibold text-white">{{ fmt(metricCurr) }}</div>

    <div class="mt-2 flex items-center gap-2">
      <span :class="deltaClass" class="px-2 py-1 rounded-lg text-xs font-semibold">
        {{ deltaText }}
      </span>
      <span class="text-xs text-white/50">{{ metricLabel }}</span>
    </div>

    <div class="mt-auto text-xs text-white/35">Prev: {{ fmt(metricPrev) }}</div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeSummary, prevPeriod } from '@/services/statsAdapters'
import { fmtEUR, fmtInt } from '@/utils/formatters'

const props = defineProps({
  from: String,
  to: String,
  metric: { type: String, default: 'ca' }, // ca|profit|itemsVendues
})

const metricCurr = ref(0)
const metricPrev = ref(0)
let req = 0

async function load() {
  const id = ++req
  const { from: pf, to: pt } = prevPeriod(props.from, props.to)

  const [a, b] = await Promise.all([
    StatsServices.summary(props.from, props.to),
    StatsServices.summary(pf, pt),
  ])
  if (id !== req) return

  const s1 = normalizeSummary(a.data)
  const s2 = normalizeSummary(b.data)

  metricCurr.value = Number(s1[props.metric] ?? 0)
  metricPrev.value = Number(s2[props.metric] ?? 0)
}

onMounted(load)
watch(() => [props.from, props.to, props.metric], load)

const metricLabel = computed(() =>
  props.metric === 'profit' ? 'Profit' : props.metric === 'itemsVendues' ? 'Vendues' : 'CA',
)

const delta = computed(() => {
  const p = metricPrev.value
  if (!p) return null
  return (metricCurr.value - p) / Math.abs(p)
})
const deltaText = computed(() => {
  if (delta.value === null) return '—'
  const pct = Math.round(delta.value * 100)
  return `${pct > 0 ? '+' : ''}${pct}%`
})
const deltaClass = computed(() => {
  if (delta.value === null) return 'bg-white/10 text-white/70'
  return delta.value >= 0 ? 'bg-emerald-500/15 text-emerald-300' : 'bg-rose-500/15 text-rose-300'
})

function fmt(v) {
  if (props.metric === 'itemsVendues') return fmtInt(v)
  return fmtEUR(v)
}
</script>
