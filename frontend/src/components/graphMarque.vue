<template>
  <section class="font-black">
    <div class="py-6 mt-4 grid px-2 text-white">
      <h1 class="text-xl font-bold text-white mb-4 ml-35">Graphique par marque</h1>
      <div id="pie-chart" class="w-full"></div>
    </div>
  </section>
</template>
<script setup>
import { onMounted } from 'vue'
import axios from 'axios'
import ApexCharts from 'apexcharts'

const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

onMounted(async () => {
  const el = document.querySelector('#pie-chart')
  if (!el) return

  try {
    const res = await axios.get(`${API}/snkVente/marque`)
    const data = res.data

    const labels = data.map((d) => d.marque)
    const series = data.map((d) => d.nb)

    new ApexCharts(el, {
      chart: { type: 'pie', height: 300 },
      labels,
      series,
      legend: {
        position: 'left',
        labels: {
          colors: 'white',
        },
      },
      horizontalAlign: 'center',
      plotOptions: {
        pie: {
          offsetX: -290,
        },
      },
      colors: [
        '#FF4560', // rouge rosé
        '#008FFB', // bleu
        '#00E396', // vert
        '#FEB019', // orange
        '#775DD0', // violet
        '#546E7A', // gris bleu
        '#26a69a', // turquoise
        '#D10CE8', // magenta
      ],
    }).render()
  } catch (err) {
    console.error('❌ Erreur :', err)
    el.textContent = 'Erreur de chargement du graphique.'
  }
})
</script>
