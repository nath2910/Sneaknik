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
const oneMonthAgo = new Date(now)
oneMonthAgo.setMonth(now.getMonth() - 1)

const from = ref(ymd(oneMonthAgo))
const to = ref(ymd(now))
</script>
