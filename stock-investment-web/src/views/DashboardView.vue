<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getPortfolioOverview, getPositions, searchStocks, getQuote } from '../services/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const userId = ref<number>(Number(localStorage.getItem('userId')) || 1)
const overview = ref<any>(null)
const positions = ref<any[]>([])
const stocks = ref<any[]>([])
const selectedStock = ref('')
const quote = ref<any>(null)
const loading = ref(true)

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
})

watch(selectedStock, () => {
  loadQuote()
})

const formatMoney = (v: number) => `¥${v.toFixed(2)}`
const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`

const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Top Navigation - Light Theme -->
    <header class="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-6">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-blue-600 rounded-xl flex items-center justify-center shadow-lg shadow-blue-100">
              <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
              </svg>
            </div>
            <span class="text-xl font-bold text-gray-900">股票投资平台</span>
          </div>

          <!-- Nav Items -->
          <nav class="hidden md:flex items-center gap-1">
            <a href="#" class="px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition text-sm font-medium">行情</a>
            <a href="#" class="px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition text-sm font-medium">交易</a>
            <a href="#" class="px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition text-sm font-medium">持仓</a>
            <a href="#" class="px-4 py-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition text-sm font-medium">资讯</a>
          </nav>

          <!-- User Actions -->
          <div class="flex items-center gap-4">
            <div class="flex items-center gap-2 text-sm text-gray-600">
              <span class="px-3 py-1 bg-gray-100 rounded-full">用户ID: {{ userId }}</span>
            </div>
            <button
              @click="handleLogout"
              class="flex items-center gap-2 px-4 py-2 text-gray-600 hover:text-red-600 hover:bg-red-50 rounded-lg transition text-sm font-medium"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
              </svg>
              退出
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto px-6 py-8">
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
                <span :class="['text-lg font-bold', overview.totalProfitLoss >= 0 ? 'text-green-600' : 'text-red-600']">
                  {{ formatMoney(overview.totalProfitLoss) }}
                </span>
              </div>
              <p :class="['text-sm mt-1', overview.totalProfitLoss >= 0 ? 'text-green-500' : 'text-red-500']">
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
              <div :class="['text-lg font-medium', quote.change >= 0 ? 'text-green-600' : 'text-red-600']">
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
            <button class="bg-blue-600 hover:bg-blue-700 text-white py-4 rounded-xl transition shadow-lg shadow-blue-200 font-medium">
              买入
            </button>
            <button class="bg-orange-500 hover:bg-orange-600 text-white py-4 rounded-xl transition shadow-lg shadow-orange-200 font-medium">
              卖出
            </button>
            <button class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium">
              持仓
            </button>
            <button class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium">
              订单
            </button>
          </div>

          <!-- Market Summary -->
          <div class="mt-6 p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl">
            <p class="text-sm text-gray-500 mb-2">市场动态</p>
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
              <span class="text-sm text-gray-600">市场交易中</span>
            </div>
          </div>
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
                <td :class="['text-right font-medium', p.profitLoss >= 0 ? 'text-green-600' : 'text-red-600']">
                  {{ formatMoney(p.profitLoss) }}
                  <div class="text-xs">{{ formatPercent(p.profitLossPercent) }}</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</template>