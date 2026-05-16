<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import {
  getWatchlist,
  getWatchlistQuotes,
  searchStocks,
  addToWatchlist,
  removeFromWatchlist,
} from '../services/api'

const userId = ref<number>(Number(localStorage.getItem('userId') || '0'))

// ── Watchlist state ──
const watchlistItems = ref<Array<{
  id: number
  stockCode: string
  stockName: string
  currentPrice: number
  change: number
  changePercent: number
  createTime: string
}>>([])
const loading = ref(true)
const error = ref('')

const loadWatchlist = async () => {
  error.value = ''
  try {
    watchlistItems.value = await getWatchlistQuotes(userId.value)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载自选列表失败'
  } finally {
    loading.value = false
  }
}

// ── Auto-refresh ──
let refreshTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  loadWatchlist()
  refreshTimer = setInterval(loadWatchlist, 5_000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})

// ── Remove ──
const removingCode = ref<string | null>(null)

const handleRemove = async (stockCode: string) => {
  removingCode.value = stockCode
  try {
    await removeFromWatchlist(userId.value, stockCode)
    watchlistItems.value = watchlistItems.value.filter((item) => item.stockCode !== stockCode)
  } catch (err) {
    error.value = err instanceof Error ? err.message : '取消关注失败'
  } finally {
    removingCode.value = null
  }
}

// ── Add stock modal ──
const showAddModal = ref(false)
const addSearchKeyword = ref('')
const addSearchResults = ref<Array<{ id: number; stockCode: string; stockName: string }>>([])
const addSearchLoading = ref(false)
const addSearchError = ref('')
const addingCode = ref<string | null>(null)

const openAddModal = () => {
  addSearchKeyword.value = ''
  addSearchResults.value = []
  addSearchError.value = ''
  showAddModal.value = true
}

const closeAddModal = () => {
  showAddModal.value = false
  addSearchKeyword.value = ''
  addSearchResults.value = []
  addSearchError.value = ''
}

const handleAddSearch = async () => {
  const kw = addSearchKeyword.value.trim()
  if (!kw) {
    addSearchResults.value = []
    return
  }
  addSearchLoading.value = true
  addSearchError.value = ''
  try {
    addSearchResults.value = await searchStocks(kw)
  } catch (err) {
    addSearchError.value = err instanceof Error ? err.message : '搜索失败'
  } finally {
    addSearchLoading.value = false
  }
}

const handleAddStock = async (stock: { id: number; stockCode: string; stockName: string }) => {
  // Check if already in watchlist
  if (watchlistItems.value.some((item) => item.stockCode === stock.stockCode)) {
    addSearchError.value = '该股票已在自选列表中'
    return
  }

  addingCode.value = stock.stockCode
  try {
    await addToWatchlist({
      userId: userId.value,
      stockCode: stock.stockCode,
      stockName: stock.stockName,
    })
    closeAddModal()
    loadWatchlist()
  } catch (err) {
    addSearchError.value = err instanceof Error ? err.message : '添加失败'
  } finally {
    addingCode.value = null
  }
}

// ── Format helpers ──
const formatPrice = (v: number): string => {
  if (v === null || v === undefined) return '-'
  return v.toFixed(2)
}

const formatChange = (v: number): string => {
  if (v === null || v === undefined) return '-'
  return `${v >= 0 ? '+' : ''}${v.toFixed(2)}`
}

const formatChangePercent = (v: number): string => {
  if (v === null || v === undefined) return '-'
  return `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`
}
</script>

<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-2">
        <div class="w-10 h-10 bg-yellow-100 rounded-xl flex items-center justify-center">
          <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
          </svg>
        </div>
        <div>
          <h1 class="text-2xl font-bold text-gray-900">我的自选</h1>
          <div class="flex items-center gap-1 mt-0.5">
            <span class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
            <span class="text-xs text-gray-400">每5秒自动刷新</span>
          </div>
        </div>
      </div>
      <button
        @click="openAddModal"
        class="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium text-sm transition-all shadow-lg shadow-blue-200 flex items-center gap-1.5"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/>
        </svg>
        添加股票
      </button>
    </div>

    <!-- Error state -->
    <div v-if="error" class="bg-red-50 border border-red-200 rounded-xl px-4 py-3 text-red-700 text-sm mb-6 flex items-center gap-2">
      <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
      </svg>
      {{ error }}
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="n in 6"
        :key="n"
        class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 animate-pulse"
      >
        <div class="flex justify-between items-start mb-4">
          <div>
            <div class="h-5 w-20 bg-gray-200 rounded mb-2"></div>
            <div class="h-4 w-14 bg-gray-100 rounded"></div>
          </div>
          <div class="text-right">
            <div class="h-7 w-24 bg-gray-200 rounded mb-2"></div>
            <div class="h-4 w-20 bg-gray-100 rounded"></div>
          </div>
        </div>
        <div class="flex gap-2 mt-6">
          <div class="flex-1 h-10 bg-gray-100 rounded-xl"></div>
          <div class="flex-1 h-10 bg-gray-100 rounded-xl"></div>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div
      v-else-if="watchlistItems.length === 0 && !loading"
      class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center"
    >
      <svg class="w-20 h-20 mx-auto text-gray-200 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"/>
      </svg>
      <p class="text-gray-500 text-lg font-medium mb-2">暂无自选股票</p>
      <p class="text-gray-400 text-sm mb-6">去行情页添加您感兴趣的股票</p>
      <router-link
        to="/market"
        class="inline-flex items-center gap-1.5 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-medium text-sm transition-all shadow-lg shadow-blue-200"
      >
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
        </svg>
        去行情页
      </router-link>
    </div>

    <!-- Stock Cards Grid -->
    <div
      v-else
      class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
    >
      <div
        v-for="item in watchlistItems"
        :key="item.stockCode"
        class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow duration-300 relative group"
      >
        <!-- Card content -->
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-lg font-semibold text-gray-900">{{ item.stockName }}</h3>
            <p class="text-sm text-gray-400">{{ item.stockCode }}</p>
          </div>
          <div class="text-right">
            <p class="text-2xl font-bold text-gray-900">
              {{ formatPrice(item.currentPrice) }}
            </p>
            <p :class="['text-sm font-medium mt-0.5', item.change >= 0 ? 'text-red-600' : 'text-green-600']">
              {{ formatChange(item.change) }}
            </p>
            <p :class="['text-xs font-medium', item.change >= 0 ? 'text-red-500' : 'text-green-500']">
              {{ formatChangePercent(item.changePercent) }}
            </p>
          </div>
        </div>

        <!-- Price trend bar (visual indicator) -->
        <div class="h-1 rounded-full mb-5 overflow-hidden bg-gray-100">
          <div
            :class="[
              'h-full rounded-full transition-all duration-500',
              item.change >= 0 ? 'bg-red-500' : 'bg-green-500'
            ]"
            :style="{ width: Math.min(Math.abs(item.changePercent || 0) * 5, 100) + '%' }"
          ></div>
        </div>

        <!-- Action buttons -->
        <div class="flex gap-2">
          <button
            @click="handleRemove(item.stockCode)"
            :disabled="removingCode === item.stockCode"
            :class="[
              'flex-1 py-2.5 rounded-xl text-sm font-medium transition-all',
              removingCode === item.stockCode
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
            ]"
          >
            {{ removingCode === item.stockCode ? '取消中...' : '取消关注' }}
          </button>
          <router-link
            :to="'/trading'"
            class="flex-1 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-sm font-medium transition-all text-center shadow-sm shadow-blue-200"
          >
            去交易
          </router-link>
        </div>
      </div>
    </div>

    <!-- Add Stock Modal -->
    <Transition name="fade">
      <div
        v-if="showAddModal"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
        @click.self="closeAddModal"
      >
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-md mx-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">添加自选股票</h3>

          <!-- Search input -->
          <div class="flex gap-2 mb-3">
            <input
              v-model="addSearchKeyword"
              @keyup.enter="handleAddSearch"
              type="text"
              placeholder="输入股票代码或名称搜索"
              class="flex-1 px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900 placeholder-gray-400 text-sm"
            />
            <button
              @click="handleAddSearch"
              :disabled="addSearchLoading"
              :class="[
                'px-4 py-3 bg-blue-600 text-white rounded-xl text-sm font-medium transition-all',
                addSearchLoading
                  ? 'opacity-60 cursor-not-allowed'
                  : 'hover:bg-blue-700'
              ]"
            >
              {{ addSearchLoading ? '搜索...' : '搜索' }}
            </button>
          </div>

          <!-- Search error -->
          <div v-if="addSearchError" class="mb-3 px-3 py-2 bg-red-50 border border-red-200 rounded-xl text-red-600 text-xs">
            {{ addSearchError }}
          </div>

          <!-- Search results -->
          <div class="max-h-60 overflow-y-auto">
            <div v-if="addSearchResults.length === 0 && !addSearchLoading && addSearchKeyword" class="text-center py-6 text-gray-400 text-sm">
              未找到匹配的股票
            </div>
            <div
              v-for="stock in addSearchResults"
              :key="stock.stockCode"
              :class="[
                'flex justify-between items-center px-4 py-3 rounded-xl transition cursor-pointer mb-1',
                addingCode === stock.stockCode
                  ? 'bg-blue-50'
                  : 'hover:bg-gray-50'
              ]"
              @click="handleAddStock(stock)"
            >
              <div>
                <span class="font-medium text-gray-900">{{ stock.stockCode }}</span>
                <span class="text-gray-500 text-sm ml-2">{{ stock.stockName }}</span>
              </div>
              <button
                :disabled="addingCode === stock.stockCode"
                :class="[
                  'px-3 py-1.5 rounded-lg text-xs font-medium transition-all',
                  addingCode === stock.stockCode
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'bg-blue-50 text-blue-600 hover:bg-blue-100'
                ]"
              >
                {{ addingCode === stock.stockCode ? '添加中' : '加自选' }}
              </button>
            </div>
          </div>

          <!-- Close button -->
          <div class="mt-4">
            <button
              @click="closeAddModal"
              class="w-full py-3 bg-gray-100 text-gray-600 rounded-xl hover:bg-gray-200 transition text-sm font-medium"
            >
              关闭
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
