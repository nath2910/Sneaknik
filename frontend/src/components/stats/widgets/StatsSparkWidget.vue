<template>
  <div class="h-full">
    <div class="flex items-center justify-between mb-2">
      <p class="text-xs text-white/60">{{ label }}</p>
      <span class="text-xs text-white/40">{{ metric === 'profit' ? 'Profit' : 'CA' }}</span>
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
  metric: { type: String, default: 'ca' }, // ca|profit
  granularity: { type: String, default: 'day' },
  label: { type: String, default: 'Tendance' },
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

const option = computed(() => {
  const xs = points.value.map((p) => p.date)
  const ys = points.value.map((p) => (props.metric === 'profit' ? p.profit : p.ca))

  return {
    backgroundColor: 'transparent',
    grid: { left: 6, right: 6, top: 6, bottom: 6 },
    xAxis: { type: 'category', data: xs, show: false },
    yAxis: { type: 'value', show: false },
    tooltip: { trigger: 'axis' },
    series: [
      {
        type: 'line',
        data: ys,
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 2 },
        areaStyle: { opacity: 0.18 },
      },
    ],
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
