<template>
  <div>
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <DashboardHeader
        subtitle="Analytics • Sneaknik"
        title="Statistiques"
        description="Analyse ton bénéfice, ton chiffre d’affaires et tes marques les plus fortes."
      />

      <!-- Résumé chiffré -->
      <section>
        <statBase />
      </section>

      <!-- Graphiques & panneaux -->
      <section class="grid grid-cols-1 lg:grid-cols-[minmax(0,1.4fr)_minmax(0,1fr)] gap-6">
        <!-- Graphique par marque -->
        <div class="bg-gray-800 border border-gray-700 rounded-3xl shadow-lg p-6">
          <h2 class="text-sm font-semibold text-gray-100 mb-3">Répartition par marque</h2>

          <div class="mt-4 pl-24 md:pl-32">
            <graphMarque />
          </div>
        </div>

        <!-- Top modèles + alertes -->
        <StatsSidePanels />
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import DashboardHeader from '@/components/dashbordHeader.vue'
import statBase from '@/components/statBase.vue'
import graphMarque from '@/components/graphMarque.vue'
import StatsSidePanels from '@/components/panneauStat.vue'
import { useAuthStore } from '@/store/authStore'

const { user } = useAuthStore()
const currentUser = user
</script>
