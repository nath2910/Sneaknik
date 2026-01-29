<template>
  <div class="w-full">
    <div
      class="group flex items-center gap-3 rounded-full border border-slate-800 bg-slate-900/70 px-5 py-3 shadow-md transition focus-within:border-violet-400/60 focus-within:ring-2 focus-within:ring-violet-500/30"
    >
      <svg
        class="h-5 w-5 text-slate-400 transition group-focus-within:text-violet-300"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M21 21l-4.35-4.35M5 11a6 6 0 1112 0 6 6 0 01-12 0z"
        />
      </svg>

      <input
        :value="modelValue"
        @input="$emit('update:modelValue', $event.target.value)"
        type="text"
        placeholder="Rechercher par modele, categorie, description..."
        class="flex-1 bg-transparent border-0 focus:outline-none focus:ring-0 text-sm text-slate-100 placeholder:text-slate-500"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

defineEmits(['update:modelValue'])
</script>
