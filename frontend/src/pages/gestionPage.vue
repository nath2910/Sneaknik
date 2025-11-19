<template>
  <div class="max-w-6xl mx-auto p-8">
    <!-- Bande de titre -->
    <div
      class="mb-8 rounded-2xl bg-gradient-to-r from-violet-600 to-pink-500 px-6 py-4 text-white shadow-lg flex items-center justify-between"
    >
      <h1 class="text-xl font-semibold">Gestion des ventes</h1>
      <p class="text-sm opacity-80">Ajoute ou supprime des paires et surveille ton stock</p>
    </div>

    <!-- Barre de recherche -->
    <div class="mb-6 flex justify-start">
      <!-- 🔥 maintenant reliée à searchTerm -->
      <SearchBarre v-model="searchTerm" /> <!-- recupere via le props-->
    </div>

    <div class="flex gap-6 items-start">
      <!-- Tableau -->
      <div class="flex-1">
        <!-- 🔥 on affiche la liste filtrée -->
        <afficherTout :snkVentes="filteredVentes" />
      </div>

      <!-- Boutons -->
      <div class="w-60 flex flex-col gap-4 items-stretch">
        <boutonOnAdd @vente-ajoutee="handleVenteAjoutee" @vente-supprimee="handleVenteSupprimee" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import boutonOnAdd from '@/components/boutonOnAdd.vue'
import afficherTout from '@/components/afficherTout.vue'
import SnkVenteServices from '@/services/SnkVenteServices'
import SearchBarre from '@/components/searchBarre.vue'

const snkVentes = ref([])
const searchTerm = ref('') // 🔍 ce que tape l'utilisateur

const chargerVentes = async () => {
  const { data } = await SnkVenteServices.getSnkVente()
  snkVentes.value = data
}

onMounted(chargerVentes)

const handleVenteAjoutee = async () => {
  await chargerVentes()
}

const handleVenteSupprimee = (id) => {
  snkVentes.value = snkVentes.value.filter((v) => v.id !== id)
}

// 🧠 liste filtrée selon searchTerm
const filteredVentes = computed(() => {
  if (!searchTerm.value) return snkVentes.value //barre de recherche vide on renvois la liste entiere

  const term = searchTerm.value.toLowerCase()

  return snkVentes.value.filter((v) => {
    // adapte selon ce que contient une vente
    // (marque, modele, id, etc.)
    return String(v.id).includes(term) || v.nomItem?.toLowerCase().includes(term)
  })
})
</script>
