<!-- src/pages/snkVente.vue -->
<template>
  <div class="max-w-135 p-6 border-2 border-purple-500 rounded-xl text-center">
    <h1 class="text-white text-sm font-medium font-black leading-tight text-xl">
      Liste des Dernier Items
    </h1>
    <table class="text-white text-sm font-medium font-black leading-tight">
      <thead class="underline underline-offset-6 decoration-2 decoration-purple-500">
        <tr>
          <th>Nom Item</th>
          <th>Prix Retail</th>
          <th>Prix Resell</th>
          <th>Date</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="item in snkVentes" :key="item.id">
          <td class="px-4 py-2">{{ item.nomItem }}</td>
          <td class="px-4 py-2 text-right">{{ item.prixRetail }}</td>
          <td class="px-4 py-2 text-right">{{ item.prixResell || '—' }}</td>
          <td class="px-4 py-2">{{ item.date }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import snkVenteServices from '../services/SnkVenteServices'

export default {
  name: 'SnkVentes',
  data() {
    return { snkVentes: [] }
  },
  methods: {
    async rechercher10() {
      const { data } = await snkVenteServices.rechercher10(10)
      this.snkVentes = data
    },
  },
  created() {
    this.rechercher10()
  },
}
</script>
