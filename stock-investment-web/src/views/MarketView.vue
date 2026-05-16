<script setup lang="ts">
import { ref, watch } from 'vue'
import { getQuote } from '../services/api'
import StockSearch from '../components/StockSearch.vue'
import KLineChart from '../components/KLineChart.vue'

const stockCode = ref('')
const quote = ref<any>(null)

const onStockSelect = (item: { stockCode: string; stockName: string }) => {
  stockCode.value = item.stockCode
}

watch(stockCode, async (code) => {
  if (code) {
    try {
      quote.value = await getQuote(code)
    } catch (err) {
      console.error(err)
    }
  }
})

const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">行情</h1>
    </div>

    <!-- Search -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">股票搜索</h2>
      <StockSearch :modelValue="stockCode" @select="onStockSelect" />
    </div>

    <!-- Quote -->
    <div v-if="quote" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
      <div class="flex justify-between items-start mb-4">
        <div>
          <h2 class="text-xl font-bold text-gray-900">{{ quote.stockName }}</h2>
          <p class="text-sm text-gray-400">{{ quote.stockCode }}</p>
        </div>
        <div class="text-right">
          <p class="text-3xl font-bold text-gray-900">{{ quote.currentPrice?.toFixed(2) }}</p>
          <p :class="['text-lg font-medium', quote.change >= 0 ? 'text-green-600' : 'text-red-600']">
            {{ quote.change >= 0 ? '+' : '' }}{{ quote.change?.toFixed(2) }} ({{ formatPercent(quote.changePercent) }})
          </p>
        </div>
      </div>
      <div class="grid grid-cols-4 gap-3 text-sm">
        <div class="p-3 bg-gray-50 rounded-xl text-center">
          <p class="text-gray-500 text-xs mb-1">开盘</p>
          <p class="font-medium text-gray-900">{{ quote.open?.toFixed(2) }}</p>
        </div>
        <div class="p-3 bg-gray-50 rounded-xl text-center">
          <p class="text-gray-500 text-xs mb-1">最高</p>
          <p class="font-medium text-red-600">{{ quote.high?.toFixed(2) }}</p>
        </div>
        <div class="p-3 bg-gray-50 rounded-xl text-center">
          <p class="text-gray-500 text-xs mb-1">最低</p>
          <p class="font-medium text-green-600">{{ quote.low?.toFixed(2) }}</p>
        </div>
        <div class="p-3 bg-gray-50 rounded-xl text-center">
          <p class="text-gray-500 text-xs mb-1">成交量</p>
          <p class="font-medium text-gray-900">{{ (quote.volume / 10000).toFixed(2) }}万</p>
        </div>
      </div>
    </div>

    <!-- K-line Chart -->
    <div v-if="stockCode" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">K线图</h2>
      <KLineChart :stockCode="stockCode" />
    </div>

    <!-- Empty State -->
    <div v-if="!stockCode" class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center text-gray-400">
      <svg class="w-16 h-16 mx-auto text-gray-200 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
      </svg>
      <p>请搜索股票查看行情</p>
    </div>
  </div>
</template>
