// src/components/stats/widgets/widgetRegistry.js
import {
  TrendingUp,
  GitCompareArrows,
  LineChart,
  Gauge,
  Boxes,
  BarChart3,
  Activity,
  Trophy,
  StickyNote,
} from 'lucide-vue-next'

// src/components/stats/widgets/widgetRegistry.js
import NetProfitWidget from './widgets/NetProfitWidget.vue'

import RoiWidget from './widgets/RoiWidget.vue'
import GrossRevenueWidget from './widgets/GrossRevenueWidget.vue'
import AvgMarginWidget from './widgets/AvgMarginWidget.vue'

import InventoryValueWidget from './widgets/InventoryValueWidget.vue'
import SellThroughWidget from './widgets/SellThroughWidget.vue'
import AvgDaysToSellWidget from './widgets/AvgDaysToSellWidget.vue'
import DeathPileWidget from './widgets/DeathPileWidget.vue'
import ActiveListingsWidget from './widgets/ActiveListingsWidget.vue'

import TopProfitDriversWidget from './widgets/TopProfitDriversWidget.vue'
import AspWidget from './widgets/AspWidget.vue'
import CashFlowWidget from './widgets/CashFlowWidget.vue'

import BrandsWidget from './widgets/BrandsWidget.vue'
import TopSalesWidget from './widgets/TopSalesWidget.vue'
import TextSectionWidget from './widgets/TextSectionWidget.vue'

const BUCKET_OPTIONS = [
  { label: 'Jour', value: 'day' },
  { label: 'Semaine', value: 'week' },
  { label: 'Mois', value: 'month' },
]

export const WIDGET_DEFS = [
  // ?? Structure / notes
  {
    type: 'textSection',
    title: 'Texte / Section',
    help: 'Bloc de texte pour structurer la page',
    icon: StickyNote,
    component: TextSectionWidget,
    defaultSize: { w: 520, h: 200 },
    defaultProps: { content: 'Section title', variant: 'title', align: 'left' },
    settings: [
      { key: 'content', label: 'Texte', type: 'textarea' },
      {
        key: 'variant',
        label: 'Style',
        type: 'select',
        options: [
          { label: 'Titre', value: 'title' },
          { label: 'Note', value: 'note' },
          { label: 'Discret', value: 'muted' },
        ],
      },
      {
        key: 'align',
        label: 'Alignement',
        type: 'select',
        options: [
          { label: 'Gauche', value: 'left' },
          { label: 'Centre', value: 'center' },
          { label: 'Droite', value: 'right' },
        ],
      },
    ],
  },
  // 💰 Finance
  {
    type: 'netProfit',
    title: 'Bénéfice net',
    help: 'Profit total sur la période',
    icon: TrendingUp,
    component: NetProfitWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'roi',
    title: 'ROI moyen',
    help: 'Retour sur investissement',
    icon: GitCompareArrows,
    component: RoiWidget,
    defaultSize: { w: 520, h: 260 },
    defaultProps: {},
    settings: [],
  },
  {
    type: 'grossRevenue',
    title: 'Chiffre d’affaires',
    help: 'Courbe du CA',
    icon: LineChart,
    component: GrossRevenueWidget,
    defaultSize: { w: 820, h: 420 },
    defaultProps: { bucket: 'day' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'avgMargin',
    title: 'Marge moyenne',
    help: 'Marge moyenne par article',
    icon: Gauge,
    component: AvgMarginWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  // 📦 Stock & vélocité
  {
    type: 'inventoryValue',
    title: 'Valeur du stock',
    help: 'Capital immobilisé',
    icon: Boxes,
    component: InventoryValueWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'sellThrough',
    title: 'Sell-through',
    help: 'Taux d’écoulement',
    icon: BarChart3,
    component: SellThroughWidget,
    defaultSize: { w: 520, h: 260 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'avgDaysToSell',
    title: 'Délai moyen',
    help: 'Days to sell',
    icon: Activity,
    component: AvgDaysToSellWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'deathPile',
    title: 'Death pile',
    help: 'Stock dormant',
    icon: Boxes,
    component: DeathPileWidget,
    defaultSize: { w: 520, h: 380 },
    defaultProps: {},
    settings: [],
  },
  {
    type: 'activeListings',
    title: 'Annonces actives',
    help: 'Nb annonces actives',
    icon: Activity,
    component: ActiveListingsWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },

  // 🚀 Performance
  {
    type: 'topProfitDrivers',
    title: 'Top profit (marques/cat)',
    help: 'Ce qui te rapporte le plus',
    icon: Trophy,
    component: TopProfitDriversWidget,
    defaultSize: { w: 720, h: 420 },
    defaultProps: { top: 8 },
    settings: [{ key: 'top', label: 'Top N', type: 'number', min: 3, max: 20 }],
  },
  {
    type: 'asp',
    title: 'Prix moyen (ASP)',
    help: 'Average selling price',
    icon: Gauge,
    component: AspWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },
  {
    type: 'cashFlow',
    title: 'Cash disponible',
    help: 'Cash pour racheter du stock',
    icon: TrendingUp,
    component: CashFlowWidget,
    defaultSize: { w: 520, h: 240 },
    defaultProps: { bucket: 'week' },
    settings: [{ key: 'bucket', label: 'Granularité', type: 'select', options: BUCKET_OPTIONS }],
  },

  // Bonus (si tu veux garder)
  {
    type: 'brands',
    title: 'Top marques (volume)',
    help: 'Barres marques',
    icon: BarChart3,
    component: BrandsWidget,
    defaultSize: { w: 720, h: 420 },
    defaultProps: { top: 8 },
    settings: [{ key: 'top', label: 'Top N', type: 'number', min: 3, max: 20 }],
  },
  {
    type: 'topSales',
    title: 'Top ventes',
    help: 'Top par bénéfice',
    icon: Trophy,
    component: TopSalesWidget,
    defaultSize: { w: 620, h: 420 },
    defaultProps: { limit: 5 },
    settings: [{ key: 'limit', label: 'Limite', type: 'number', min: 3, max: 30 }],
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

