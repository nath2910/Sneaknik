<template>
  <ChartCard
    title="Death pile"
    subtitle="Stock dormant (âge)"
    :accent="accent"
    :loading="loading"
    :error="error"
    :option="option"
  />
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeBreakdown } from '@/services/statsAdapters'
import { formatNumber } from '@/utils/formatters'
import ChartCard from './_parts/ChartCard.vue'

const props = defineProps({ from: String, to: String })
const accent = '#EF4444'

const loading = ref(false)
const error = ref('')
const buckets = ref([]) // label: "0-30", "31-90", "91-180", "180+"
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { data } = await StatsServices.breakdown('deathPileAge', props.from, props.to)
    if (id !== req) return
    buckets.value = normalizeBreakdown(data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to], load)

const option = computed(() => {
  const labels = buckets.value.map((b) => b.label)
  const values = buckets.value.map((b) => b.value)
  const colors = labels.map((l) => {
    const s = l.toLowerCase()
    if (s.includes('180')) return '#B91C1C'
    if (s.includes('91') || s.includes('90')) return '#EF4444'
    if (s.includes('31')) return '#F59E0B'
    return '#22C55E'
  })

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', valueFormatter: (v) => formatNumber(v) },
    grid: { left: 46, right: 14, top: 10, bottom: 26 },
    xAxis: {
      type: 'category',
      data: labels,
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
        type: 'bar',
        data: values.map((v, i) => ({
          value: v,
          itemStyle: { color: colors[i], borderRadius: [10, 10, 4, 4] },
        })),
        barWidth: 26,
      },
    ],
  }
})
</script>
