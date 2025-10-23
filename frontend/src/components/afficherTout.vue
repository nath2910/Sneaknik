<!-- src/pages/snkVente.vue -->
<template>
  <div class="max-w-5xl mx-auto p-6">
    <h1 class="text-2xl font-semibold mb-4 text-center">Liste des items</h1>

    <!--  bloc scrollable -->
    <div class="h-80 max-w-135 overflow-y-auto border rounded-lg shadow bg-gray-800">
      <table class="min-w-full border-collapse">
        <thead class="bg-gray-800 sticky top-0">
          <tr class="text-white">
            <th class="border-b px-4 py-2 text-left">id</th>
            <th class="border-b px-4 py-2 text-left">nom_item</th>
            <th class="border-b px-4 py-2 text-left">prix_retail</th>
            <th class="border-b px-4 py-2 text-left">prix_resell</th>
            <th class="border-b px-4 py-2 text-left">date</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in snkVentes" :key="item.id" class="hover:bg-gray-800 text-white">
            <td class="border-b px-4 py-2">{{ item.id }}</td>
            <td class="border-b px-4 py-2">{{ item.nomItem }}</td>
            <td class="border-b px-4 py-2">{{ item.prixRetail }}</td>
            <td class="border-b px-4 py-2">{{ item.prixResell }}</td>
            <td class="border-b px-4 py-2">{{ item.date }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import snkVenteServices from '../services/snkVenteServices'

export default {
  name: 'SnkVentes',
  data() {
    return { snkVentes: [] }
  },
  methods: {
    async getSnkVente() {
      const { data } = await snkVenteServices.getSnkVente()
      this.snkVentes = data
    },
  },
  created() {
    this.getSnkVente()
  },
}
</script>
