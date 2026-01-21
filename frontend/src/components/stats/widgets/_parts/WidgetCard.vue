<template>
  <div
    class="h-full rounded-2xl border border-slate-800/80 bg-slate-900/50 shadow-[0_10px_30px_rgba(0,0,0,.35)] backdrop-blur p-4 flex flex-col"
  >
    <div class="flex items-start justify-between gap-3 mb-3">
      <div class="min-w-0">
        <p v-if="subtitle" class="text-[11px] text-white/55 truncate">{{ subtitle }}</p>
        <p class="text-sm font-semibold text-white truncate">{{ title }}</p>
      </div>

      <div class="flex items-center gap-2">
        <slot name="actions" />
        <span class="h-2.5 w-2.5 rounded-full" :style="{ backgroundColor: accent }" />
      </div>
    </div>

    <div class="flex-1 min-h-0">
      <div v-if="loading" class="h-full flex items-center justify-center">
        <div class="animate-pulse text-xs text-white/50">Chargement…</div>
      </div>

      <div v-else-if="error" class="h-full flex items-center justify-center">
        <div class="text-xs text-red-300/90">Erreur : {{ error }}</div>
      </div>

      <div v-else class="h-full">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  accent: { type: String, default: '#8B5CF6' },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
})
</script>
