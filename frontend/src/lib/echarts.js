// src/lib/echarts.js
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'

import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
  TitleComponent,
  ToolboxComponent,
  DataZoomComponent,
} from 'echarts/components'

use([
  CanvasRenderer,

  LineChart,
  BarChart,
  PieChart,

  GridComponent,
  LegendComponent,
  TooltipComponent,
  TitleComponent,
  ToolboxComponent,
  DataZoomComponent,
])
