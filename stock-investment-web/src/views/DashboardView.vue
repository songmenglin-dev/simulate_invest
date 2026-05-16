<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { getPortfolioOverview, getPositions, searchStocks, getQuote, getSimulatedQuotes } from '../services/api'

const userId = ref<number>(Number(localStorage.getItem('userId')) || 1)
const overview = ref<any>(null)
const positions = ref<any[]>([])
const stocks = ref<any[]>([])
const selectedStock = ref('')
const quote = ref<any>(null)
const loading = ref(true)

const dashboardStocks = ref([
  { code: '600519', name: '贵州茅台' },
  { code: '000858', name: '五粮液' },
  { code: '600036', name: '招商银行' },
  { code: '601318', name: '中国平安' },
  { code: '002594', name: '比亚迪' },
  { code: '000333', name: '美的集团' },
])

const simulatedQuotes = ref<any[]>([])
const dashboardCodes = computed(() => dashboardStocks.value.map(s => s.code))

const availableStocksForDashboard = computed(() =>
  stocks.value.filter(s => !dashboardStocks.value.some(ds => ds.code === s.stockCode))
)

const selectedDashboardStock = ref('')

const addDashboardStock = () => {
  if (!selectedDashboardStock.value) return
  const stock = stocks.value.find(s => s.stockCode === selectedDashboardStock.value)
  if (stock && !dashboardStocks.value.some(s => s.code === stock.stockCode)) {
    dashboardStocks.value.push({ code: stock.stockCode, name: stock.stockName })
    selectedDashboardStock.value = ''
  }
}

const removeDashboardStock = (code: string) => {
  dashboardStocks.value = dashboardStocks.value.filter(s => s.code !== code)
}

const loadSimulatedQuotes = async () => {
  if (dashboardCodes.value.length === 0) return
  try {
    simulatedQuotes.value = await getSimulatedQuotes(dashboardCodes.value)
  } catch (err) {
    // silently fail
  }
}

let dashboardTimer: ReturnType<typeof setInterval> | null = null

const loadData = async () => {
  try {
    const [overviewData, positionsData, stocksData] = await Promise.all([
      getPortfolioOverview(userId.value),
      getPositions(userId.value),
      searchStocks(),
    ])
    overview.value = overviewData
    positions.value = positionsData
    stocks.value = stocksData
    if (stocksData.length > 0) {
      selectedStock.value = stocksData[0].stockCode
    }
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

const loadQuote = async () => {
  if (selectedStock.value) {
    try {
      quote.value = await getQuote(selectedStock.value)
    } catch (err) {
      console.error(err)
    }
  }
}

onMounted(() => {
  loadData()
  loadSimulatedQuotes()
  dashboardTimer = setInterval(loadSimulatedQuotes, 3000)
})

onUnmounted(() => {
  if (dashboardTimer) clearInterval(dashboardTimer)
})

watch(selectedStock, () => {
  loadQuote()
})

const formatMoney = (v: number) => `¥${v.toFixed(2)}`
const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`

</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-4 0a1 1 0 01-1-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 01-1 1"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">概览</h1>
    </div>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <template v-else>
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- 账户概览 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center gap-2 mb-6">
            <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-900">账户概览</h2>
          </div>

          <div v-if="overview" class="space-y-4">
            <div class="p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl border border-blue-100">
              <p class="text-sm text-gray-500 mb-1">总资产</p>
              <p class="text-3xl font-bold text-blue-600">{{ formatMoney(overview.totalAssets) }}</p>
            </div>
            <div class="grid grid-cols-2 gap-3">
              <div class="p-3 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-500 mb-1">可用资金</p>
                <p class="text-lg font-semibold text-gray-900">{{ formatMoney(overview.availableCash) }}</p>
              </div>
              <div class="p-3 bg-gray-50 rounded-xl">
                <p class="text-xs text-gray-500 mb-1">冻结资金</p>
                <p class="text-lg font-semibold text-yellow-600">{{ formatMoney(overview.frozenCash) }}</p>
              </div>
            </div>
            <div class="p-3 bg-gray-50 rounded-xl">
              <p class="text-xs text-gray-500 mb-1">持仓市值</p>
              <p class="text-lg font-semibold text-gray-900">{{ formatMoney(overview.totalMarketValue) }}</p>
            </div>
            <div class="p-4 bg-gray-50 rounded-xl border border-gray-200">
              <div class="flex justify-between items-center">
                <span class="text-sm text-gray-500">总盈亏</span>
                <span :class="['text-lg font-bold', overview.totalProfitLoss >= 0 ? 'text-red-600' : 'text-green-600']">
                  {{ formatMoney(overview.totalProfitLoss) }}
                </span>
              </div>
              <p :class="['text-sm mt-1', overview.totalProfitLoss >= 0 ? 'text-red-500' : 'text-green-500']">
                {{ formatPercent(overview.profitLossPercent) }}
              </p>
            </div>
          </div>
        </div>

        <!-- 行情查询 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center gap-2 mb-6">
            <div class="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-900">行情查询</h2>
          </div>

          <select
            v-model="selectedStock"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
          >
            <option v-for="s in stocks" :key="s.stockCode" :value="s.stockCode">
              {{ s.stockCode }} - {{ s.stockName }}
            </option>
          </select>

          <div v-if="quote" class="space-y-4">
            <div class="p-4 bg-gray-50 rounded-xl">
              <div class="flex justify-between items-start mb-2">
                <span class="text-xl font-bold text-gray-900">{{ quote.stockName }}</span>
                <span class="text-2xl font-bold text-gray-900">{{ quote.currentPrice.toFixed(2) }}</span>
              </div>
              <div :class="['text-lg font-medium', quote.change >= 0 ? 'text-red-600' : 'text-green-600']">
                {{ quote.change >= 0 ? '+' : '' }}{{ quote.change.toFixed(2) }} ({{ formatPercent(quote.changePercent) }})
              </div>
            </div>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div class="flex justify-between p-3 bg-gray-50 rounded-xl">
                <span class="text-gray-500">开盘</span>
                <span class="font-medium text-gray-900">{{ quote.open.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between p-3 bg-gray-50 rounded-xl">
                <span class="text-gray-500">最高</span>
                <span class="font-medium text-gray-900">{{ quote.high.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between p-3 bg-gray-50 rounded-xl">
                <span class="text-gray-500">最低</span>
                <span class="font-medium text-gray-900">{{ quote.low.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between p-3 bg-gray-50 rounded-xl">
                <span class="text-gray-500">成交量</span>
                <span class="font-medium text-gray-900">{{ (quote.volume / 10000).toFixed(2) }}万</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center gap-2 mb-6">
            <div class="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-900">快捷操作</h2>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <router-link to="/trading" class="bg-blue-600 hover:bg-blue-700 text-white py-4 rounded-xl transition shadow-lg shadow-blue-200 font-medium text-center">
              买入
            </router-link>
            <router-link to="/trading" class="bg-orange-500 hover:bg-orange-600 text-white py-4 rounded-xl transition shadow-lg shadow-orange-200 font-medium text-center">
              卖出
            </router-link>
            <router-link to="/portfolio" class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium text-center">
              持仓
            </router-link>
            <router-link to="/trading" class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium text-center">
              订单
            </router-link>
          </div>

          <div class="mt-6 p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl">
            <p class="text-sm text-gray-500 mb-2">市场动态</p>
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
              <span class="text-sm text-gray-600">市场交易中</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 实时市场看板 -->
      <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-2">
            <div class="w-10 h-10 bg-red-100 rounded-xl flex items-center justify-center">
              <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
            </div>
            <h2 class="text-lg font-semibold text-gray-900">市场看板</h2>
            <span class="px-2 py-0.5 bg-amber-100 text-amber-700 text-xs rounded-full font-medium">模拟数据</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            <span class="text-xs text-gray-400">每3秒自动刷新</span>
          </div>
        </div>

        <div class="flex items-center gap-3 mb-4">
          <select
            v-model="selectedDashboardStock"
            @change="addDashboardStock"
            class="px-4 py-2 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
          >
            <option value="">+ 添加股票</option>
            <option v-for="s in availableStocksForDashboard" :key="s.stockCode" :value="s.stockCode">
              {{ s.stockCode }} - {{ s.stockName }}
            </option>
          </select>
          <span v-if="availableStocksForDashboard.length === 0" class="text-xs text-gray-400">所有股票已添加</span>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">
          <div
            v-for="q in simulatedQuotes"
            :key="q.stockCode"
            class="relative p-4 rounded-xl border transition-all duration-300 hover:shadow-md cursor-default"
            :class="q.change >= 0 ? 'bg-red-50 border-red-100 hover:bg-red-100' : 'bg-green-50 border-green-100 hover:bg-green-100'"
          >
            <button
              @click="removeDashboardStock(q.stockCode)"
              class="absolute top-1 right-1 w-5 h-5 flex items-center justify-center rounded-full text-gray-400 hover:text-gray-600 hover:bg-gray-200 transition text-xs leading-none"
              title="移除"
            >x</button>
            <div class="text-sm font-medium text-gray-900 truncate mb-1 pr-4">{{ q.stockName }}</div>
            <div class="text-xs text-gray-400 mb-2">{{ q.stockCode }}</div>
            <div class="text-lg font-bold mb-1" :class="q.change >= 0 ? 'text-red-600' : 'text-green-600'">
              {{ q.currentPrice?.toFixed(2) }}
            </div>
            <div class="text-xs font-medium" :class="q.change >= 0 ? 'text-red-500' : 'text-green-500'">
              {{ q.change >= 0 ? '+' : '' }}{{ q.change?.toFixed(2) }} ({{ q.changePercent >= 0 ? '+' : '' }}{{ q.changePercent?.toFixed(2) }}%)
            </div>
          </div>
        </div>

        <div v-if="dashboardStocks.length > 0 && simulatedQuotes.length === 0" class="text-center py-8 text-gray-400 text-sm">
          正在加载实时数据...
        </div>
      </div>

      <!-- 持仓列表 -->
      <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center gap-2 mb-6">
          <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">持仓明细</h2>
        </div>

        <div v-if="positions.length === 0" class="text-gray-400 text-center py-12 bg-gray-50 rounded-xl">
          <svg class="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
          </svg>
          <p>暂无持仓数据</p>
        </div>

        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="text-gray-500 text-sm border-b border-gray-100">
                <th class="text-left py-3 px-4 font-medium">股票</th>
                <th class="text-right py-3 px-4 font-medium">持仓数量</th>
                <th class="text-right py-3 px-4 font-medium">成本价</th>
                <th class="text-right py-3 px-4 font-medium">现价</th>
                <th class="text-right py-3 px-4 font-medium">市值</th>
                <th class="text-right py-3 px-4 font-medium">盈亏</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="p in positions" :key="p.positionId" class="border-b border-gray-50 hover:bg-gray-50 transition">
                <td class="py-4 px-4">
                  <div class="font-medium text-gray-900">{{ p.stockName }}</div>
                  <div class="text-gray-400 text-xs">{{ p.stockCode }}</div>
                </td>
                <td class="text-right text-gray-600">{{ p.totalQuantity }}</td>
                <td class="text-right text-gray-600">¥{{ p.avgCost.toFixed(2) }}</td>
                <td class="text-right text-gray-600">¥{{ p.currentPrice.toFixed(2) }}</td>
                <td class="text-right font-medium text-gray-900">{{ formatMoney(p.marketValue) }}</td>
                <td :class="['text-right font-medium', p.profitLoss >= 0 ? 'text-red-600' : 'text-green-600']">
                  {{ formatMoney(p.profitLoss) }}
                  <div class="text-xs">{{ formatPercent(p.profitLossPercent) }}</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>
  </div>
</template>
