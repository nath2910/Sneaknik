<template>
  <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
    <!-- Bandeau -->
    <DashboardHeader
      subtitle="Backoffice • Stock Sneaknik"
      title="Gestion des ventes"
      description="Ajoute ou supprime des paires et surveille ton stock en temps réel."
    />

    <!-- Contenu connecté -->
    <section class="space-y-6">
      <!-- Résumé stock -->
      <StockSummaryRow
        :total-paires="totalPaires"
        :nb-en-stock="nbEnStock"
        :nb-vendues="nbVendues"
        :valeur-stock="valeurStock"
      />

      <!-- Barre de recherche -->
      <div class="flex justify-center">
        <div class="w-full md:w-3/4 lg:w-2/3">
          <SearchBarre v-model="searchTerm" />
        </div>
      </div>

      <!-- Boutons Ajouter / Supprimer centrés -->
      <div class="flex justify-center">
        <GestionActionsPanel
          @vente-ajoutee="handleVenteAjoutee"
          @vente-supprimee="handleVenteSupprimee"
        />
      </div>

      <!-- Tableau plein largeur -->
      <div class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg overflow-hidden">
        <div class="px-6 py-4 flex items-center justify-between border-b border-gray-700/80">
          <div>
            <h2 class="text-lg font-semibold text-gray-100">Liste des items</h2>
            <p class="text-xs text-gray-400">{{ filteredVentes.length }} paire(s) trouvée(s)</p>
          </div>
        </div>

        <div class="p-4">
          <div class="max-h-[480px] overflow-y-auto pr-2">
            <afficherTout :snkVentes="filteredVentes" @edit="openEditModal" />
          </div>
        </div>
      </div>
    </section>

    <!-- Modal d’édition -->
    <EditVenteModal v-model="showEditModal" :vente="venteToEdit" @saved="handleVenteUpdated" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/store/authStore'
import afficherTout from '@/components/afficherTout.vue'
import SearchBarre from '@/components/searchBarre.vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'
import DashboardHeader from '@/components/dashbordHeader.vue'
import StockSummaryRow from '@/components/résumeStock.vue'
import GestionActionsPanel from '@/components/blocBoutonAddDelete.vue'  
import EditVenteModal from '@/components/modifItem.vue'

const snkVentes = ref([])
const searchTerm = ref('')

const { user } = useAuthStore()
const currentUser = user

const showEditModal = ref(false)
const venteToEdit = ref(null)

const chargerVentes = async () => {
  if (!currentUser.value) {
    snkVentes.value = []
    return
  }
  try {
    const { data } = await SnkVenteServices.getSnkVente()
    snkVentes.value = data
  } catch (e) {
    console.error('Erreur chargement ventes', e)
    snkVentes.value = []
  }
}

onMounted(chargerVentes)

// Stats stock
const isVendue = (v) => Boolean(v.dateVente ?? v.date_vente)
const prixRetailOf = (v) => Number(v.prixRetail ?? v.prix_retail ?? 0)

const totalPaires = computed(() => snkVentes.value.length)
const nbVendues = computed(() => snkVentes.value.filter(isVendue).length)
const nbEnStock = computed(() => snkVentes.value.filter((v) => !isVendue(v)).length)
const valeurStock = computed(() =>
  snkVentes.value
    .filter((v) => !isVendue(v))
    .reduce((sum, v) => {
      const prix = prixRetailOf(v)
      if (Number.isNaN(prix)) return sum
      return sum + prix
    }, 0),
)

// Callbacks ajout/suppression
const handleVenteAjoutee = async () => {
  await chargerVentes()
}

const handleVenteSupprimee = (id) => {
  snkVentes.value = snkVentes.value.filter((v) => v.id !== id)
}

// Filtre recherche
const filteredVentes = computed(() => {
  if (!searchTerm.value) return snkVentes.value

  const term = searchTerm.value.toLowerCase()

  return snkVentes.value.filter((v) => {
    return (
      String(v.id).includes(term) ||
      v.nomItem?.toLowerCase().includes(term) ||
      v.categorie?.toLowerCase().includes(term) ||
      v.description?.toLowerCase().includes(term)
    )
  })
})

// édition
const openEditModal = (vente) => {
  venteToEdit.value = { ...vente }
  showEditModal.value = true
}

const handleVenteUpdated = (updated) => {
  const index = snkVentes.value.findIndex((v) => v.id === updated.id)
  if (index !== -1) {
    snkVentes.value[index] = updated
  }
}
</script>
