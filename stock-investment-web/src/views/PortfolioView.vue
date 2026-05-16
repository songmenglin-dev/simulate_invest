<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPortfolioOverview, getPositions, getCashBalance } from '../services/api'

const userId = Number(localStorage.getItem('userId')) || 1
const overview = ref<any>(null)
const positions = ref<any[]>([])
const cash = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    const [overviewData, positionsData, cashData] = await Promise.all([
      getPortfolioOverview(userId),
      getPositions(userId),
      getCashBalance(userId),
    ])
    overview.value = overviewData
    positions.value = positionsData
    cash.value = cashData
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
})

const formatMoney = (v: number) => `¥${(v ?? 0).toFixed(2)}`
const formatPercent = (v: number) => `${(v ?? 0) >= 0 ? '+' : ''}${(v ?? 0).toFixed(2)}%`
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">持仓</h1>
    </div>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <template v-else>
      <!-- Overview Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-sm text-gray-500 mb-1">总资产</p>
          <p class="text-2xl font-bold text-blue-600">{{ formatMoney(overview?.totalAssets) }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-sm text-gray-500 mb-1">持仓市值</p>
          <p class="text-2xl font-bold text-gray-900">{{ formatMoney(overview?.totalMarketValue) }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-sm text-gray-500 mb-1">总盈亏</p>
          <p :class="['text-2xl font-bold', overview?.totalProfitLoss >= 0 ? 'text-red-600' : 'text-green-600']">{{ formatMoney(overview?.totalProfitLoss) }}</p>
          <p :class="['text-sm', overview?.profitLossPercent >= 0 ? 'text-red-500' : 'text-green-500']">{{ formatPercent(overview?.profitLossPercent) }}</p>
        </div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
          <p class="text-sm text-gray-500 mb-1">可用资金</p>
          <p class="text-2xl font-bold text-gray-900">{{ formatMoney(overview?.availableCash) }}</p>
        </div>
      </div>

      <!-- Cash Account -->
      <div v-if="cash" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-5 mb-6">
        <h3 class="text-sm font-semibold text-gray-700 mb-3">资金账户</h3>
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 text-sm">
          <div>
            <span class="text-gray-400">账户号</span>
            <p class="font-medium text-gray-900">{{ cash.accountNo }}</p>
          </div>
          <div>
            <span class="text-gray-400">可用资金</span>
            <p class="font-medium text-gray-900">{{ formatMoney(cash.availableCash) }}</p>
          </div>
          <div>
            <span class="text-gray-400">冻结资金</span>
            <p class="font-medium text-yellow-600">{{ formatMoney(cash.frozenCash) }}</p>
          </div>
          <div>
            <span class="text-gray-400">状态</span>
            <p class="font-medium" :class="cash.status === 1 ? 'text-green-600' : 'text-red-600'">{{ cash.status === 1 ? '正常' : '禁用' }}</p>
          </div>
        </div>
      </div>

      <!-- Positions Table -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">持仓明细</h2>
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
                <th class="text-right py-3 px-4 font-medium">持仓/可用</th>
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
                <td class="text-right py-3 px-4">
                  <div class="text-gray-900 font-medium">{{ p.totalQuantity }}</div>
                  <div class="text-gray-400 text-xs">可用 {{ p.availableQuantity }}</div>
                </td>
                <td class="text-right py-3 px-4 text-gray-600">¥{{ p.avgCost?.toFixed(2) }}</td>
                <td class="text-right py-3 px-4 text-gray-900 font-medium">¥{{ p.currentPrice?.toFixed(2) }}</td>
                <td class="text-right py-3 px-4 font-medium text-gray-900">¥{{ p.marketValue?.toFixed(2) }}</td>
                <td :class="['text-right py-3 px-4 font-medium', p.profitLoss >= 0 ? 'text-red-600' : 'text-green-600']">
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
