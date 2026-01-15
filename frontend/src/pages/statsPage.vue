<template>
  <StatsCanvas v-model:from="from" v-model:to="to" />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import StatsCanvas from '@/components/stats/StatsCanvas.vue'


function ymd(d: Date) {
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

const now = new Date()
const from = ref(ymd(new Date(now.getFullYear(), now.getMonth(), 1)))
const to = ref(ymd(new Date(now.getFullYear(), now.getMonth() + 1, 0)))
</script>
