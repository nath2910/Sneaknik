<template>
  <div class="min-h-screen">
    <section
      class="mx-auto w-full max-w-6xl px-4 sm:px-6 lg:px-8 py-6 sm:py-10 space-y-6 sm:space-y-8"
    >
      <!-- Header -->
      <header class="flex flex-col gap-3 sm:gap-4">
        <DashboardHeader
          subtitle="Dashboard • Sneaknik"
          title="Sneaknik V5.2"
          description="Vue rapide de ton stock et de tes ventes du mois en cours."
        />
      </header>

      <!-- Contenu principal -->
      <div class="grid grid-cols-1 gap-6 sm:gap-8 lg:grid-cols-12 lg:items-start">
        <!-- Colonne droite (Overview) : en haut sur mobile, à droite sur desktop -->
        <aside class="order-1 lg:order-2 lg:col-span-4 w-full">
          <!-- option sticky sur desktop (si tu veux) -->
          <div class="lg:sticky lg:top-6">
            <HomeOverview
              :total-benefice="monthlyTotalBenefice"
              :total-c-a="monthlyTotalCA"
              :nb-vendues="monthlyNbVendues"
              :nb-en-stock="nbEnStock"
              :loading="loading"
              @go-gestion="goToGestion"
              @go-stats="goToStats"
            />
          </div>
        </aside>

        <!-- Colonne gauche : liste/dernier items -->
        <main class="order-2 lg:order-1 lg:col-span-8 min-w-0">
          <!-- petit wrapper responsive pour éviter l’effet “collé” -->
          <div class="rounded-2xl bg-white/0">
            <Affichage10 />
          </div>
        </main>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Affichage10 from '@/components/AcceuilAffichageDix.vue'
import HomeOverview from '@/components/AcceuilWidgetLateral.vue'
import DashboardHeader from '@/components/HeaderDePage.vue'
import SnkVenteServices from '@/services/SnkVenteServices.js'
import { useAuthStore } from '@/store/authStore'

const router = useRouter()

const { user } = useAuthStore()
const currentUser = user

const snkVentes = ref<any[]>([])
const loading = ref(false)

const chargerVentes = async () => {
  // ✅ ne lance pas l’API si pas connecté
  if (!currentUser.value) {
    snkVentes.value = []
    loading.value = false
    return
  }

  loading.value = true
  try {
    const { data } = await SnkVenteServices.getSnkVente()
    snkVentes.value = Array.isArray(data) ? data : []
  } catch (e) {
    console.error('Erreur chargement ventes (Accueil)', e)
    snkVentes.value = []
  } finally {
    loading.value = false
  }
}

onMounted(chargerVentes)

const isVendue = (v: any) => Boolean(v.dateVente ?? v.date_vente)
const prixRetailOf = (v: any) => Number(v.prixRetail ?? v.prix_retail ?? 0)
const prixResellOf = (v: any) => Number(v.prixResell ?? v.prix_resell ?? 0)

/**
 * Stats globales stock
 */
const nbEnStock = computed(() => snkVentes.value.filter((v) => !isVendue(v)).length)

/**
 * Stats du mois en cours
 */
const now = new Date()
const currentMonth = now.getMonth()
const currentYear = now.getFullYear()

const ventesDuMois = computed(() =>
  snkVentes.value.filter((v) => {
    const raw = v.dateVente ?? v.date_vente
    if (!raw) return false
    const d = new Date(raw)
    if (Number.isNaN(d.getTime())) return false
    return d.getMonth() === currentMonth && d.getFullYear() === currentYear
  }),
)

const monthlyNbVendues = computed(() => ventesDuMois.value.length)

const monthlyTotalCA = computed(() =>
  ventesDuMois.value.reduce((sum, v) => {
    const resell = prixResellOf(v)
    if (Number.isNaN(resell)) return sum
    return sum + resell
  }, 0),
)

const monthlyTotalBenefice = computed(() =>
  ventesDuMois.value.reduce((sum, v) => {
    const retail = prixRetailOf(v)
    const resell = prixResellOf(v)
    if (Number.isNaN(retail) || Number.isNaN(resell)) return sum
    return sum + (resell - retail)
  }, 0),
)

const goToGestion = () => router.push('/gestion')
const goToStats = () => router.push('/stats')
</script>
