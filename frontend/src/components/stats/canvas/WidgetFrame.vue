<template>
  <div
    ref="root"
    class="widget panzoom-exclude"
    :data-id="widget.id"
    :data-edit="editMode ? 'true' : 'false'"
    :class="{ 'drag-armed': dragArmed, 'widget--tight': widget.props?.tight }"
    :style="style"
    @pointerdown="onPointerDown"
  >
    <div class="widget__header drag-handle">
      <div class="widget__title">
        <span class="dot" />
        <span class="title">{{ widget.title }}</span>
        <span v-if="editMode" class="drag-grip" aria-hidden="true" />
      </div>

      <div class="widget__actions" v-if="editMode">
        <button type="button" class="iconbtn" title="Réglages" @click="$emit('settings')">
          <Settings class="w-4 h-4" />
        </button>
        <button type="button" class="iconbtn" title="Supprimer" @click="$emit('remove')">
          <Trash2 class="w-4 h-4" />
        </button>
      </div>
    </div>

    <div class="widget__body">
      <component :is="comp" :from="from" :to="to" v-bind="widget.props" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Settings, Trash2 } from 'lucide-vue-next'

type Widget = {
  id: string
  type: string
  title: string
  x: number
  y: number
  w: number
  h: number
  props?: Record<string, unknown>
  z?: number
}

const props = defineProps<{
  widget: Widget
  editMode: boolean
  dragArmed: boolean
  comp: any
  from: string
  to: string
  style: Record<string, string | number>
}>()

const emit = defineEmits<{
  (e: 'settings'): void
  (e: 'remove'): void
  (e: 'dragStart', ev: PointerEvent): void
}>()

function onPointerDown(e: PointerEvent) {
  if (!props.editMode) return
  const target = e.target as HTMLElement | null
  if (target?.closest('button, a, input, select, textarea, .iconbtn')) return
  emit('dragStart', e)
}

const root = ref<HTMLElement | null>(null)
defineExpose({ root })
</script>

<style scoped>
.widget {
  position: absolute;
  border-radius: 22px;
  overflow: hidden;
  background: rgba(17, 24, 39, 0.82);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.45);
  will-change: transform, width, height;
  contain: layout paint style;
  touch-action: none;
  transition:
    border-color 160ms ease,
    box-shadow 160ms ease;
}
.widget.is-dragging {
  cursor: grabbing;
  transition: none;
}
.widget:hover {
  border-color: rgba(255, 255, 255, 0.14);
  box-shadow: 0 24px 70px rgba(0, 0, 0, 0.55);
}
.widget.drag-armed {
  border-color: rgba(139, 92, 246, 0.6);
  box-shadow: 0 18px 50px rgba(139, 92, 246, 0.15);
  cursor: grab;
}

.widget__header {
  height: 44px;
  display: flex;
  align-items: center;
  padding: 0 10px 0 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(10, 15, 30, 0.88);
}

.widget__title {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  user-select: none;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #8b5cf6;
  box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.15);
}
.title {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 650;
  font-size: 0.9rem;
}
.drag-grip {
  width: 14px;
  height: 14px;
  border-radius: 6px;
  opacity: 0.7;
  background:
    radial-gradient(circle, rgba(255, 255, 255, 0.45) 1px, transparent 1.5px) 0 0 / 6px 6px;
}

.widget__actions {
  margin-left: auto;
  display: inline-flex;
  gap: 8px;
}
.iconbtn {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.92);
  display: grid;
  place-items: center;
  transition:
    background 160ms ease,
    border-color 160ms ease;
}
.iconbtn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.16);
}

/* en édition : on peut drag en cliquant partout */
.widget[data-edit='true'] .drag-handle {
  cursor: grab;
}
.widget.is-dragging .drag-handle {
  cursor: grabbing;
}

.widget__body {
  height: calc(100% - 44px);
  padding: 12px;
}
.widget--tight .widget__body {
  padding: 6px;
}
</style>
