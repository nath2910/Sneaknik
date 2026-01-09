<template>
  <div class="bg-gray-900 py-20 sm:py-32">
    <div class="mx-auto max-w-7xl px-6 lg:px-8">
      <div class="grid grid-cols-1 gap-x-8 gap-y-16 text-center lg:grid-cols-3 text-xl">
        <div v-for="stat in stats" :key="stat.id" class="mx-auto flex max-w-xs flex-col gap-y-4">
          <dt class="text-base text-gray-400">{{ stat.name }}</dt>
          <dd class="order-first text-3xl font-semibold tracking-tight text-white sm:text-5xl">
            {{ stat.value }}
          </dd>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import snkVenteServices from '@/services/SnkVenteServices.js'

const loading = ref(true)
const error = ref(null)
const total = ref(0)
const total2025 = ref(0)
const totalCA = ref(0)

const fmt = new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' })

const stats = computed(() => [
  { id: 1, name: 'Total Bénéfice', value: fmt.format(total.value) },
  { id: 2, name: 'Total Chiffre d’affaires', value: fmt.format(totalCA.value) },
  { id: 3, name: 'Total Bénéfice 2025', value: fmt.format(total2025.value) },
])

onMounted(async () => {
  try {
    const { data: totalGlobal } = await snkVenteServices.totalBenef()
    const { data: benef2025 } = await snkVenteServices.totalBenefAnnee(2025)
    const { data: CA } = await snkVenteServices.chiffreAffaire(2025)
    total.value = Number(totalGlobal ?? 0)
    total2025.value = Number(benef2025 ?? 0)
    totalCA.value = Number(CA ?? 0)
  } catch (e) {
    error.value = e?.message || 'Impossible de récupérer les totaux'
  } finally {
    loading.value = false
  }
})
</script>
