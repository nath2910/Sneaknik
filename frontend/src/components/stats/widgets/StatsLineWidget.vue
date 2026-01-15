<template>
  <div class="h-full">
    <div class="flex items-center justify-between mb-2">
      <p class="text-xs text-white/60">Évolution</p>
      <span class="text-xs text-white/40">{{ granularityLabel }}</span>
    </div>
    <VChart class="chart" :option="option" autoresize />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeTimeseries } from '@/services/statsAdapters'

const props = defineProps({
  from: String,
  to: String,
  granularity: { type: String, default: 'day' }, // day|week|month
  showCA: { type: Boolean, default: true },
  showProfit: { type: Boolean, default: true },
})

const points = ref([])
let req = 0

async function load() {
  const id = ++req
  const { data } = await StatsServices.timeseries(props.from, props.to, props.granularity)
  if (id !== req) return
  points.value = normalizeTimeseries(data)
}
onMounted(load)
watch(() => [props.from, props.to, props.granularity], load)

const granularityLabel = computed(() =>
  props.granularity === 'month' ? 'Mensuel' : props.granularity === 'week' ? 'Hebdo' : 'Journalier',
)

const option = computed(() => {
  const xs = points.value.map((p) => p.date)
  const series = []
  if (props.showCA) {
    series.push({
      name: 'CA',
      type: 'line',
      data: points.value.map((p) => p.ca),
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 3 },
    })
  }
  if (props.showProfit) {
    series.push({
      name: 'Profit',
      type: 'line',
      data: points.value.map((p) => p.profit),
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 3 },
    })
  }

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    legend: { textStyle: { color: 'rgba(255,255,255,.65)' } },
    grid: { left: 44, right: 14, top: 26, bottom: 24 },
    xAxis: { type: 'category', data: xs, axisLabel: { color: '#9CA3AF' } },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#9CA3AF' },
      splitLine: { lineStyle: { color: '#1F2937' } },
    },
    series,
    color: ['#8B5CF6', '#22C55E'],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
