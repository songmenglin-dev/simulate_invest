<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBacktestHistory } from '../services/api'

const router = useRouter()
const userId = ref<number>(Number(localStorage.getItem('userId') || '0'))

interface BacktestHistoryItem {
  id: number
  resultNo: string
  strategyType: string
  stockCode: string
  stockName: string
  startDate: string
  endDate: string
  totalReturn: number
  totalTrades: number
  createTime: string
}

const history = ref<BacktestHistoryItem[]>([])
const loading = ref(false)
const error = ref('')

const loadHistory = async () => {
  loading.value = true
  error.value = ''
  try {
    history.value = await getBacktestHistory(userId.value)
  } catch (err: unknown) {
    error.value = err instanceof Error ? err.message : '加载失败，请重试'
    history.value = []
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (item: BacktestHistoryItem) => {
  router.push({ path: '/backtest', query: { id: String(item.id) } })
}

const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${(v * 100).toFixed(2)}%`

const formatDateTime = (timeStr: string) => {
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  loadHistory()
})
</script>

<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center gap-2 mb-6">
      <router-link
        to="/backtest"
        class="w-10 h-10 bg-gray-100 rounded-xl flex items-center justify-center hover:bg-gray-200 transition"
      >
        <svg class="w-5 h-5 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
        </svg>
      </router-link>
      <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">回测历史</h1>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 flex items-center justify-center">
      <div class="flex flex-col items-center gap-3">
        <div class="w-8 h-8 border-2 border-blue-600 border-t-transparent rounded-full animate-spin" />
        <p class="text-sm text-gray-400">加载回测记录...</p>
      </div>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
      <svg class="w-12 h-12 text-red-200 mx-auto mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
      </svg>
      <p class="text-red-600 font-medium mb-2">加载失败</p>
      <p class="text-gray-400 text-sm mb-4">{{ error }}</p>
      <button
        @click="loadHistory"
        class="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-xl transition"
      >
        重新加载
      </button>
    </div>

    <!-- Empty -->
    <div v-else-if="history.length === 0" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
      <svg class="w-16 h-16 text-gray-200 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
      </svg>
      <p class="text-gray-400 text-lg mb-2">暂无回测记录</p>
      <p class="text-gray-300 text-sm mb-6">运行回测后，记录将显示在这里</p>
      <router-link
        to="/backtest"
        class="inline-flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-xl transition"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        开始回测
      </router-link>
    </div>

    <!-- History Table -->
    <div v-else class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="text-gray-500 text-sm border-b border-gray-100">
              <th class="text-left py-3 px-4 font-medium">策略名称</th>
              <th class="text-left py-3 px-4 font-medium">股票</th>
              <th class="text-left py-3 px-4 font-medium hidden md:table-cell">时间区间</th>
              <th class="text-right py-3 px-4 font-medium">收益率</th>
              <th class="text-right py-3 px-4 font-medium hidden sm:table-cell">交易次数</th>
              <th class="text-right py-3 px-4 font-medium hidden lg:table-cell">创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in history"
              :key="item.id"
              @click="handleViewDetail(item)"
              class="border-b border-gray-50 hover:bg-blue-50/50 transition cursor-pointer"
            >
              <td class="py-4 px-4">
                <div class="font-medium text-gray-900 text-sm">{{ item.strategyType }}</div>
                <div class="text-xs text-gray-400 font-mono">{{ item.resultNo }}</div>
              </td>
              <td class="py-4 px-4">
                <div class="font-medium text-gray-900 text-sm">{{ item.stockName }}</div>
                <div class="text-xs text-gray-400">{{ item.stockCode }}</div>
              </td>
              <td class="py-4 px-4 text-sm text-gray-600 hidden md:table-cell">
                {{ item.startDate }} ~ {{ item.endDate }}
              </td>
              <td class="py-4 px-4 text-right">
                <span class="text-sm font-bold" :class="item.totalReturn >= 0 ? 'text-red-600' : 'text-green-600'">
                  {{ formatPercent(item.totalReturn) }}
                </span>
              </td>
              <td class="py-4 px-4 text-right text-sm text-gray-600 hidden sm:table-cell">
                {{ item.totalTrades }}
              </td>
              <td class="py-4 px-4 text-right text-sm text-gray-500 hidden lg:table-cell">
                {{ formatDateTime(item.createTime) }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="mt-4 text-center text-sm text-gray-400">
        共 {{ history.length }} 条记录 - 点击行查看详情
      </div>
    </div>
  </div>
</template>
