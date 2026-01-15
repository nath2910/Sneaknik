// src/components/stats/widgetRegistry.js
import {
  Gauge,
  LineChart,
  PieChart,
  BarChart3,
  Trophy,
  Activity,
  TrendingUp,
  Waves,
  GitCompareArrows,
  Boxes,
} from 'lucide-vue-next'

import StatsKpisWidget from './widgets/StatsKpisWidget.vue'
import StatsLineWidget from './widgets/StatsLineWidget.vue'
import StatsPieWidget from './widgets/StatsPieWidget.vue'
import StatsBarWidget from './widgets/StatsBarWidget.vue'
import StatsTopWidget from './widgets/StatsTopWidget.vue'

// nouveaux
import StatsSparkWidget from './widgets/StatsSparkWidget.vue'
import StatsCumulativeWidget from './widgets/StatsCumulativeWidget.vue'
import StatsMovingAvgWidget from './widgets/StatsMovingAvgWidget.vue'
import StatsCompareWidget from './widgets/StatsCompareWidget.vue'
import StatsStockWidget from './widgets/StatsStockWidget.vue'

const GRANULARITY_OPTIONS = [
  { label: 'Jour', value: 'day' },
  { label: 'Semaine', value: 'week' },
  { label: 'Mois', value: 'month' },
]

export const WIDGET_DEFS = [
  {
    type: 'kpis',
    title: 'KPIs',
    help: 'Résumé rapide',
    icon: Gauge,
    component: StatsKpisWidget,
    defaultSize: { w: 640, h: 250 },
    defaultProps: { showMargin: true, showSold: true, showStock: true, showStockValue: true },
    settings: [
      { key: 'showMargin', label: 'Afficher marge', type: 'toggle', hint: 'ProfitMargin' },
      { key: 'showSold', label: 'Afficher vendues', type: 'toggle', hint: 'itemsVendues' },
      { key: 'showStock', label: 'Afficher stock', type: 'toggle', hint: 'itemsEnStock' },
      {
        key: 'showStockValue',
        label: 'Afficher valeur stock',
        type: 'toggle',
        hint: 'valeurStock',
      },
    ],
  },

  {
    type: 'line',
    title: 'Courbes (CA & Profit)',
    help: 'Évolution dans le temps',
    icon: LineChart,
    component: StatsLineWidget,
    defaultSize: { w: 820, h: 460 },
    defaultProps: { granularity: 'day', showCA: true, showProfit: true },
    settings: [
      { key: 'granularity', label: 'Granularité', type: 'select', options: GRANULARITY_OPTIONS },
      { key: 'showCA', label: 'Afficher CA', type: 'toggle' },
      { key: 'showProfit', label: 'Afficher Profit', type: 'toggle' },
    ],
  },

  {
    type: 'pie',
    title: 'Donut (Marques)',
    help: 'Répartition marques (nb)',
    icon: PieChart,
    component: StatsPieWidget,
    defaultSize: { w: 460, h: 460 },
    defaultProps: { top: 8 },
    settings: [{ key: 'top', label: 'Top N', type: 'number', min: 3, max: 20 }],
  },

  {
    type: 'bar',
    title: 'Barres (Top marques)',
    help: 'Top marques (volume)',
    icon: BarChart3,
    component: StatsBarWidget,
    defaultSize: { w: 720, h: 420 },
    defaultProps: { top: 8 },
    settings: [{ key: 'top', label: 'Top N', type: 'number', min: 3, max: 20 }],
  },

  {
    type: 'top',
    title: 'Top ventes',
    help: 'Top par bénéfice',
    icon: Trophy,
    component: StatsTopWidget,
    defaultSize: { w: 620, h: 420 },
    defaultProps: { limit: 5 },
    settings: [{ key: 'limit', label: 'Limite', type: 'number', min: 3, max: 30 }],
  },

  // 5 nouveaux
  {
    type: 'spark',
    title: 'Sparkline',
    help: 'Mini tendance',
    icon: Activity,
    component: StatsSparkWidget,
    defaultSize: { w: 420, h: 220 },
    defaultProps: { metric: 'ca', granularity: 'day', label: 'Tendance' },
    settings: [
      { key: 'label', label: 'Titre', type: 'text' },
      {
        key: 'metric',
        label: 'Métrique',
        type: 'select',
        options: [
          { label: 'CA', value: 'ca' },
          { label: 'Profit', value: 'profit' },
        ],
      },
      { key: 'granularity', label: 'Granularité', type: 'select', options: GRANULARITY_OPTIONS },
    ],
  },

  {
    type: 'cumul',
    title: 'Cumul',
    help: 'Cumul CA/Profit',
    icon: TrendingUp,
    component: StatsCumulativeWidget,
    defaultSize: { w: 720, h: 380 },
    defaultProps: { metric: 'ca', granularity: 'day' },
    settings: [
      {
        key: 'metric',
        label: 'Métrique',
        type: 'select',
        options: [
          { label: 'CA', value: 'ca' },
          { label: 'Profit', value: 'profit' },
        ],
      },
      { key: 'granularity', label: 'Granularité', type: 'select', options: GRANULARITY_OPTIONS },
    ],
  },

  {
    type: 'ma',
    title: 'Moyenne mobile',
    help: 'Lissage de courbe',
    icon: Waves,
    component: StatsMovingAvgWidget,
    defaultSize: { w: 760, h: 380 },
    defaultProps: { metric: 'ca', granularity: 'day', window: 7 },
    settings: [
      {
        key: 'metric',
        label: 'Métrique',
        type: 'select',
        options: [
          { label: 'CA', value: 'ca' },
          { label: 'Profit', value: 'profit' },
        ],
      },
      { key: 'granularity', label: 'Granularité', type: 'select', options: GRANULARITY_OPTIONS },
      { key: 'window', label: 'Fenêtre', type: 'number', min: 2, max: 30 },
    ],
  },

  {
    type: 'compare',
    title: 'Comparatif',
    help: 'Vs période précédente',
    icon: GitCompareArrows,
    component: StatsCompareWidget,
    defaultSize: { w: 460, h: 240 },
    defaultProps: { metric: 'ca' },
    settings: [
      {
        key: 'metric',
        label: 'Métrique',
        type: 'select',
        options: [
          { label: 'CA', value: 'ca' },
          { label: 'Profit', value: 'profit' },
          { label: 'Vendues', value: 'itemsVendues' },
        ],
      },
    ],
  },

  {
    type: 'stock',
    title: 'Stock',
    help: 'Vendues vs stock + valeur',
    icon: Boxes,
    component: StatsStockWidget,
    defaultSize: { w: 520, h: 360 },
    defaultProps: {},
    settings: [],
  },
]

export function getWidgetDef(type) {
  return WIDGET_DEFS.find((d) => d.type === type)
}

export function newWidget(type, x, y) {
  const uid =
    globalThis.crypto?.randomUUID?.() ?? `${Date.now()}_${Math.random().toString(16).slice(2)}`
  const def = getWidgetDef(type)
  if (!def) throw new Error(`Unknown widget type: ${type}`)
  return {
    id: `${type}_${uid}`,
    type,
    title: def.title,
    x,
    y,
    w: def.defaultSize.w,
    h: def.defaultSize.h,
    props: { ...def.defaultProps },
  }
}
