<template>
  <div class="h-full">
    <p class="text-xs text-white/60 mb-2">Cumul — {{ metricLabel }}</p>
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
watch(() => [props.from, props.to, props.metric, props.granularity], load)

const metricLabel = computed(() => (props.metric === 'profit' ? 'Profit' : 'CA'))

const option = computed(() => {
  const xs = points.value.map((p) => p.date)
  let acc = 0
  const ys = points.value.map((p) => {
    acc += props.metric === 'profit' ? p.profit : p.ca
    return acc
  })

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
    series: [{ type: 'line', data: ys, smooth: true, symbol: 'none', lineStyle: { width: 3 } }],
    color: ['#8B5CF6'],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
