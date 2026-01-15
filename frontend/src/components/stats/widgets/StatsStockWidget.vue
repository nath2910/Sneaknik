<template>
  <div class="h-full flex flex-col">
    <div class="flex items-center justify-between mb-2">
      <p class="text-xs text-white/60">Stock</p>
      <span class="text-xs text-white/40">{{ ratioLabel }}</span>
    </div>

    <div class="grow min-h-0">
      <VChart class="chart" :option="option" autoresize />
    </div>

    <div class="mt-2 text-xs text-white/60">
      Valeur stock : <span class="text-white font-semibold">{{ fmtEUR(s.valeurStock) }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import StatsServices from '@/services/StatsServices'
import { normalizeSummary } from '@/services/statsAdapters'
import { fmtEUR, fmtInt } from '@/utils/formatters'

const props = defineProps({ from: String, to: String })
const s = ref(normalizeSummary({}))
let req = 0

async function load() {
  const id = ++req
  const { data } = await StatsServices.summary(props.from, props.to)
  if (id !== req) return
  s.value = normalizeSummary(data)
}
onMounted(load)
watch(() => [props.from, props.to], load)

const ratioLabel = computed(() => {
  const sold = s.value.itemsVendues
  const stock = s.value.itemsEnStock
  const total = sold + stock
  if (!total) return '—'
  return `${fmtInt(sold)} vendues / ${fmtInt(stock)} stock`
})

const option = computed(() => {
  const sold = s.value.itemsVendues
  const stock = s.value.itemsEnStock
  return {
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['60%', '82%'],
        label: { show: false },
        data: [
          { name: 'Vendues', value: sold },
          { name: 'En stock', value: stock },
        ],
      },
    ],
    color: ['#22C55E', '#8B5CF6'],
  }
})
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>
