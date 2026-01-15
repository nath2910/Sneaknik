<template>
  <div class="h-full">
    <p class="text-xs text-white/60 mb-2">Répartition marques (nb)</p>
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
  const norm = normalizeBrands(data)
  items.value = norm.slice(0, props.top)
}
onMounted(load)
watch(() => [props.from, props.to, props.top], load)

const option = computed(() => {
  const seriesData = items.value.map((i) => ({ name: i.label, value: i.nb }))
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, textStyle: { color: 'rgba(255,255,255,.6)' } },
    series: [
      {
        type: 'pie',
        radius: ['55%', '80%'],
        avoidLabelOverlap: true,
        label: { show: false },
        labelLine: { show: false },
        data: seriesData,
      },
    ],
    color: ['#8B5CF6', '#22C55E', '#06B6D4', '#F59E0B', '#EC4899', '#A78BFA', '#60A5FA', '#34D399'],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
