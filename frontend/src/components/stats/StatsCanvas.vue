<template>
  <div
    class="canvas-root"
    :data-edit="editMode ? 'true' : 'false'"
    @pointerdown="onCanvasPointerDown"
  >
    <CanvasDock
      :edit-mode="editMode"
      :scale="scale"
      :palette-open="paletteOpen"
      @toggleEdit="toggleEditMode"
      @openPalette="paletteOpen = true"
      @zoomIn="zoomIn"
      @zoomOut="zoomOut"
      @resetZoom="resetZoom"
      @centerView="centerView"
      @resetLayout="resetLayout"
    />

    <!-- Canvas -->
    <div ref="viewportEl" class="viewport">
      <div class="date-panel">
        <div class="date-title">Periode</div>
        <div class="date-row">
          <label class="date-field">
            <span>Du</span>
            <input
              class="date-input"
              type="date"
              :value="localFrom"
              @input="setFrom($event.target.value)"
            />
          </label>
          <label class="date-field">
            <span>Au</span>
            <input
              class="date-input"
              type="date"
              :value="localTo"
              @input="setTo($event.target.value)"
            />
          </label>
        </div>
        <div class="date-actions">
          <button type="button" class="date-chip" @click="preset('month')">Mois</button>
          <button type="button" class="date-chip" @click="preset('ytd')">YTD</button>
          <button type="button" class="date-chip" @click="preset('year')">Annee</button>
        </div>
      </div>
      <div v-if="editMode" class="edit-grid" aria-hidden="true"></div>
      <div
        v-if="snapGuides.x !== null"
        class="snap-guide snap-guide--x"
        :style="{ left: `${snapGuides.x}px` }"
        aria-hidden="true"
      ></div>
      <div
        v-if="snapGuides.y !== null"
        class="snap-guide snap-guide--y"
        :style="{ top: `${snapGuides.y}px` }"
        aria-hidden="true"
      ></div>
      <div ref="boardEl" class="board">
        <WidgetFrame
          v-for="w in widgets"
          :key="w.id"
          :widget="w"
          :edit-mode="editMode"
          :drag-armed="dragArmedId === w.id"
          :comp="getComp(w.type)"
          :from="widgetFrom(w)"
          :to="widgetTo(w)"
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

    <Transition name="save-toast">
      <div v-if="showSaveToast" class="save-toast" role="status">Layout enregistre</div>
    </Transition>

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
import { useAuthStore } from '@/store/authStore'
import StatsServices from '@/services/StatsServices'

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

const { user } = useAuthStore()
// Chaque utilisateur a une cle de layout isolee; guest reste en stockage local.
const userId = computed(() => user.value?.id ?? 'guest')

/* ===== Mode édition/figé ===== */
// Le mode edition est stocke par utilisateur pour eviter les fuites d'etat UI.
const EDIT_KEY_PREFIX = 'snk_stats_canvas_edit_v1'
const editKey = computed(() => `${EDIT_KEY_PREFIX}_${userId.value}`)
const editMode = ref(true)

function loadEditMode() {
  const raw = localStorage.getItem(editKey.value)
  editMode.value = raw ? raw === 'true' : true
}
loadEditMode()

function persistEditMode() {
  localStorage.setItem(editKey.value, String(editMode.value))
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

function minSizeFor(w: Widget) {
  const def = getWidgetDef(w.type)
  return {
    w: def?.minSize?.w ?? MIN_W,
    h: def?.minSize?.h ?? MIN_H,
  }
}

function clampWidget(w: Widget) {
  const minSize = minSizeFor(w)
  w.w = clamp(w.w, minSize.w, BOARD_W)
  w.h = clamp(w.h, minSize.h, BOARD_H)
  w.x = clamp(w.x, 0, BOARD_W - w.w)
  w.y = clamp(w.y, 0, BOARD_H - w.h)
}

// Layout persiste par utilisateur; fallback sur l'ancienne cle globale si presente.
const STORAGE_KEY_PREFIX = 'snk_stats_canvas_layout_v4'
const layoutKey = computed(() => `${STORAGE_KEY_PREFIX}_${userId.value}`)

function loadLayout(key: string): unknown | null {
  try {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

// Widget d'accueil par defaut quand aucun layout n'existe.
const defaultLayout = (): Widget[] => {
  const def = getWidgetDef('textBlock')
  if (!def) return []

  const w: Widget = {
    id: 'textBlock_welcome',
    type: def.type,
    title: def.title,
    x: (BOARD_W - def.defaultSize.w) / 2,
    y: (BOARD_H - def.defaultSize.h) / 2,
    w: def.defaultSize.w,
    h: def.defaultSize.h,
    props: {
      ...def.defaultProps,
      content:
        "Bienvenue sur ton espace stats. Ajoute des widgets depuis la palette pour composer ton dashboard.",
      align: 'center',
    },
  }

  clampWidget(w)
  return [w]
}

function normalizeLayout(raw: unknown): Widget[] | null {
  if (!Array.isArray(raw)) return null

  const list: Widget[] = []
  for (const item of raw) {
    let type = (item as any)?.type
    let def = getWidgetDef(type)
    if (!def && type === 'textSection') {
      type = 'textBlock'
      def = getWidgetDef(type)
    }
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

function loadLayoutForUser() {
  const raw = loadLayout(layoutKey.value) ?? loadLayout(STORAGE_KEY_PREFIX)
  return normalizeLayout(raw) ?? defaultLayout()
}

const widgets = ref<Widget[]>(loadLayoutForUser())

let saveTimer: number | null = null
let toastTimer: number | null = null
let remoteSaveTimer: number | null = null
const showSaveToast = ref(false)

function showSavedToast() {
  showSaveToast.value = true
  if (toastTimer) window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    showSaveToast.value = false
    toastTimer = null
  }, 1600)
}

// Chargement du layout serveur pour plus de robustesse (prive / multi-appareil).
async function loadLayoutFromServer() {
  try {
    const res = await StatsServices.getLayout()
    const payload = res?.data?.layout
    if (payload === null || typeof payload === 'undefined') {
      return
    }
    const normalized = normalizeLayout(payload)
    if (normalized) {
      widgets.value = normalized
      localStorage.setItem(layoutKey.value, JSON.stringify(payload))
    }
  } catch (err) {
    console.warn('[stats] remote load failed', err)
  }
}

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
  localStorage.setItem(layoutKey.value, JSON.stringify(minimal))
  showSavedToast()
  scheduleRemoteSave(minimal)
}
function scheduleSave() {
  if (saveTimer) window.clearTimeout(saveTimer)
  saveTimer = window.setTimeout(() => {
    saveLayoutNow()
    saveTimer = null
  }, 250)
}

// Sauvegarde distante debounce pour eviter de spammer l'API.
function scheduleRemoteSave(minimal: Array<Record<string, unknown>>) {
  if (userId.value === 'guest') return
  if (remoteSaveTimer) window.clearTimeout(remoteSaveTimer)
  remoteSaveTimer = window.setTimeout(async () => {
    try {
      await StatsServices.saveLayout(minimal)
    } catch (err) {
      console.warn('[stats] remote save failed', err)
    } finally {
      remoteSaveTimer = null
    }
  }, 800)
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

function widgetFrom(w: Widget) {
  const useGlobal = w.props?.useGlobalRange !== false
  return useGlobal ? from.value : (w.props?.from ?? from.value)
}

function widgetTo(w: Widget) {
  const useGlobal = w.props?.useGlobalRange !== false
  return useGlobal ? to.value : (w.props?.to ?? to.value)
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
const settingsFields = computed(() => {
  const base = settingsDef.value?.settings ?? []
  return [
    {
      key: 'useGlobalRange',
      label: 'Utiliser periode globale',
      type: 'toggle',
      hint: 'Active par defaut',
    },
    { key: 'from', label: 'Du', type: 'date' },
    { key: 'to', label: 'Au', type: 'date' },
    ...base,
  ]
})
const settingsModel = computed(() => {
  const base = settingsWidget.value?.props ?? {}
  return {
    useGlobalRange: base.useGlobalRange ?? true,
    from: base.from ?? localFrom.value,
    to: base.to ?? localTo.value,
    ...base,
  }
})

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
const snapGuides = ref<{ x: number | null; y: number | null }>({ x: null, y: null })
const SNAP_DIST = 8

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

function snapValue(value: number, targets: number[]) {
  let best = value
  let bestDiff = SNAP_DIST + 1
  for (const t of targets) {
    const diff = Math.abs(value - t)
    if (diff < bestDiff) {
      bestDiff = diff
      best = t
    }
  }
  return best
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

  // snap vs other widgets (center + edges)
  const targetsX: number[] = []
  const targetsY: number[] = []
  for (const other of widgets.value) {
    if (other.id === activeDragId) continue
    targetsX.push(other.x, other.x + other.w / 2, other.x + other.w)
    targetsY.push(other.y, other.y + other.h / 2, other.y + other.h)
  }

  const snappedX = snapValue(state.x, targetsX)
  const snappedY = snapValue(state.y, targetsY)
  const showX = Math.abs(snappedX - state.x) <= SNAP_DIST
  const showY = Math.abs(snappedY - state.y) <= SNAP_DIST
  state.x = showX ? snappedX : state.x
  state.y = showY ? snappedY : state.y

  const clamped = clampWidgetPosition(state.x, state.y, w)
  state.x = clamped.x
  state.y = clamped.y

  snapGuides.value = {
    x: showX ? state.x : null,
    y: showY ? state.y : null,
  }

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

  snapGuides.value = { x: null, y: null }

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

function syncPanzoomExclude(enabled: boolean) {
  const pz = camera.getPanzoom()
  pz?.setOptions?.({ excludeClass: enabled ? 'panzoom-exclude' : null })
}

function detachAllInteract() {
  window.removeEventListener('pointermove', onGlobalPointerMove)
  widgetEls.forEach((el) => el.classList.remove('is-dragging'))
  dragStates.clear()
  activeDragId = null
  setCanvasPanEnabled(true)
}

watch(
  editMode,
  async (enabled) => {
    syncPanzoomExclude(enabled)
  if (!enabled) {
    dragArmedId.value = null
    detachAllInteract()
    return
  }
  },
  { immediate: true },
)

watch(
  userId,
  async () => {
    loadEditMode()
    detachAllInteract()
    widgets.value = loadLayoutForUser()
    await nextTick()
    widgets.value.forEach((w) => clampWidget(w))
    centerView()
    if (userId.value !== 'guest') {
      await loadLayoutFromServer()
    }
  },
  { immediate: false },
)

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
    syncPanzoomExclude(editMode.value)
  })

  await nextTick()

  widgets.value.forEach((w) => clampWidget(w))
  if (editMode.value) disarmWidget()

  if (userId.value !== 'guest') {
    await loadLayoutFromServer()
  }
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
  z-index: 1;
  background:
    radial-gradient(circle at 1px 1px, rgba(170, 200, 255, 0.1) 1px, transparent 1.6px) 0 0 / 28px
      28px,
    radial-gradient(circle at 1px 1px, rgba(255, 255, 255, 0.04) 1px, transparent 1.8px) 0 0 / 8px
      8px,
    linear-gradient(180deg, #050812 0%, #040611 100%);
}

.edit-grid {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.35;
  z-index: 2;
  background:
    linear-gradient(transparent 23px, rgba(148, 163, 184, 0.08) 24px) 0 0 / 24px 24px,
    linear-gradient(90deg, transparent 23px, rgba(148, 163, 184, 0.08) 24px) 0 0 / 24px 24px;
  mix-blend-mode: screen;
}

.date-panel {
  position: fixed;
  top: 12px;
  left: 12px;
  z-index: 60;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(2, 6, 23, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
  touch-action: manipulation;
}

.date-title {
  font-size: 0.6rem;
  text-transform: uppercase;
  letter-spacing: 0.25em;
  color: rgba(148, 163, 184, 0.85);
}

.date-row {
  display: grid;
  gap: 8px;
}

@media (min-width: 640px) {
  .date-row {
    grid-template-columns: 1fr 1fr;
  }
}

.date-field {
  display: grid;
  grid-template-columns: 22px 1fr;
  align-items: center;
  gap: 6px;
  color: rgba(226, 232, 240, 0.85);
  font-size: 0.7rem;
}

.date-input {
  height: 26px;
  padding: 0 6px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.7rem;
  color-scheme: dark;
  transition:
    border-color 160ms ease,
    background 160ms ease;
  touch-action: manipulation;
}
.date-input:hover {
  border-color: rgba(148, 163, 184, 0.35);
  background: rgba(255, 255, 255, 0.08);
}

.date-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.date-chip {
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.65rem;
  transition:
    border-color 160ms ease,
    background 160ms ease,
    transform 140ms ease;
  touch-action: manipulation;
}
.date-chip:hover {
  border-color: rgba(148, 163, 184, 0.35);
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-1px);
}

@media (hover: none) and (pointer: coarse) {
  .date-panel {
    padding: 10px 12px;
    gap: 8px;
    border-radius: 14px;
  }
  .date-title {
    font-size: 0.68rem;
  }
  .date-field {
    font-size: 0.78rem;
  }
  .date-input {
    height: 34px;
    font-size: 0.78rem;
  }
  .date-chip {
    height: 30px;
    padding: 0 12px;
    font-size: 0.78rem;
  }
}

.snap-guide {
  position: absolute;
  z-index: 3;
  pointer-events: none;
  background: rgba(139, 92, 246, 0.35);
  box-shadow: 0 0 8px rgba(139, 92, 246, 0.2);
}
.snap-guide--x {
  top: 0;
  bottom: 0;
  width: 1px;
}
.snap-guide--y {
  left: 0;
  right: 0;
  height: 1px;
}

.save-toast {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 80;
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(15, 23, 42, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.25);
  color: rgba(226, 232, 240, 0.95);
  font-size: 0.85rem;
  letter-spacing: 0.01em;
  pointer-events: none;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.35);
}

.save-toast-enter-active,
.save-toast-leave-active {
  transition: opacity 160ms ease, transform 160ms ease;
}
.save-toast-enter-from,
.save-toast-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

/* ✅ en mode édition, on peut drag “partout” (même sur les charts) */
</style>
