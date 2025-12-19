<template>
  <section class="font-black">
    <div class="py-6 mt-4 grid px-2 text-white">
      <div id="pie-chart" class="w-full"></div>
    </div>
  </section>
</template>
<script setup>
import { onMounted } from 'vue'
import ApexCharts from 'apexcharts'
import api from '@/services/api' // 👈 au lieu de axios + API

onMounted(async () => {
  const el = document.querySelector('#pie-chart')
  if (!el) return

  try {
    const res = await api.get('/snkVente/marque') // 👈 X-USER-ID auto
    const data = res.data

    const labels = data.map((d) => d.marque)
    const series = data.map((d) => d.nb)

    new ApexCharts(el, {
      chart: { type: 'pie', height: 300 },
      labels,
      series,
      legend: {
        position: 'left',
        horizontalAlign: 'left',
        // on pousse la légende encore plus à gauche
        offsetX: -125,
        itemMargin: {
          vertical: 6,
        },
        labels: {
          colors: 'white',
        },
      },
      plotOptions: {
        pie: {
          offsetX: -110,
        },
      },
      colors: [
        '#FF4560',
        '#008FFB',
        '#00E396',
        '#FEB019',
        '#775DD0',
        '#546E7A',
        '#26a69a',
        '#D10CE8',
      ],
    }).render()
  } catch (err) {
    console.error('❌ Erreur :', err)
    el.textContent = 'Erreur de chargement du graphique.'
  }
})
</script>
