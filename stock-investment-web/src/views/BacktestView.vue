<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  runBacktest,
  getBacktestResult,
  getStrategyTemplates,
  searchStocks,
} from '../services/api'

const route = useRoute()
const router = useRouter()

const userId = ref<number>(Number(localStorage.getItem('userId') || '0'))

// --- Strategy templates ---
interface StrategyParam {
  name: string
  label: string
  type: string
  defaultValue: any
}

interface StrategyTemplate {
  type: string
  name: string
  description: string
  parameters: StrategyParam[]
}

const strategyTemplates = ref<StrategyTemplate[]>([])
const selectedStrategy = ref('')
const strategyLoading = ref(false)

const currentStrategy = computed(() => {
  return strategyTemplates.value.find(s => s.type === selectedStrategy.value)
})

const strategyParams = ref<Record<string, any>>({})

watch(selectedStrategy, () => {
  const tmpl = currentStrategy.value
  if (tmpl) {
    const params: Record<string, any> = {}
    for (const p of tmpl.parameters) {
      params[p.name] = p.defaultValue
    }
    strategyParams.value = params
  }
})

const loadStrategies = async () => {
  strategyLoading.value = true
  try {
    strategyTemplates.value = await getStrategyTemplates()
    if (strategyTemplates.value.length > 0) {
      selectedStrategy.value = strategyTemplates.value[0].type
    }
  } catch {
    strategyTemplates.value = []
  } finally {
    strategyLoading.value = false
  }
}

// --- Stock search ---
interface StockInfo {
  id: number
  stockCode: string
  stockName: string
}

const stocks = ref<StockInfo[]>([])
const selectedStockCode = ref('')

const selectedStockName = computed(() => {
  const found = stocks.value.find(s => s.stockCode === selectedStockCode.value)
  return found ? found.stockName : ''
})

const loadStocks = async () => {
  try {
    stocks.value = await searchStocks()
  } catch {
    stocks.value = []
  }
}

// --- Form ---
const startDate = ref('')
const endDate = ref('')
const initialCapital = ref(100000)

// Default date range: last year
const setDefaultDates = () => {
  const now = new Date()
  const oneYearAgo = new Date(now)
  oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1)

  endDate.value = now.toISOString().split('T')[0]
  startDate.value = oneYearAgo.toISOString().split('T')[0]
}

const isFormValid = computed(() => {
  return selectedStrategy.value && selectedStockCode.value && startDate.value && endDate.value && initialCapital.value > 0
})

// --- Run backtest ---
interface BacktestResult {
  id: number
  resultNo: string
  strategyType: string
  stockCode: string
  stockName: string
  startDate: string
  endDate: string
  initialCapital: number
  finalCapital: number
  totalReturn: number
  annualReturn: number
  maxDrawdown: number
  winRate: number
  totalTrades: number
  winningTrades: number
  sharpeRatio: number
  equityCurveJson: string
  tradesJson: string
}

interface TradeRecord {
  entryDate: string
  exitDate: string
  entryPrice: number
  exitPrice: number
  return_: number
  profitLoss: number
}

const result = ref<BacktestResult | null>(null)
const running = ref(false)
const runError = ref('')
const trades = ref<TradeRecord[]>([])
const equityCurveData = ref<{ date: string; value: number }[]>([])
const tradesPage = ref(0)
const tradesPageSize = ref(10)

const paginatedTrades = computed(() => {
  const start = tradesPage.value * tradesPageSize.value
  return trades.value.slice(start, start + tradesPageSize.value)
})

const totalTradePages = computed(() => {
  return Math.ceil(trades.value.length / tradesPageSize.value)
})

const handleRunBacktest = async () => {
  if (!isFormValid.value) return
  runError.value = ''
  result.value = null
  trades.value = []
  equityCurveData.value = []
  running.value = true

  try {
    const data = await runBacktest({
      userId: userId.value,
      strategyType: selectedStrategy.value,
      stockCode: selectedStockCode.value,
      stockName: selectedStockName.value,
      startDate: startDate.value,
      endDate: endDate.value,
      initialCapital: initialCapital.value,
      parameters: strategyParams.value,
    })
    result.value = data
    tradesPage.value = 0

    // Parse trades
    if (data.tradesJson) {
      try {
        trades.value = JSON.parse(data.tradesJson)
      } catch {
        trades.value = []
      }
    }

    // Parse equity curve
    if (data.equityCurveJson) {
      try {
        equityCurveData.value = JSON.parse(data.equityCurveJson)
      } catch {
        equityCurveData.value = []
      }
    }
  } catch (err: unknown) {
    runError.value = err instanceof Error ? err.message : '回测执行失败，请重试'
  } finally {
    running.value = false
  }
}

const formatMoney = (v: number) => `¥${v.toFixed(2)}`
const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${(v * 100).toFixed(2)}%`
const formatPercentRaw = (v: number) => `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`
const formatNum = (v: number) => v.toFixed(4)

// --- Load from query param ---
onMounted(async () => {
  setDefaultDates()
  await Promise.all([loadStrategies(), loadStocks()])

  // Check if we should load an existing result by ID
  const resultId = route.query.id
  if (resultId) {
    const id = Number(resultId)
    if (id && !isNaN(id)) {
      try {
        const data = await getBacktestResult(id)
        result.value = data
        selectedStrategy.value = data.strategyType
        selectedStockCode.value = data.stockCode
        startDate.value = data.startDate
        endDate.value = data.endDate
        initialCapital.value = data.initialCapital

        if (data.tradesJson) {
          try { trades.value = JSON.parse(data.tradesJson) } catch { trades.value = [] }
        }
        if (data.equityCurveJson) {
          try { equityCurveData.value = JSON.parse(data.equityCurveJson) } catch { equityCurveData.value = [] }
        }
      } catch {
        runError.value = '无法加载回测结果'
      }
    }
  }
})

// Equity curve bar visualization - normalize to percentages of width
const equityMax = computed(() => {
  if (equityCurveData.value.length === 0) return 1
  return Math.max(...equityCurveData.value.map(d => d.value), initialCapital.value)
})

const equityMin = computed(() => {
  if (equityCurveData.value.length === 0) return 0
  return Math.min(...equityCurveData.value.map(d => d.value), initialCapital.value)
})

const equityRange = computed(() => equityMax.value - equityMin.value || 1)

const getBarHeightPercent = (value: number) => {
  return ((value - equityMin.value) / equityRange.value) * 100
}
</script>

<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
        </svg>
      </div>
      <div class="flex-1">
        <h1 class="text-2xl font-bold text-gray-900">策略回测</h1>
      </div>
      <router-link
        to="/backtest/history"
        class="flex items-center gap-1 px-4 py-2 text-sm font-medium text-blue-600 hover:bg-blue-50 rounded-lg transition"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        回测历史
      </router-link>
    </div>

    <!-- Configuration Form -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-5">回测配置</h2>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Strategy Selector -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">选择策略</label>
          <div v-if="strategyLoading" class="flex items-center gap-2 text-gray-400 text-sm py-3">
            <div class="w-4 h-4 border-2 border-gray-300 border-t-transparent rounded-full animate-spin" />
            加载策略中...
          </div>
          <select
            v-else
            v-model="selectedStrategy"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
          >
            <option value="" disabled>请选择策略</option>
            <option v-for="s in strategyTemplates" :key="s.type" :value="s.type">
              {{ s.name }}
            </option>
          </select>
          <p v-if="currentStrategy" class="text-xs text-gray-400 mt-1">{{ currentStrategy.description }}</p>
        </div>

        <!-- Stock Selector -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">选择股票</label>
          <select
            v-model="selectedStockCode"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
          >
            <option value="" disabled>请选择股票</option>
            <option v-for="s in stocks" :key="s.stockCode" :value="s.stockCode">
              {{ s.stockCode }} - {{ s.stockName }}
            </option>
          </select>
        </div>

        <!-- Date Range -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">开始日期</label>
          <input
            v-model="startDate"
            type="date"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">结束日期</label>
          <input
            v-model="endDate"
            type="date"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
          />
        </div>

        <!-- Initial Capital -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">初始资金</label>
          <div class="relative">
            <span class="absolute left-4 top-3 text-gray-400">¥</span>
            <input
              v-model.number="initialCapital"
              type="number"
              min="1"
              step="10000"
              class="w-full pl-8 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
            />
          </div>
        </div>
      </div>

      <!-- Strategy Parameters -->
      <div v-if="currentStrategy && currentStrategy.parameters.length > 0" class="mt-5">
        <label class="block text-sm font-medium text-gray-700 mb-2">策略参数</label>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-3">
          <div v-for="param in currentStrategy.parameters" :key="param.name">
            <label class="block text-xs text-gray-500 mb-1">{{ param.label }}</label>
            <input
              v-if="param.type === 'number' || param.type === 'int'"
              v-model.number="strategyParams[param.name]"
              type="number"
              :step="param.type === 'int' ? 1 : 0.01"
              class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
            />
            <input
              v-else
              v-model="strategyParams[param.name]"
              type="text"
              class="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
            />
          </div>
        </div>
      </div>

      <!-- Run Button -->
      <div class="mt-6">
        <button
          @click="handleRunBacktest"
          :disabled="!isFormValid || running"
          class="w-full md:w-auto px-8 py-3 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-medium rounded-xl transition flex items-center justify-center gap-2"
        >
          <svg v-if="running" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
          </svg>
          {{ running ? '回测运行中...' : '开始回测' }}
        </button>
      </div>

      <!-- Error -->
      <div v-if="runError" class="mt-4 p-4 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700 flex items-start gap-2">
        <svg class="w-5 h-5 flex-shrink-0 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        <span>{{ runError }}</span>
      </div>
    </div>

    <!-- Results -->
    <template v-if="result">
      <!-- Metric Cards -->
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-6">
        <!-- Total Return -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-xs text-gray-500 mb-1">总收益率</p>
          <p class="text-2xl font-bold" :class="result.totalReturn >= 0 ? 'text-red-600' : 'text-green-600'">
            {{ formatPercent(result.totalReturn) }}
          </p>
        </div>

        <!-- Annual Return -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-xs text-gray-500 mb-1">年化收益率</p>
          <p class="text-2xl font-bold" :class="result.annualReturn >= 0 ? 'text-red-600' : 'text-green-600'">
            {{ formatPercent(result.annualReturn) }}
          </p>
        </div>

        <!-- Max Drawdown -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-xs text-gray-500 mb-1">最大回撤</p>
          <p class="text-2xl font-bold text-red-500">
            {{ formatPercentRaw(result.maxDrawdown) }}
          </p>
        </div>

        <!-- Win Rate -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-xs text-gray-500 mb-1">胜率</p>
          <p class="text-2xl font-bold text-gray-900">
            {{ formatPercentRaw(result.winRate) }}
          </p>
        </div>

        <!-- Sharpe Ratio -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-xs text-gray-500 mb-1">夏普比率</p>
          <p class="text-2xl font-bold text-gray-900">
            {{ formatNum(result.sharpeRatio) }}
          </p>
        </div>
      </div>

      <!-- Equity Curve Card -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">净值曲线</h3>

        <div v-if="equityCurveData.length === 0" class="text-center py-8 text-gray-400 text-sm">
          暂无净值曲线数据
        </div>

        <div v-else>
          <!-- Summary -->
          <div class="flex items-center justify-between mb-4 p-4 bg-gray-50 rounded-xl">
            <div class="text-center">
              <p class="text-xs text-gray-500 mb-1">初始资金</p>
              <p class="text-lg font-bold text-gray-900">{{ formatMoney(result.initialCapital) }}</p>
            </div>
            <div class="text-gray-300 text-2xl">&rarr;</div>
            <div class="text-center">
              <p class="text-xs text-gray-500 mb-1">最终资金</p>
              <p class="text-lg font-bold" :class="result.finalCapital >= result.initialCapital ? 'text-red-600' : 'text-green-600'">
                {{ formatMoney(result.finalCapital) }}
              </p>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-500 mb-1">总交易次数</p>
              <p class="text-lg font-bold text-gray-900">{{ result.totalTrades }}</p>
            </div>
            <div class="text-center">
              <p class="text-xs text-gray-500 mb-1">盈利次数</p>
              <p class="text-lg font-bold text-red-600">{{ result.winningTrades }}</p>
            </div>
          </div>

          <!-- Bar Chart Visualization -->
          <div class="relative h-32 flex items-end gap-px">
            <div
              v-for="(point, idx) in equityCurveData"
              :key="idx"
              class="flex-1 rounded-t"
              :class="point.value >= result.initialCapital ? 'bg-red-400' : 'bg-green-400'"
              :style="{ height: Math.max(getBarHeightPercent(point.value), 2) + '%' }"
              :title="`${point.date}: ${formatMoney(point.value)}`"
            />
          </div>

          <!-- Time labels -->
          <div class="flex justify-between mt-2 text-xs text-gray-400">
            <span>{{ equityCurveData[0]?.date || '' }}</span>
            <span>{{ equityCurveData[equityCurveData.length - 1]?.date || '' }}</span>
          </div>
        </div>
      </div>

      <!-- Trade List -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">交易明细</h3>

        <!-- No trades -->
        <div v-if="trades.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400">
          <svg class="w-12 h-12 text-gray-200 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
          </svg>
          <p class="text-sm">策略在回测区间内无交易信号</p>
        </div>

        <!-- Trade Table -->
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="text-gray-500 text-sm border-b border-gray-100">
                <th class="text-left py-3 px-4 font-medium">#</th>
                <th class="text-left py-3 px-4 font-medium">入场日</th>
                <th class="text-left py-3 px-4 font-medium">出场日</th>
                <th class="text-right py-3 px-4 font-medium">入场价</th>
                <th class="text-right py-3 px-4 font-medium">出场价</th>
                <th class="text-right py-3 px-4 font-medium">收益率</th>
                <th class="text-right py-3 px-4 font-medium">盈亏额</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(trade, idx) in paginatedTrades"
                :key="idx"
                class="border-b border-gray-50 hover:bg-gray-50 transition"
              >
                <td class="py-4 px-4 text-sm text-gray-500">{{ (tradesPage * tradesPageSize) + idx + 1 }}</td>
                <td class="py-4 px-4 text-sm text-gray-900">{{ trade.entryDate }}</td>
                <td class="py-4 px-4 text-sm text-gray-900">{{ trade.exitDate }}</td>
                <td class="py-4 px-4 text-sm text-right font-mono text-gray-900">¥{{ trade.entryPrice.toFixed(2) }}</td>
                <td class="py-4 px-4 text-sm text-right font-mono text-gray-900">¥{{ trade.exitPrice.toFixed(2) }}</td>
                <td class="py-4 px-4 text-sm text-right font-medium" :class="trade.return_ >= 0 ? 'text-red-600' : 'text-green-600'">
                  {{ formatPercentRaw(trade.return_) }}
                </td>
                <td class="py-4 px-4 text-sm text-right font-medium" :class="trade.profitLoss >= 0 ? 'text-red-600' : 'text-green-600'">
                  {{ formatMoney(trade.profitLoss) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="totalTradePages > 1" class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
          <button
            @click="tradesPage = Math.max(0, tradesPage - 1)"
            :disabled="tradesPage === 0"
            class="px-3 py-1.5 text-sm font-medium rounded-lg transition"
            :class="tradesPage === 0 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
          >
            上一页
          </button>
          <span class="text-sm text-gray-500">
            {{ tradesPage + 1 }} / {{ totalTradePages }}
          </span>
          <button
            @click="tradesPage = Math.min(totalTradePages - 1, tradesPage + 1)"
            :disabled="tradesPage >= totalTradePages - 1"
            class="px-3 py-1.5 text-sm font-medium rounded-lg transition"
            :class="tradesPage >= totalTradePages - 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
          >
            下一页
          </button>
        </div>
      </div>
    </template>

    <!-- Initial State -->
    <div
      v-if="!result && !running && !runError"
      class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center"
    >
      <svg class="w-16 h-16 text-gray-200 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
      </svg>
      <p class="text-gray-400 text-lg mb-2">配置策略参数并开始回测</p>
      <p class="text-gray-300 text-sm">选择策略、股票和回测区间，点击"开始回测"查看结果</p>
    </div>
  </div>
</template>
