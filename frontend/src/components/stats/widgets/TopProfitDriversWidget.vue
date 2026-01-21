<template>
  <WidgetCard
    title="Top profit"
    subtitle="Marques / Catégories"
    :accent="accent"
    :loading="loading"
    :error="error"
  >
    <div class="flex items-center gap-2 mb-2">
      <button
        class="text-[11px] px-2.5 py-1 rounded-full border"
        :class="mode === 'brands' ? activePill : idlePill"
        @click="mode = 'brands'"
      >
        Marques
      </button>
      <button
        class="text-[11px] px-2.5 py-1 rounded-full border"
        :class="mode === 'categories' ? activePill : idlePill"
        @click="mode = 'categories'"
      >
        Catégories
      </button>
    </div>

    <VChart class="w-full h-full" :option="option" autoresize />
  </WidgetCard>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeRank } from '@/services/statsAdapters'
import { formatEUR } from '@/utils/formatters'
import WidgetCard from './_parts/WidgetCard.vue'

const props = defineProps({ from: String, to: String, top: { type: Number, default: 8 } })
const accent = '#8B5CF6'
const mode = ref('brands')

const loading = ref(false)
const error = ref('')
const brands = ref([])
const categories = ref([])
let req = 0

async function load() {
  const id = ++req
  loading.value = true
  error.value = ''
  try {
    const [b, c] = await Promise.all([
      StatsServices.rank('topBrandsProfit', props.from, props.to, props.top),
      StatsServices.rank('topCategoriesProfit', props.from, props.to, props.top),
    ])
    if (id !== req) return
    brands.value = normalizeRank(b.data)
    categories.value = normalizeRank(c.data)
  } catch (e) {
    if (id !== req) return
    error.value = e?.response?.data?.message ?? e?.message ?? 'Impossible de charger'
  } finally {
    if (id === req) loading.value = false
  }
}

onMounted(load)
watch(() => [props.from, props.to, props.top], load)

const activePill = 'border-violet-400/40 bg-violet-500/15 text-violet-200'
const idlePill = 'border-slate-700 bg-slate-900/30 text-white/60 hover:text-white/80'

const option = computed(() => {
  const items = mode.value === 'brands' ? brands.value : categories.value
  const labels = items.map((i) => i.label)
  const values = items.map((i) => i.value)

  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', valueFormatter: (v) => formatEUR(v) },
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
    color: [accent],
  }
})
</script>
