<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import * as echarts from 'echarts/core'
import { LineChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { getFinancialOverview, getRevenueTrend, getIncomeStatement, getBalanceSheet, getCashFlowStatement } from '../services/api'
import StockSearch from '../components/StockSearch.vue'
import FinancialTable from '../components/FinancialTable.vue'

echarts.use([LineChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const stockCode = ref('')
const overview = ref<any>(null)
const revenueTrend = ref<any>(null)
const income = ref<any>(null)
const balance = ref<any>(null)
const cashflow = ref<any>(null)
const trendChartRef = ref<HTMLDivElement>()
let trendChart: echarts.ECharts | null = null

const onStockSelect = (item: { stockCode: string; stockName: string }) => {
  stockCode.value = item.stockCode
}

const loadData = async (code: string) => {
  try {
    const [overviewData, trendData, incomeData, balanceData, cashflowData] = await Promise.all([
      getFinancialOverview(code),
      getRevenueTrend(code),
      getIncomeStatement(code),
      getBalanceSheet(code),
      getCashFlowStatement(code),
    ])
    overview.value = overviewData
    revenueTrend.value = trendData
    income.value = incomeData
    balance.value = balanceData
    cashflow.value = cashflowData
    renderTrendChart()
  } catch (err) {
    console.error(err)
  }
}

const renderTrendChart = () => {
  if (!trendChartRef.value || !revenueTrend.value) return
  if (!trendChart) trendChart = echarts.init(trendChartRef.value)
  const d = revenueTrend.value
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['营收', '增长率'], top: 0 },
    grid: { left: '5%', right: '5%', top: '15%', height: '70%' },
    xAxis: { data: d.dates, axisLabel: { rotate: 30 } },
    yAxis: [
      { type: 'value', name: '营收(亿)', axisLabel: { formatter: (v: number) => (v / 100000000).toFixed(0) + '亿' } },
      { type: 'value', name: '增长率(%)', axisLabel: { formatter: '{value}%' } },
    ],
    series: [
      { name: '营收', type: 'bar', data: d.revenues, itemStyle: { color: '#3b82f6' } },
      { name: '增长率', type: 'line', yAxisIndex: 1, data: d.growthRates, itemStyle: { color: '#f59e0b' }, symbol: 'circle', symbolSize: 8 },
    ],
  })
}

watch(stockCode, (code) => { if (code) loadData(code) })

const formatMoney = (v: number) => `¥${(v ?? 0).toFixed(2)}`
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-amber-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z M20.488 9H15V3.512A9.025 9.025 0 0120.488 9z"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">财务分析</h1>
    </div>

    <!-- Search -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">选择股票</h2>
      <StockSearch :modelValue="stockCode" @select="onStockSelect" />
    </div>

    <template v-if="stockCode">
      <!-- Overview Metrics -->
      <div v-if="overview" class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">营收</p>
          <p class="text-xl font-bold text-gray-900">{{ ((overview.revenue || 0) / 100000000).toFixed(2) }}亿</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">净利润</p>
          <p class="text-xl font-bold text-gray-900">{{ ((overview.netProfit || 0) / 100000000).toFixed(2) }}亿</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">ROE</p>
          <p class="text-xl font-bold text-green-600">{{ ((overview.roe || 0) * 100).toFixed(1) }}%</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">EPS</p>
          <p class="text-xl font-bold text-gray-900">¥{{ (overview.eps || 0).toFixed(2) }}</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">市盈率(PE)</p>
          <p :class="['text-xl font-bold', (overview.peRatio || 0) > 50 ? 'text-red-500' : 'text-green-600']">{{ (overview.peRatio || 0).toFixed(1) }}</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">市净率(PB)</p>
          <p :class="['text-xl font-bold', (overview.pbRatio || 0) > 10 ? 'text-red-500' : 'text-green-600']">{{ (overview.pbRatio || 0).toFixed(1) }}</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">股息率</p>
          <p class="text-xl font-bold text-blue-600">{{ ((overview.dividendYield || 0) * 100).toFixed(2) }}%</p>
        </div>
        <div class="bg-white rounded-xl border border-gray-100 p-4 text-center">
          <p class="text-xs text-gray-400 mb-1">股票名称</p>
          <p class="text-lg font-bold text-gray-900">{{ overview.stockName }}</p>
        </div>
      </div>

      <!-- Revenue Trend -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">营收趋势</h2>
        <div ref="trendChartRef" style="width:100%;height:400px;"></div>
      </div>

      <!-- Financial Statements -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">
        <FinancialTable v-if="income" title="利润表" :data="{
          '营收': '¥' + (income.revenue || 0).toLocaleString(),
          '净利润': '¥' + (income.netProfit || 0).toLocaleString(),
          '每股收益(EPS)': '¥' + (income.eps || 0).toFixed(2),
          '报告日期': income.reportDate || '-',
        }" />
        <FinancialTable v-if="balance" title="资产负债表" :data="{
          '总资产': '¥' + (balance.totalAssets || 0).toLocaleString(),
          '总负债': '¥' + (balance.totalLiabilities || 0).toLocaleString(),
          '股东权益': '¥' + (balance.shareholdersEquity || 0).toLocaleString(),
          '报告日期': balance.reportDate || '-',
        }" />
        <FinancialTable v-if="cashflow" title="现金流量表" :data="{
          '经营活动现金流': '¥' + (cashflow.operatingCashFlow || 0).toLocaleString(),
          '投资活动现金流': '¥' + (cashflow.investingCashFlow || 0).toLocaleString(),
          '融资活动现金流': '¥' + (cashflow.financingCashFlow || 0).toLocaleString(),
          '净现金流': '¥' + (cashflow.netCashFlow || 0).toLocaleString(),
          '报告日期': cashflow.reportDate || '-',
        }" />
      </div>
    </template>

    <!-- Empty -->
    <div v-if="!stockCode" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center text-gray-400">
      <p>请输入股票代码查看财务分析</p>
    </div>
  </div>
</template>
