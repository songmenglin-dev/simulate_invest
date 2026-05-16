<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts/core'
import { CandlestickChart, LineChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent, DataZoomComponent, MarkLineComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getKLine, getIndicators } from '../services/api'

echarts.use([CandlestickChart, LineChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, DataZoomComponent, MarkLineComponent, CanvasRenderer])

const props = defineProps<{ stockCode: string }>()

const chartRef = ref<HTMLDivElement>()
const showMA = ref(true)
const showMACD = ref(false)
const showKDJ = ref(false)
const period = ref('daily')
let chart: echarts.ECharts | null = null

const buildOption = (kline: any, indicators: any): echarts.EChartsOption => {
  const dates = kline.dates || []
  const ohlc = (kline.open || []).map((_: number, i: number) => [
    kline.open[i],
    kline.close[i],
    kline.low[i],
    kline.high[i],
  ])

  const series: any[] = [
    { name: 'K线', type: 'candlestick', data: ohlc, itemStyle: { color: '#ef4444', color0: '#22c55e', borderColor: '#ef4444', borderColor0: '#22c55e' } },
  ]

  if (showMA.value && indicators) {
    const maColors = ['#f59e0b', '#3b82f6', '#8b5cf6', '#ec4899']
    ;[5, 10, 20, 60].forEach((n, i) => {
      const key = `ma${n}` as keyof typeof indicators
      if (indicators[key] !== undefined) {
        series.push({ name: `MA${n}`, type: 'line', data: new Array(dates.length).fill(indicators[key]), lineStyle: { color: maColors[i], width: 1 }, symbol: 'none' })
      }
    })
  }

  const grids: any[] = [{ left: '5%', right: '5%', top: '10%', height: showMACD.value || showKDJ.value ? '55%' : '75%' }]
  const yAxes: any[] = [{ scale: true, splitArea: { show: true } }]
  const xAxes: any[] = [{ data: dates, scale: true, boundaryGap: true, axisLine: { onZero: false }, splitLine: { show: false }, min: 'dataMin', max: 'dataMax' }]

  if (showMACD.value && indicators) {
    const dif = indicators.macd
    const dea = indicators.signal
    const macdBar = indicators.histogram
    const macdData = new Array(dates.length).fill(null)
    macdData[dates.length - 1] = [dif, dea, macdBar]
    series.push({ name: 'MACD', type: 'bar', yAxisIndex: 1, xAxisIndex: 0, data: macdData.map((d: any) => d ? d[2] : 0), itemStyle: { color: (p: any) => p.value >= 0 ? '#ef4444' : '#22c55e' } })
    series.push({ name: 'DIF', type: 'line', yAxisIndex: 1, xAxisIndex: 0, data: new Array(dates.length).fill(dif), lineStyle: { color: '#3b82f6' }, symbol: 'none' })
    series.push({ name: 'DEA', type: 'line', yAxisIndex: 1, xAxisIndex: 0, data: new Array(dates.length).fill(dea), lineStyle: { color: '#f59e0b' }, symbol: 'none' })
    grids.push({ left: '5%', right: '5%', top: '70%', height: '20%' })
    yAxes.push({ scale: true })
  }

  if (showKDJ.value && indicators) {
    const kdjColors = ['#3b82f6', '#f59e0b', '#8b5cf6']
    ;['k', 'd', 'j'].forEach((key, i) => {
      const value = indicators[key as keyof typeof indicators] as number
      series.push({ name: key.toUpperCase(), type: 'line', yAxisIndex: showMACD.value ? 2 : 1, xAxisIndex: 0, data: new Array(dates.length).fill(value), lineStyle: { color: kdjColors[i], width: 1 }, symbol: 'none' })
    })
    if (!showMACD.value) {
      grids.push({ left: '5%', right: '5%', top: '70%', height: '20%' })
      yAxes.push({ scale: true })
    }
  }

  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    legend: { data: series.map((s: any) => s.name), top: 0 },
    grid: grids,
    xAxis: grids.map(() => ({ ...xAxes[0], gridIndex: undefined })),
    yAxis: yAxes.map((y: any, i: number) => ({ ...y, gridIndex: i })),
    series,
  }
}

const loadData = async () => {
  if (!props.stockCode || !chart) return
  try {
    const [kline, indicators] = await Promise.all([
      getKLine(props.stockCode, period.value),
      getIndicators(props.stockCode, period.value),
    ])
    chart.setOption(buildOption(kline, indicators), true)
  } catch (err) {
    console.error('Failed to load chart data:', err)
  }
}

onMounted(() => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
    loadData()
  }
})

onUnmounted(() => {
  chart?.dispose()
})

watch(() => props.stockCode, () => loadData())
watch(period, () => loadData())
watch([showMA, showMACD, showKDJ], () => loadData())
</script>

<template>
  <div>
    <div class="flex items-center gap-4 mb-4 flex-wrap">
      <div class="flex items-center gap-2 bg-gray-100 rounded-lg p-1">
        <button
          v-for="p in [{v:'daily',l:'日线'},{v:'weekly',l:'周线'},{v:'monthly',l:'月线'}]"
          :key="p.v"
          :class="['px-3 py-1 rounded-md text-sm font-medium transition', period === p.v ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700']"
          @click="period = p.v"
        >{{ p.l }}</button>
      </div>
      <label class="flex items-center gap-1 text-sm text-gray-600 cursor-pointer">
        <input type="checkbox" v-model="showMA" class="rounded" /> MA
      </label>
      <label class="flex items-center gap-1 text-sm text-gray-600 cursor-pointer">
        <input type="checkbox" v-model="showMACD" class="rounded" /> MACD
      </label>
      <label class="flex items-center gap-1 text-sm text-gray-600 cursor-pointer">
        <input type="checkbox" v-model="showKDJ" class="rounded" /> KDJ
      </label>
    </div>
    <div ref="chartRef" style="width:100%;height:500px;"></div>
  </div>
</template>
