<template>
  <WidgetCard
    title="Top marques"
    subtitle="Volume des ventes"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <VChart class="chart" :option="option" autoresize />
  </WidgetCard>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeBrands } from '@/services/statsAdapters'
import WidgetCard from './_parts/WidgetCard.vue'

const props = defineProps({ from: String, to: String, top: { type: Number, default: 8 } })
const accent = '#8B5CF6'

const loading = ref(false)
const error = ref('')
const brands = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const { data } = await StatsServices.brands(props.from, props.to)
    if (id !== req) return
    brands.value = normalizeBrands(data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to, props.top], load)

const option = computed(() => {
  const items = brands.value.slice(0, props.top)
  const labels = items.map((i) => i.label)
  const values = items.map((i) => i.nb)

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
