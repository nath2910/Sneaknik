// src/lib/echarts.js
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'

import { LineChart, BarChart, PieChart, TreemapChart, HeatmapChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
  TitleComponent,
  ToolboxComponent,
  DataZoomComponent,
  VisualMapComponent,
} from 'echarts/components'

use([
  CanvasRenderer,

  LineChart,
  BarChart,
  PieChart,
  TreemapChart,
  HeatmapChart,

  GridComponent,
  LegendComponent,
  TooltipComponent,
  TitleComponent,
  ToolboxComponent,
  DataZoomComponent,
  VisualMapComponent,
])
