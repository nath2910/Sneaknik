<template>
  <div class="h-full">
    <p class="text-xs text-white/60 mb-2">Top marques (volume)</p>
    <VChart class="chart" :option="option" autoresize />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeBrands } from '@/services/statsAdapters'

const props = defineProps({ from: String, to: String, top: { type: Number, default: 8 } })
const items = ref([])
let req = 0

async function load() {
  const id = ++req
  const { data } = await StatsServices.brands(props.from, props.to)
  if (id !== req) return
  items.value = normalizeBrands(data).slice(0, props.top)
}
onMounted(load)
watch(() => [props.from, props.to, props.top], load)

const option = computed(() => {
  const labels = items.value.map((i) => i.label)
  const values = items.value.map((i) => i.nb)

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    grid: { left: 110, right: 14, top: 10, bottom: 24 },
    xAxis: {
      type: 'value',
      axisLabel: { color: '#9CA3AF' },
      splitLine: { lineStyle: { color: '#1F2937' } },
    },
    yAxis: {
      type: 'category',
      data: labels,
      axisLabel: { color: '#E5E7EB' },
      axisLine: { lineStyle: { color: '#374151' } },
    },
    series: [
      { type: 'bar', data: values, barWidth: 18, itemStyle: { borderRadius: [10, 10, 10, 10] } },
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
