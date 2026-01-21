<template>
  <WidgetCard
    title="ROI moyen"
    subtitle="(Bénéfice / Coût achat) x 100"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <div class="h-full flex items-center gap-4">
      <div class="min-w-0">
        <div class="text-3xl font-bold text-white leading-none">{{ roiText }}</div>
        <div class="mt-2">
          <span
            v-if="kpi.deltaPct != null"
            class="text-[11px] px-2 py-0.5 rounded-full border"
            :class="
              kpi.deltaPct >= 0
                ? 'border-emerald-400/30 bg-emerald-500/10 text-emerald-300'
                : 'border-red-400/30 bg-red-500/10 text-red-300'
            "
          >
            {{ deltaText }}
          </span>
        </div>
        <p class="mt-2 text-[11px] text-white/45">Objectif: rester au-dessus de 25–30%</p>
      </div>

      <div class="flex-1 min-w-0 h-full">
        <VChart class="w-full h-full" :option="option" autoresize />
      </div>
    </div>
  </WidgetCard>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeKpi } from '@/services/statsAdapters'
import { formatPct, signFmt } from '@/utils/formatters'
import WidgetCard from './_parts/WidgetCard.vue'

const props = defineProps({ from: String, to: String })
const accent = '#8B5CF6'
const loading = ref(false)
const error = ref('')
const kpi = ref({ value: 0, deltaPct: null })
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { data } = await StatsServices.kpi('roi', props.from, props.to)
    if (id !== req) return
    kpi.value = normalizeKpi(data) // value = ROI en %
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to], load)

const roiText = computed(() => formatPct(kpi.value.value, { digits: 1 }))
const deltaText = computed(() => (kpi.value.deltaPct == null ? '' : signFmt(kpi.value.deltaPct)))

const option = computed(() => {
  const v = Math.max(-50, Math.min(200, Number(kpi.value.value ?? 0)))
  return {
    backgroundColor: 'transparent',
    series: [
      {
        type: 'gauge',
        startAngle: 210,
        endAngle: -30,
        min: -50,
        max: 200,
        splitNumber: 5,
        axisLine: {
          lineStyle: {
            width: 12,
            color: [
              [0.35, '#EF4444'], // -50 -> 37.5
              [0.55, '#F59E0B'],
              [0.75, '#22C55E'],
              [1, '#8B5CF6'],
            ],
          },
        },
        pointer: { itemStyle: { color: '#E5E7EB' } },
        axisTick: { lineStyle: { color: '#475569' } },
        splitLine: { lineStyle: { color: '#334155' } },
        axisLabel: { color: '#9CA3AF' },
        detail: { valueAnimation: true, formatter: (x) => `${x.toFixed(0)}%`, color: '#E5E7EB' },
        data: [{ value: v }],
      },
    ],
  }
})
</script>
