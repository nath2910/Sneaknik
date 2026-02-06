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
        <div class="fixed inset-0 bg-black/45 backdrop-blur-md" />
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
            <DialogPanel class="glass-panel w-full max-w-lg rounded-[22px]">
              <div class="glass-header px-5 py-4 flex items-center">
                <DialogTitle class="text-white font-semibold text-lg">{{ title }}</DialogTitle>
                <button
                  class="glass-iconbtn ml-auto h-8 w-8 grid place-items-center rounded-xl"
                  @click="$emit('close')"
                >
                  <span class="close-x" aria-hidden="true"></span>
                </button>
              </div>

              <div class="p-5 space-y-4">
                <div v-if="!fields.length" class="text-sm text-white/60">
                  Pas de reglages pour ce widget.
                </div>

                <template v-for="f in fields" :key="f.key">
                  <div
                    v-if="!(f.hideWhenGlobalRange && draft.useGlobalRange !== false)"
                    class="space-y-1"
                  >
                    <div class="text-sm text-white/80 font-semibold">{{ f.label }}</div>

                    <select
                      v-if="f.type === 'select'"
                      v-model="draft[f.key]"
                      class="glass-field w-full h-10 rounded-xl px-3"
                    >
                      <option v-for="o in f.options" :key="o.value" :value="o.value">
                        {{ o.label }}
                      </option>
                    </select>

                    <div v-else-if="f.type === 'number'" class="number-field">
                      <button
                        type="button"
                        class="glass-iconbtn number-btn"
                        @click="stepNumber(f, -1)"
                      >
                        -
                      </button>
                      <input
                        type="number"
                        v-model.number="draft[f.key]"
                        :min="f.min"
                        :max="f.max"
                        :step="f.step ?? 1"
                        class="glass-field number-input"
                        @blur="clampNumberField(f)"
                      />
                      <button
                        type="button"
                        class="glass-iconbtn number-btn"
                        @click="stepNumber(f, 1)"
                      >
                        +
                      </button>
                      <div v-if="hasNumberMeta(f)" class="number-meta">
                        <span v-if="f.min !== undefined">min {{ f.min }}</span>
                        <span v-if="f.max !== undefined">max {{ f.max }}</span>
                        <span v-if="f.step">pas {{ f.step }}</span>
                        <span v-if="f.unit">{{ f.unit }}</span>
                      </div>
                    </div>

                    <input
                      v-else-if="f.type === 'text'"
                      type="text"
                      v-model="draft[f.key]"
                      class="glass-field w-full h-10 rounded-xl px-3"
                    />
                    <CompactDateInput
                      v-else-if="f.type === 'date'"
                      v-model="draft[f.key]"
                      :min-date="minDate"
                      :max-date="maxDate"
                      class="w-full"
                    />
                    <textarea
                      v-else-if="f.type === 'textarea'"
                      v-model="draft[f.key]"
                      rows="4"
                      class="glass-field w-full rounded-xl px-3 py-2"
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
                </template>
              </div>

              <div class="glass-footer px-5 py-4 flex justify-end gap-2">
                <button
                  class="glass-btn px-4 h-10 rounded-xl"
                  @click="$emit('close')"
                >
                  Annuler
                </button>
                <button
                  class="glass-btn glass-btn-primary px-4 h-10 rounded-xl"
                  @click="onSave"
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
import CompactDateInput from '@/components/ui/CompactDateInput.vue'

const props = defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Reglages' },
  fields: { type: Array, default: () => [] },
  model: { type: Object, default: () => ({}) },
  minDate: { type: String, default: '' },
  maxDate: { type: String, default: '' },
})
const emit = defineEmits(['close', 'save'])

const draft = reactive({})

function clampNumberField(field) {
  const raw = Number(draft[field.key] ?? 0)
  const min = field.min ?? -Infinity
  const max = field.max ?? Infinity
  const next = Math.min(Math.max(raw, min), max)
  if (!Number.isFinite(next)) return
  draft[field.key] = next
}

function stepNumber(field, direction) {
  const step = Number(field.step ?? 1)
  const current = Number(draft[field.key] ?? 0)
  const next = current + step * direction
  draft[field.key] = next
  clampNumberField(field)
}

function hasNumberMeta(field) {
  return field.min !== undefined || field.max !== undefined || field.step || field.unit
}

function normalizeDraft() {
  for (const field of props.fields || []) {
    if (field?.type !== 'number') continue
    const raw = Number(draft[field.key] ?? 0)
    const min = field.min ?? -Infinity
    const max = field.max ?? Infinity
    const next = Math.min(Math.max(raw, min), max)
    if (Number.isFinite(next)) {
      draft[field.key] = next
    }
  }
}

function onSave() {
  normalizeDraft()
  emit('save', { ...draft })
}

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

<style scoped>
.close-x {
  position: relative;
  width: 12px;
  height: 12px;
  display: inline-block;
}
.close-x::before,
.close-x::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  width: 12px;
  height: 2px;
  background: currentColor;
  transform-origin: center;
}
.close-x::before {
  transform: translate(-50%, -50%) rotate(45deg);
}
.close-x::after {
  transform: translate(-50%, -50%) rotate(-45deg);
}
.glass-panel {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(8, 10, 16, 0.62);
  backdrop-filter: blur(24px) saturate(140%) contrast(105%);
  box-shadow:
    0 25px 70px rgba(0, 0, 0, 0.65),
    inset 0 1px 0 rgba(255, 255, 255, 0.18);
}
.glass-panel::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.08), transparent 55%);
  opacity: 0.5;
  pointer-events: none;
}
.glass-panel::after {
  content: '';
  position: absolute;
  inset: -1px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.06), transparent 45%);
  opacity: 0.35;
  pointer-events: none;
}
.glass-panel > * {
  position: relative;
  z-index: 1;
}
.glass-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(10, 12, 18, 0.55);
  backdrop-filter: blur(16px) saturate(140%);
}
.glass-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.12);
}
.glass-iconbtn {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.18);
}
.glass-iconbtn:hover {
  background: rgba(255, 255, 255, 0.14);
}
.glass-iconbtn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.14), transparent 60%);
  opacity: 0.35;
  pointer-events: none;
}
.glass-field {
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px) saturate(120%);
}
.glass-btn {
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.18),
    0 8px 18px rgba(0, 0, 0, 0.25);
}
.glass-btn:hover {
  background: rgba(255, 255, 255, 0.16);
}
.glass-btn::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.14), transparent 60%);
  opacity: 0.35;
  pointer-events: none;
}
.glass-btn-primary {
  background: rgba(255, 255, 255, 0.2);
}
.glass-btn-primary::before {
  opacity: 0.45;
}
.number-field {
  display: grid;
  grid-template-columns: 34px 1fr 34px;
  align-items: center;
  gap: 8px;
}
.number-input {
  height: 40px;
  border-radius: 12px;
  padding: 0 12px;
  text-align: center;
  font-weight: 600;
  letter-spacing: 0.02em;
}
.number-btn {
  height: 40px;
  width: 34px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
}
.number-meta {
  grid-column: 1 / -1;
  display: flex;
  gap: 10px;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: rgba(226, 232, 240, 0.5);
}
</style>
