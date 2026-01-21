<template>
  <div
    class="canvas-root"
    :data-edit="editMode ? 'true' : 'false'"
    @pointerdown="onCanvasPointerDown"
  >
    <CanvasDock
      :edit-mode="editMode"
      :scale="scale"
      :from="localFrom"
      :to="localTo"
      @toggleEdit="toggleEditMode"
      @openPalette="paletteOpen = true"
      @preset="preset"
      @zoomIn="zoomIn"
      @zoomOut="zoomOut"
      @resetZoom="resetZoom"
      @centerView="centerView"
      @resetLayout="resetLayout"
      @update:from="setFrom"
      @update:to="setTo"
    />

    <!-- Canvas -->
    <div ref="viewportEl" class="viewport">
      <div ref="boardEl" class="board">
        <WidgetFrame
          v-for="w in widgets"
          :key="w.id"
          :widget="w"
          :edit-mode="editMode"
          :drag-armed="dragArmedId === w.id"
          :comp="getComp(w.type)"
          :from="from"
          :to="to"
          :style="widgetStyle(w)"
          :ref="(c: any) => setWidgetRef(w.id, c)"
          @dragStart="startDrag(w.id, $event)"
          @settings="openSettings(w)"
          @remove="removeWidget(w.id)"
        />
      </div>
    </div>

    <!-- Palette -->
    <WidgetPalette
      :open="paletteOpen"
      :widgets="palette"
      @close="paletteOpen = false"
      @add="addWidget"
    />

    <!-- Settings -->
    <WidgetSettingsModal
      :open="settingsOpen"
      :title="settingsTitle"
      :fields="settingsFields"
      :model="settingsModel"
      @close="closeSettings"
      @save="applySettings"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, toRefs, watch } from 'vue'
import CanvasDock from './canvas/CanvasDock.vue'
import WidgetFrame from './canvas/WidgetFrame.vue'
import { useCanvasCamera } from './canvas/useCanvaCamera'

import WidgetPalette from './WidgetPalette.vue'
import WidgetSettingsModal from './WidgetSettingsModal.vue'
import { WIDGET_DEFS, getWidgetDef, newWidget } from './widgetRegistry'

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

/* props/emit */
const props = defineProps({
  from: { type: String, required: true },
  to: { type: String, required: true },
})
const emit = defineEmits(['update:from', 'update:to'])
const { from, to } = toRefs(props)

/* ===== Mode édition/figé ===== */
const EDIT_KEY = 'snk_stats_canvas_edit_v1'
const editMode = ref(
  localStorage.getItem(EDIT_KEY) ? localStorage.getItem(EDIT_KEY) === 'true' : true,
)

function persistEditMode() {
  localStorage.setItem(EDIT_KEY, String(editMode.value))
}
function toggleEditMode() {
  editMode.value = !editMode.value
  persistEditMode()
}

/* ===== Dates (local + safe sync) ===== */
const localFrom = ref(from.value)
const localTo = ref(to.value)

watch(
  () => [from.value, to.value],
  () => {
    localFrom.value = from.value
    localTo.value = to.value
  },
)

function setFrom(v: string) {
  if (!v) return
  localFrom.value = v
  emit('update:from', v)
  if (v > localTo.value) {
    localTo.value = v
    emit('update:to', v)
  }
}
function setTo(v: string) {
  if (!v) return
  localTo.value = v
  emit('update:to', v)
  if (v < localFrom.value) {
    localFrom.value = v
    emit('update:from', v)
  }
}

function preset(kind: 'month' | 'ytd' | 'year') {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  const ymd = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`

  if (kind === 'month') {
    setFrom(ymd(new Date(now.getFullYear(), now.getMonth(), 1)))
    setTo(ymd(new Date(now.getFullYear(), now.getMonth() + 1, 0)))
  } else if (kind === 'ytd') {
    setFrom(ymd(new Date(now.getFullYear(), 0, 1)))
    setTo(ymd(now))
  } else {
    setFrom(ymd(new Date(now.getFullYear(), 0, 1)))
    setTo(ymd(new Date(now.getFullYear(), 11, 31)))
  }
}

/* ===== Layout ===== */
const GRID = 10
const MIN_W = 320
const MIN_H = 220
const BOARD_W = 9000
const BOARD_H = 6000

const clamp = (n: number, a: number, b: number) => Math.max(a, Math.min(b, n))
const snap = (n: number) => Math.round(n / GRID) * GRID

function clampWidget(w: Widget) {
  w.w = clamp(w.w, MIN_W, BOARD_W)
  w.h = clamp(w.h, MIN_H, BOARD_H)
  w.x = clamp(w.x, 0, BOARD_W - w.w)
  w.y = clamp(w.y, 0, BOARD_H - w.h)
}

const STORAGE_KEY = 'snk_stats_canvas_layout_v4'

function loadLayout(): Widget[] | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as Widget[]) : null
  } catch {
    return null
  }
}

const defaultLayout = (): Widget[] => [
  //mettre un widget de texte
]

function normalizeLayout(raw: unknown): Widget[] | null {
  if (!Array.isArray(raw)) return null

  const list: Widget[] = []
  for (const item of raw) {
    const def = getWidgetDef((item as any)?.type)
    if (!def) continue

    const w: Widget = {
      id:
        typeof (item as any)?.id === 'string'
          ? (item as any).id
          : `${def.type}_${Date.now()}_${Math.random().toString(16).slice(2)}`,
      type: def.type,
      title: typeof (item as any)?.title === 'string' ? (item as any).title : def.title,
      x: Number.isFinite((item as any)?.x) ? Number((item as any).x) : 0,
      y: Number.isFinite((item as any)?.y) ? Number((item as any).y) : 0,
      w: Number.isFinite((item as any)?.w) ? Number((item as any).w) : def.defaultSize.w,
      h: Number.isFinite((item as any)?.h) ? Number((item as any).h) : def.defaultSize.h,
      props: { ...def.defaultProps, ...((item as any)?.props ?? {}) },
    }

    clampWidget(w)
    list.push(w)
  }

  return list
}

const widgets = ref<Widget[]>(normalizeLayout(loadLayout()) ?? defaultLayout())

let saveTimer: number | null = null
function saveLayoutNow() {
  const minimal = widgets.value.map(({ id, type, title, x, y, w, h, props }) => ({
    id,
    type,
    title,
    x,
    y,
    w,
    h,
    props,
  }))
  localStorage.setItem(STORAGE_KEY, JSON.stringify(minimal))
}
function scheduleSave() {
  if (saveTimer) window.clearTimeout(saveTimer)
  saveTimer = window.setTimeout(() => {
    saveLayoutNow()
    saveTimer = null
  }, 250)
}

function widgetStyle(w: Widget) {
  return {
    width: `${w.w}px`,
    height: `${w.h}px`,
    transform: `translate3d(${w.x}px, ${w.y}px, 0)`,
    zIndex: w.z ?? 1,
  }
}

/* ===== Widget registry ===== */
const paletteOpen = ref(false)
const palette = WIDGET_DEFS.map((w) => ({
  type: w.type,
  title: w.title,
  icon: w.icon,
  help: w.help ?? 'Ajoute ce widget au canvas',
}))
const dragArmedId = ref<string | null>(null)

function getComp(type: string) {
  return getWidgetDef(type)?.component
}

/* ===== Settings ===== */
const settingsOpen = ref(false)
const settingsWidgetId = ref<string | null>(null)

const settingsWidget = computed(
  () => widgets.value.find((x) => x.id === settingsWidgetId.value) ?? null,
)
const settingsDef = computed(() =>
  settingsWidget.value ? getWidgetDef(settingsWidget.value.type) : null,
)

const settingsTitle = computed(() => settingsWidget.value?.title ?? 'Réglages')
const settingsFields = computed(() => settingsDef.value?.settings ?? [])
const settingsModel = computed(() => ({ ...(settingsWidget.value?.props ?? {}) }))

function openSettings(w: Widget) {
  settingsWidgetId.value = w.id
  settingsOpen.value = true
}
function closeSettings() {
  settingsOpen.value = false
  settingsWidgetId.value = null
}

function onCanvasPointerDown(e: PointerEvent) {
  if (!editMode.value) return
  const target = e.target as HTMLElement | null
  if (target?.closest('.widget')) return
  dragArmedId.value = null
}
function applySettings(newModel: Record<string, unknown>) {
  const w = settingsWidget.value
  if (!w) return
  w.props = { ...(w.props ?? {}), ...newModel }
  scheduleSave()
  closeSettings()
}

/* ===== Camera (Panzoom) ===== */
const viewportEl = ref<HTMLElement | null>(null)
const boardEl = ref<HTMLElement | null>(null)

const camera = useCanvasCamera(viewportEl, boardEl, {
  boardWidth: BOARD_W,
  boardHeight: BOARD_H,
  maxScale: 3,
  minScale: 0.15,
  contain: 'outside',
  excludeClass: 'panzoom-exclude',
})

const scale = computed(() => camera.scale.value)

function widgetsBounds() {
  const list = widgets.value
  if (!list.length) return { cx: BOARD_W / 2, cy: BOARD_H / 2 }

  let minX = Infinity,
    minY = Infinity,
    maxX = -Infinity,
    maxY = -Infinity

  for (const w of list) {
    minX = Math.min(minX, w.x)
    minY = Math.min(minY, w.y)
    maxX = Math.max(maxX, w.x + w.w)
    maxY = Math.max(maxY, w.y + w.h)
  }
  return { cx: (minX + maxX) / 2, cy: (minY + maxY) / 2 }
}

function centerView() {
  const { cx, cy } = widgetsBounds()
  camera.centerOn(cx, cy)
}

function zoomIn() {
  camera.zoomIn()
}
function zoomOut() {
  camera.zoomOut()
}
function resetZoom() {
  camera.resetZoom()
}

/* ===== Drag ===== */
const widgetEls = new Map<string, HTMLElement>()
type DragState = {
  x: number
  y: number
  scale: number
  lastX: number
  lastY: number
  raf: number | null
}
const dragStates = new Map<string, DragState>()
let activeDragId: string | null = null
let zTop = 10

function setWidgetRef(id: string, c: any) {
  const el = (c?.root?.value ?? c?.$el ?? null) as HTMLElement | null
  if (el) {
    widgetEls.set(id, el)
  } else {
    const dragEl = widgetEls.get(id)
    if (dragEl) dragEl.classList.remove('is-dragging')
    widgetEls.delete(id)
    clearDragState(id)
  }
}

function applyWidgetDOM(el: HTMLElement, w: Widget) {
  el.style.width = `${w.w}px`
  el.style.height = `${w.h}px`
  el.style.transform = `translate3d(${w.x}px, ${w.y}px, 0)`
}

function applyWidgetDOMAt(el: HTMLElement, w: Widget, x: number, y: number) {
  el.style.width = `${w.w}px`
  el.style.height = `${w.h}px`
  el.style.transform = `translate3d(${x}px, ${y}px, 0)`
}

function clampWidgetPosition(x: number, y: number, w: Widget) {
  return {
    x: clamp(x, 0, BOARD_W - w.w),
    y: clamp(y, 0, BOARD_H - w.h),
  }
}

function rectsOverlap(
  a: { x: number; y: number; w: number; h: number },
  b: { x: number; y: number; w: number; h: number },
  pad = GRID,
) {
  return !(
    a.x + a.w + pad <= b.x ||
    b.x + b.w + pad <= a.x ||
    a.y + a.h + pad <= b.y ||
    b.y + b.h + pad <= a.y
  )
}

function placeWidget(w: Widget, centerX: number, centerY: number) {
  const baseX = snap(centerX - w.w / 2)
  const baseY = snap(centerY - w.h / 2)
  const step = GRID * 6
  const maxR = Math.max(BOARD_W, BOARD_H)

  const hasOverlap = (x: number, y: number) =>
    widgets.value.some((o) => rectsOverlap({ x, y, w: w.w, h: w.h }, o))

  const base = clampWidgetPosition(baseX, baseY, w)
  if (!hasOverlap(base.x, base.y)) return base

  for (let r = step; r <= maxR; r += step) {
    for (let dx = -r; dx <= r; dx += step) {
      for (let dy = -r; dy <= r; dy += step) {
        if (Math.abs(dx) !== r && Math.abs(dy) !== r) continue
        const p = clampWidgetPosition(baseX + dx, baseY + dy, w)
        if (!hasOverlap(p.x, p.y)) return p
      }
    }
  }

  return base
}

function clearDragState(id: string) {
  const state = dragStates.get(id)
  if (!state) return
  if (state.raf) cancelAnimationFrame(state.raf)
  dragStates.delete(id)
}

function scheduleDragApply(el: HTMLElement, w: Widget, state: DragState) {
  if (state.raf) return
  state.raf = requestAnimationFrame(() => {
    state.raf = null
    applyWidgetDOMAt(el, w, state.x, state.y)
  })
}

function startDrag(id: string, event: PointerEvent) {
  if (!editMode.value) return
  const w = widgets.value.find((x) => x.id === id)
  const el = widgetEls.get(id)
  if (!w || !el) return

  event.preventDefault()
  event.stopPropagation()

  dragArmedId.value = id
  w.z = ++zTop
  activeDragId = id

  const s = Number(camera.scale.value || 1)
  dragStates.set(id, {
    x: w.x,
    y: w.y,
    scale: s > 0 ? s : 1,
    lastX: event.clientX,
    lastY: event.clientY,
    raf: null,
  })

  el.classList.add('is-dragging')
  setCanvasPanEnabled(false)

  window.addEventListener('pointermove', onGlobalPointerMove)
  window.addEventListener('pointerup', onGlobalPointerUp, { once: true })
  window.addEventListener('pointercancel', onGlobalPointerUp, { once: true })
}

function onGlobalPointerMove(event: PointerEvent) {
  if (!activeDragId) return
  const state = dragStates.get(activeDragId)
  const w = widgets.value.find((x) => x.id === activeDragId)
  const el = widgetEls.get(activeDragId)
  if (!state || !w || !el) return

  const dx = event.clientX - state.lastX
  const dy = event.clientY - state.lastY
  state.lastX = event.clientX
  state.lastY = event.clientY

  state.x += dx / state.scale
  state.y += dy / state.scale

  const clamped = clampWidgetPosition(state.x, state.y, w)
  state.x = clamped.x
  state.y = clamped.y

  scheduleDragApply(el, w, state)
}

function finishDrag(id: string) {
  const state = dragStates.get(id)
  const w = widgets.value.find((x) => x.id === id)
  const el = widgetEls.get(id)
  if (el) el.classList.remove('is-dragging')

  if (!state || !w) {
    clearDragState(id)
    setCanvasPanEnabled(true)
    activeDragId = null
    return
  }

  const snapped = clampWidgetPosition(snap(state.x), snap(state.y), w)
  w.x = snapped.x
  w.y = snapped.y
  clampWidget(w)
  if (el) applyWidgetDOM(el, w)

  clearDragState(id)
  setCanvasPanEnabled(true)
  scheduleSave()
  activeDragId = null
  if (dragArmedId.value === id) dragArmedId.value = null
}

function onGlobalPointerUp() {
  window.removeEventListener('pointermove', onGlobalPointerMove)
  if (!activeDragId) return
  if (!dragStates.has(activeDragId)) {
    activeDragId = null
    setCanvasPanEnabled(true)
    return
  }
  finishDrag(activeDragId)
}
/** active/désactive le pan du board */
function setCanvasPanEnabled(enabled: boolean) {
  const pz = camera.getPanzoom()
  pz?.setOptions?.({ disablePan: !enabled })
}

function detachAllInteract() {
  window.removeEventListener('pointermove', onGlobalPointerMove)
  widgetEls.forEach((el) => el.classList.remove('is-dragging'))
  dragStates.clear()
  activeDragId = null
  setCanvasPanEnabled(true)
}

watch(editMode, async (enabled) => {
  if (!enabled) {
    dragArmedId.value = null
    detachAllInteract()
    return
  }
})

/* ===== Actions ===== */
function removeWidget(id: string) {
  if (!editMode.value) return
  widgets.value = widgets.value.filter((w) => w.id !== id)
  scheduleSave()
}

function resetLayout() {
  if (!editMode.value) return

  // on ferme les modales si besoin
  paletteOpen.value = false
  closeSettings()

  // on débranche tout et on vide
  detachAllInteract()
  widgets.value = []

  nextTick(() => {
    // recentre la caméra au milieu de la board (vu que plus de widgets)
    centerView()
    // save immédiat (pour être sûr que le layout vide est persisté)
    saveLayoutNow()
  })
}

function addWidget(type: string) {
  if (!editMode.value) return
  paletteOpen.value = false

  let w: Widget
  try {
    w = newWidget(type, 0, 0) as Widget
  } catch (err) {
    console.warn('[stats] addWidget failed', err)
    return
  }

  const p = camera.boardPointFromViewportCenter()
  const placed = placeWidget(w, p.x, p.y)
  w.x = placed.x
  w.y = placed.y
  w.z = ++zTop
  clampWidget(w)

  widgets.value.push(w)

  nextTick(() => {
    scheduleSave()
  })
}

/* ===== Lifecycle ===== */
onMounted(async () => {
  // init camera + centre quand la vue est prête
  camera.init(() => {
    centerView()
  })

  await nextTick()

  widgets.value.forEach((w) => clampWidget(w))
  if (editMode.value) disarmWidget()
})

onBeforeUnmount(() => {
  detachAllInteract()
  camera.destroy()
})
</script>

<style scoped>
.canvas-root {
  --bg: #060a12;
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: var(--bg);
}

.viewport {
  position: absolute;
  inset: 0;
  overflow: hidden;
}
.viewport,
.board {
  touch-action: none;
}

.board {
  width: 9000px;
  height: 6000px;
  position: relative;
  background:
    radial-gradient(circle at 1px 1px, rgba(170, 200, 255, 0.1) 1px, transparent 1.6px) 0 0 / 28px
      28px,
    radial-gradient(circle at 1px 1px, rgba(255, 255, 255, 0.04) 1px, transparent 1.8px) 0 0 / 8px
      8px,
    linear-gradient(180deg, #050812 0%, #040611 100%);
}

/* ✅ en mode édition, on peut drag “partout” (même sur les charts) */
</style>


