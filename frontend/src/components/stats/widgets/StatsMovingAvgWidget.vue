<template>
  <div class="h-full">
    <p class="text-xs text-white/60 mb-2">Moyenne mobile ({{ window }} pts) — {{ metricLabel }}</p>
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
  metric: { type: String, default: 'ca' }, // ca|profit
  granularity: { type: String, default: 'day' },
  window: { type: Number, default: 7 },
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
watch(() => [props.from, props.to, props.metric, props.granularity, props.window], load)

const metricLabel = computed(() => (props.metric === 'profit' ? 'Profit' : 'CA'))

function movingAvg(arr, win) {
  const out = []
  for (let i = 0; i < arr.length; i++) {
    const a = Math.max(0, i - win + 1)
    const slice = arr.slice(a, i + 1)
    out.push(slice.reduce((s, v) => s + v, 0) / slice.length)
  }
  return out
}

const option = computed(() => {
  const xs = points.value.map((p) => p.date)
  const raw = points.value.map((p) => (props.metric === 'profit' ? p.profit : p.ca))
  const win = Math.max(2, props.window)
  const ma = movingAvg(raw, win)

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    grid: { left: 44, right: 14, top: 10, bottom: 24 },
    xAxis: { type: 'category', data: xs, axisLabel: { color: '#9CA3AF' } },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#9CA3AF' },
      splitLine: { lineStyle: { color: '#1F2937' } },
    },
    series: [
      {
        type: 'line',
        data: raw,
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 2, opacity: 0.35 },
      },
      { type: 'line', data: ma, smooth: true, symbol: 'none', lineStyle: { width: 3 } },
    ],
    color: ['#93C5FD', '#8B5CF6'],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
