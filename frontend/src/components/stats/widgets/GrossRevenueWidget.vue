<template>
  <ChartCard
    title="Chiffre d’affaires"
    subtitle="Évolution sur la période"
    :accent="accent"
    :loading="loading"
    :error="error"
    :option="option"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeSeries } from '@/services/statsAdapters'
import { formatEUR } from '@/utils/formatters'
import ChartCard from './_parts/ChartCard.vue'

const props = defineProps({ from: String, to: String, bucket: { type: String, default: 'day' } })
const accent = '#3B82F6'

const loading = ref(false)
const error = ref('')
const series = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { data } = await StatsServices.series('grossRevenue', props.from, props.to, props.bucket)
    if (id !== req) return
    series.value = normalizeSeries(data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to, props.bucket], load)

const option = computed(() => {
  const x = series.value.map((p) => p.date)
  const y = series.value.map((p) => p.value)

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', valueFormatter: (v) => formatEUR(v) },
    grid: { left: 42, right: 14, top: 10, bottom: 26 },
    xAxis: {
      type: 'category',
      data: x,
      axisLabel: { color: '#9CA3AF' },
      axisLine: { lineStyle: { color: '#374151' } },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#9CA3AF' },
      splitLine: { lineStyle: { color: '#1F2937' } },
    },
    series: [
      {
        type: 'line',
        data: y,
        smooth: true,
        showSymbol: false,
        lineStyle: { width: 3 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: `${accent}66` },
              { offset: 1, color: `${accent}00` },
            ],
          },
        },
      },
    ],
    color: [accent],
  }
})
</script>
