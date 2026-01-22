<template>
  <TransitionRoot appear :show="open" as="template">
    <Dialog as="div" class="relative z-50" @close="$emit('close')">
      <TransitionChild
        as="template"
        enter="duration-200 ease-out"
        enterFrom="opacity-0"
        enterTo="opacity-100"
        leave="duration-150 ease-in"
        leaveFrom="opacity-100"
        leaveTo="opacity-0"
      >
        <div class="fixed inset-0 bg-black/60 backdrop-blur-sm" />
      </TransitionChild>

      <div class="fixed inset-0 overflow-y-auto">
        <div class="flex min-h-full items-center justify-center p-4">
          <TransitionChild
            as="template"
            enter="duration-200 ease-out"
            enterFrom="opacity-0 scale-95 translate-y-1"
            enterTo="opacity-100 scale-100 translate-y-0"
            leave="duration-150 ease-in"
            leaveFrom="opacity-100 scale-100 translate-y-0"
            leaveTo="opacity-0 scale-95 translate-y-1"
          >
            <DialogPanel
              class="w-full max-w-lg rounded-2xl border border-white/10 bg-[#0b1220] shadow-2xl"
            >
              <div class="px-5 py-4 border-b border-white/10 flex items-center">
                <DialogTitle class="text-white font-semibold text-lg">{{ title }}</DialogTitle>
                <button class="ml-auto text-white/70 hover:text-white" @click="$emit('close')">
                  ✕
                </button>
              </div>

              <div class="p-5 space-y-4">
                <div v-if="!fields.length" class="text-sm text-white/60">
                  Pas de réglages pour ce widget.
                </div>

                <div
                  v-for="f in fields"
                  :key="f.key"
                  v-if="!(f.type === 'date' && draft.useGlobalRange !== false)"
                  class="space-y-1"
                >
                  <div class="text-sm text-white/80 font-semibold">{{ f.label }}</div>

                  <select
                    v-if="f.type === 'select'"
                    v-model="draft[f.key]"
                    class="w-full h-10 rounded-xl border border-white/10 bg-white/5 text-white px-3"
                  >
                    <option v-for="o in f.options" :key="o.value" :value="o.value">
                      {{ o.label }}
                    </option>
                  </select>

                  <input
                    v-else-if="f.type === 'number'"
                    type="number"
                    v-model.number="draft[f.key]"
                    :min="f.min"
                    :max="f.max"
                    class="w-full h-10 rounded-xl border border-white/10 bg-white/5 text-white px-3"
                  />

                  <input
                    v-else-if="f.type === 'text'"
                    type="text"
                    v-model="draft[f.key]"
                    class="w-full h-10 rounded-xl border border-white/10 bg-white/5 text-white px-3"
                  />
                  <input
                    v-else-if="f.type === 'date'"
                    type="date"
                    v-model="draft[f.key]"
                    class="w-full h-10 rounded-xl border border-white/10 bg-white/5 text-white px-3"
                  />
                  <textarea
                    v-else-if="f.type === 'textarea'"
                    v-model="draft[f.key]"
                    rows="4"
                    class="w-full rounded-xl border border-white/10 bg-white/5 text-white px-3 py-2"
                  ></textarea>

                  <label
                    v-else-if="f.type === 'toggle'"
                    class="inline-flex items-center gap-2 text-white/80"
                  >
                    <input type="checkbox" v-model="draft[f.key]" />
                    <span class="text-sm">{{ f.hint ?? '' }}</span>
                  </label>

                  <div v-if="f.help" class="text-xs text-white/50">{{ f.help }}</div>
                </div>
              </div>

              <div class="px-5 py-4 border-t border-white/10 flex justify-end gap-2">
                <button
                  class="px-4 h-10 rounded-xl border border-white/10 bg-white/5 text-white/80 hover:bg-white/10"
                  @click="$emit('close')"
                >
                  Annuler
                </button>
                <button
                  class="px-4 h-10 rounded-xl bg-violet-600/80 text-white hover:bg-violet-600"
                  @click="$emit('save', draft)"
                >
                  Sauver
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { Dialog, DialogPanel, DialogTitle, TransitionChild, TransitionRoot } from '@headlessui/vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Réglages' },
  fields: { type: Array, default: () => [] },
  model: { type: Object, default: () => ({}) },
})
defineEmits(['close', 'save'])

const draft = reactive({})

watch(
  () => props.open,
  (isOpen) => {
    if (!isOpen) return
    Object.keys(draft).forEach((k) => delete draft[k])
    Object.assign(draft, props.model || {})
  },
  { immediate: true },
)
</script>
