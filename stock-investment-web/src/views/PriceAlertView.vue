<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import {
  createPriceAlert,
  getPriceAlerts,
  cancelPriceAlert,
  getAlertNotifications,
  markNotificationRead,
  markAllNotificationsRead,
  searchStocks,
  getQuote,
} from '../services/api'

const userId = ref<number>(Number(localStorage.getItem('userId') || '0'))

// --- Stock search ---
interface StockInfo {
  id: number
  stockCode: string
  stockName: string
}

const stocks = ref<StockInfo[]>([])
const stockSearchKeyword = ref('')
const selectedStock = ref('')

const filteredStocks = computed(() => {
  if (!stockSearchKeyword.value) return stocks.value
  const kw = stockSearchKeyword.value.toLowerCase()
  return stocks.value.filter(
    s => s.stockCode.toLowerCase().includes(kw) || s.stockName.toLowerCase().includes(kw)
  )
})

const loadStocks = async () => {
  try {
    stocks.value = await searchStocks()
    if (stocks.value.length > 0 && !selectedStock.value) {
      selectedStock.value = stocks.value[0].stockCode
    }
  } catch {
    stocks.value = []
  }
}

// --- Create alert form ---
const alertType = ref<'PRICE_ABOVE' | 'PRICE_BELOW'>('PRICE_ABOVE')
const targetPrice = ref<number | null>(null)
const currentPrice = ref<number | null>(null)
const selectedStockName = computed(() => {
  const found = stocks.value.find(s => s.stockCode === selectedStock.value)
  return found ? found.stockName : ''
})
const alertCreating = ref(false)
const createError = ref('')
const createSuccess = ref('')

const priceWarning = computed(() => {
  if (currentPrice.value === null || targetPrice.value === null || targetPrice.value <= 0) return ''
  if (alertType.value === 'PRICE_ABOVE' && currentPrice.value >= targetPrice.value) {
    return '当前价格已满足预警条件'
  }
  if (alertType.value === 'PRICE_BELOW' && currentPrice.value <= targetPrice.value) {
    return '当前价格已满足预警条件'
  }
  return ''
})

const isFormValid = computed(() => {
  return selectedStock.value && targetPrice.value && targetPrice.value > 0
})

const loadCurrentPrice = async () => {
  if (!selectedStock.value) {
    currentPrice.value = null
    return
  }
  try {
    const quote = await getQuote(selectedStock.value)
    currentPrice.value = quote.currentPrice
  } catch {
    currentPrice.value = null
  }
}

watch(selectedStock, () => {
  loadCurrentPrice()
})

const handleCreateAlert = async () => {
  if (!isFormValid.value) return
  createError.value = ''
  createSuccess.value = ''
  alertCreating.value = true
  try {
    await createPriceAlert({
      userId: userId.value,
      stockCode: selectedStock.value,
      stockName: selectedStockName.value,
      alertType: alertType.value,
      targetPrice: targetPrice.value!,
    })
    createSuccess.value = '预警创建成功'
    targetPrice.value = null
    await loadAlerts()
  } catch (err: unknown) {
    createError.value = err instanceof Error ? err.message : '创建失败，请重试'
  } finally {
    alertCreating.value = false
  }
}

// --- Alert list ---
interface PriceAlert {
  id: number
  alertNo: string
  stockCode: string
  stockName: string
  alertType: string
  targetPrice: number
  status: string
  createTime: string
  updateTime: string
}

const alerts = ref<PriceAlert[]>([])
const alertFilter = ref<'ALL' | 'ACTIVE' | 'TRIGGERED' | 'CANCELLED'>('ALL')
const alertsLoading = ref(false)
const alertsPage = ref(0)
const alertsPageSize = ref(10)

const filteredAlerts = computed(() => {
  let list = alerts.value
  if (alertFilter.value === 'ACTIVE') list = list.filter(a => a.status === 'ACTIVE')
  if (alertFilter.value === 'TRIGGERED') list = list.filter(a => a.status === 'TRIGGERED')
  if (alertFilter.value === 'CANCELLED') list = list.filter(a => a.status === 'CANCELLED')
  return list
})

const paginatedAlerts = computed(() => {
  const start = alertsPage.value * alertsPageSize.value
  return filteredAlerts.value.slice(start, start + alertsPageSize.value)
})

const totalAlertPages = computed(() => {
  return Math.ceil(filteredAlerts.value.length / alertsPageSize.value)
})

const loadAlerts = async () => {
  alertsLoading.value = true
  try {
    alerts.value = await getPriceAlerts(userId.value)
  } catch {
    alerts.value = []
  } finally {
    alertsLoading.value = false
    alertsPage.value = 0
  }
}

const handleCancelAlert = async (alert: PriceAlert) => {
  try {
    await cancelPriceAlert(alert.id, userId.value)
    alert.status = 'CANCELLED'
  } catch {
    // silently fail
  }
}

const alertTypeLabel = (type: string) => type === 'PRICE_ABOVE' ? '上涨' : '下跌'

const statusLabel = (status: string) => {
  switch (status) {
    case 'ACTIVE': return '活跃中'
    case 'TRIGGERED': return '已触发'
    case 'CANCELLED': return '已取消'
    default: return status
  }
}

const statusClass = (status: string) => {
  switch (status) {
    case 'ACTIVE': return 'bg-blue-100 text-blue-700'
    case 'TRIGGERED': return 'bg-red-100 text-red-700'
    case 'CANCELLED': return 'bg-gray-100 text-gray-500'
    default: return 'bg-gray-100 text-gray-700'
  }
}

const filterTabs: { key: typeof alertFilter.value; label: string }[] = [
  { key: 'ALL', label: '全部' },
  { key: 'ACTIVE', label: '活跃中' },
  { key: 'TRIGGERED', label: '已触发' },
  { key: 'CANCELLED', label: '已取消' },
]

// --- Notification history ---
interface AlertNotification {
  id: number
  alertId: number
  stockCode: string
  stockName: string
  alertType: string
  targetPrice: number
  triggeredPrice: number
  isRead: number
  createTime: string
}

const notifications = ref<AlertNotification[]>([])
const notificationsLoading = ref(false)

const loadNotifications = async () => {
  notificationsLoading.value = true
  try {
    notifications.value = await getAlertNotifications(userId.value)
  } catch {
    notifications.value = []
  } finally {
    notificationsLoading.value = false
  }
}

const handleMarkNotificationRead = async (notification: AlertNotification) => {
  if (notification.isRead) return
  try {
    await markNotificationRead(notification.id)
    notification.isRead = 1
  } catch {
    // silently fail
  }
}

const handleMarkAllNotificationsRead = async () => {
  try {
    await markAllNotificationsRead(userId.value)
    notifications.value.forEach(n => { n.isRead = 1 })
  } catch {
    // silently fail
  }
}

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMin = Math.floor(diffMs / 60000)
  const diffHour = Math.floor(diffMs / 3600000)
  const diffDay = Math.floor(diffMs / 86400000)

  if (diffMin < 1) return '刚刚'
  if (diffMin < 60) return `${diffMin}分钟前`
  if (diffHour < 24) return `${diffHour}小时前`
  if (diffDay < 7) return `${diffDay}天前`
  return date.toLocaleDateString('zh-CN')
}

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
  loadStocks()
  loadAlerts()
  loadNotifications()
})
</script>

<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-red-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">价格预警</h1>
    </div>

    <div class="space-y-6">
      <!-- Create Alert + Alert List -->
      <div class="space-y-6">
        <!-- Create Alert Form -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-5">创建预警</h2>

          <!-- Stock Selector -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">选择股票</label>
            <div class="relative">
              <input
                v-model="stockSearchKeyword"
                type="text"
                placeholder="搜索股票代码或名称..."
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900"
              />
              <svg class="absolute right-4 top-3.5 w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
              </svg>
            </div>
            <div class="mt-2 max-h-40 overflow-y-auto border border-gray-100 rounded-xl" v-if="filteredStocks.length > 0 && stockSearchKeyword">
              <button
                v-for="s in filteredStocks"
                :key="s.stockCode"
                @click="selectedStock = s.stockCode; stockSearchKeyword = ''"
                class="w-full text-left px-4 py-2.5 hover:bg-blue-50 transition text-sm"
                :class="{ 'bg-blue-50 text-blue-700': selectedStock === s.stockCode }"
              >
                <span class="font-medium">{{ s.stockCode }}</span>
                <span class="text-gray-500 ml-2">{{ s.stockName }}</span>
              </button>
            </div>
            <select
              v-model="selectedStock"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900 mt-2"
            >
              <option value="" disabled>请选择股票</option>
              <option v-for="s in stocks" :key="s.stockCode" :value="s.stockCode">
                {{ s.stockCode }} - {{ s.stockName }}
              </option>
            </select>
          </div>

          <!-- Alert Type Toggle -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">预警类型</label>
            <div class="flex gap-2">
              <button
                @click="alertType = 'PRICE_ABOVE'"
                class="flex-1 py-2.5 px-4 rounded-xl font-medium text-sm transition border"
                :class="alertType === 'PRICE_ABOVE'
                  ? 'bg-red-50 border-red-300 text-red-700'
                  : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'"
              >
                <span class="inline-flex items-center gap-1">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"/>
                  </svg>
                  上涨预警
                </span>
              </button>
              <button
                @click="alertType = 'PRICE_BELOW'"
                class="flex-1 py-2.5 px-4 rounded-xl font-medium text-sm transition border"
                :class="alertType === 'PRICE_BELOW'
                  ? 'bg-green-50 border-green-300 text-green-700'
                  : 'bg-gray-50 border-gray-200 text-gray-600 hover:bg-gray-100'"
              >
                <span class="inline-flex items-center gap-1">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                  下跌预警
                </span>
              </button>
            </div>
          </div>

          <!-- Target Price -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">目标价格</label>
            <div class="relative">
              <span class="absolute left-4 top-3 text-gray-400">¥</span>
              <input
                v-model.number="targetPrice"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="请输入目标价格"
                class="w-full pl-8 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900"
              />
            </div>
            <p v-if="targetPrice !== null && targetPrice <= 0" class="text-red-500 text-xs mt-1">
              价格必须大于0
            </p>
          </div>

          <!-- Current Price -->
          <div v-if="selectedStock && currentPrice !== null" class="mb-4 p-3 bg-gray-50 rounded-xl">
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-500">当前价格 ({{ selectedStockName }})</span>
              <span class="text-lg font-bold text-gray-900">¥{{ currentPrice.toFixed(2) }}</span>
            </div>
          </div>

          <!-- Warning -->
          <div v-if="priceWarning" class="mb-4 p-3 bg-amber-50 border border-amber-200 rounded-xl flex items-center gap-2">
            <svg class="w-5 h-5 text-amber-500 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 16.5c-.77.833.192 2.5 1.732 2.5z"/>
            </svg>
            <span class="text-sm text-amber-700">{{ priceWarning }}</span>
          </div>

          <!-- Messages -->
          <div v-if="createError" class="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-sm text-red-700">
            {{ createError }}
          </div>
          <div v-if="createSuccess" class="mb-4 p-3 bg-green-50 border border-green-200 rounded-xl text-sm text-green-700">
            {{ createSuccess }}
          </div>

          <!-- Submit -->
          <button
            @click="handleCreateAlert"
            :disabled="!isFormValid || alertCreating"
            class="w-full py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white font-medium rounded-xl transition flex items-center justify-center gap-2"
          >
            <svg v-if="alertCreating" class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
            </svg>
            {{ alertCreating ? '创建中...' : '创建预警' }}
          </button>
        </div>

        <!-- Alert List -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-5">预警列表</h2>

          <!-- Filter Tabs -->
          <div class="flex gap-1 mb-4 p-1 bg-gray-100 rounded-xl">
            <button
              v-for="tab in filterTabs"
              :key="tab.key"
              @click="alertFilter = tab.key; alertsPage = 0"
              class="flex-1 py-2 text-sm font-medium rounded-lg transition"
              :class="alertFilter === tab.key
                ? 'bg-white text-blue-600 shadow-sm'
                : 'text-gray-500 hover:text-gray-700'"
            >
              {{ tab.label }}
            </button>
          </div>

          <!-- Loading -->
          <div v-if="alertsLoading" class="flex items-center justify-center py-12">
            <div class="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin" />
          </div>

          <!-- Empty -->
          <div v-else-if="filteredAlerts.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400">
            <svg class="w-12 h-12 text-gray-200 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
            </svg>
            <p class="text-sm">暂无预警记录</p>
          </div>

          <!-- Alert Table -->
          <div v-else class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="text-gray-500 text-sm border-b border-gray-100">
                  <th class="text-left py-3 px-4 font-medium">编号</th>
                  <th class="text-left py-3 px-4 font-medium">股票</th>
                  <th class="text-left py-3 px-4 font-medium">类型</th>
                  <th class="text-right py-3 px-4 font-medium">目标价</th>
                  <th class="text-left py-3 px-4 font-medium">状态</th>
                  <th class="text-left py-3 px-4 font-medium hidden md:table-cell">创建时间</th>
                  <th class="text-right py-3 px-4 font-medium">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr
                  v-for="alert in paginatedAlerts"
                  :key="alert.id"
                  class="border-b border-gray-50 hover:bg-gray-50 transition"
                >
                  <td class="py-4 px-4 text-sm text-gray-500 font-mono">{{ alert.alertNo }}</td>
                  <td class="py-4 px-4">
                    <div class="font-medium text-gray-900 text-sm">{{ alert.stockName }}</div>
                    <div class="text-gray-400 text-xs">{{ alert.stockCode }}</div>
                  </td>
                  <td class="py-4 px-4">
                    <span
                      class="inline-flex items-center gap-1 px-2 py-1 rounded-lg text-xs font-medium"
                      :class="alert.alertType === 'PRICE_ABOVE' ? 'bg-red-50 text-red-600' : 'bg-green-50 text-green-600'"
                    >
                      <svg
                        v-if="alert.alertType === 'PRICE_ABOVE'"
                        class="w-3 h-3"
                        fill="none" stroke="currentColor" viewBox="0 0 24 24"
                      >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"/>
                      </svg>
                      <svg
                        v-else
                        class="w-3 h-3"
                        fill="none" stroke="currentColor" viewBox="0 0 24 24"
                      >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                      </svg>
                      {{ alertTypeLabel(alert.alertType) }}
                    </span>
                  </td>
                  <td class="py-4 px-4 text-right font-mono text-sm text-gray-900">¥{{ alert.targetPrice.toFixed(2) }}</td>
                  <td class="py-4 px-4">
                    <span class="px-2 py-1 rounded-lg text-xs font-medium" :class="statusClass(alert.status)">
                      {{ statusLabel(alert.status) }}
                    </span>
                  </td>
                  <td class="py-4 px-4 text-sm text-gray-500 hidden md:table-cell">{{ formatDateTime(alert.createTime) }}</td>
                  <td class="py-4 px-4 text-right">
                    <button
                      v-if="alert.status === 'ACTIVE'"
                      @click="handleCancelAlert(alert)"
                      class="px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 border border-red-200 rounded-lg transition"
                    >
                      取消
                    </button>
                    <span v-else class="text-xs text-gray-300">-</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <div v-if="totalAlertPages > 1" class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
            <button
              @click="alertsPage = Math.max(0, alertsPage - 1)"
              :disabled="alertsPage === 0"
              class="px-3 py-1.5 text-sm font-medium rounded-lg transition"
              :class="alertsPage === 0 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
            >
              上一页
            </button>
            <span class="text-sm text-gray-500">
              {{ alertsPage + 1 }} / {{ totalAlertPages }}
            </span>
            <button
              @click="alertsPage = Math.min(totalAlertPages - 1, alertsPage + 1)"
              :disabled="alertsPage >= totalAlertPages - 1"
              class="px-3 py-1.5 text-sm font-medium rounded-lg transition"
              :class="alertsPage >= totalAlertPages - 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
            >
              下一页
            </button>
          </div>
        </div>
      </div>

      <!-- Notification History -->
      <div>
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-center justify-between mb-5">
            <div class="flex items-center gap-2">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
              </svg>
              <h2 class="text-lg font-semibold text-gray-900">通知记录</h2>
            </div>
            <button
              @click="handleMarkAllNotificationsRead"
              class="text-xs text-blue-600 hover:text-blue-700 font-medium transition"
            >
              全部已读
            </button>
          </div>

          <!-- Loading -->
          <div v-if="notificationsLoading" class="flex items-center justify-center py-12">
            <div class="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin" />
          </div>

          <!-- Empty -->
          <div v-else-if="notifications.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400">
            <svg class="w-12 h-12 text-gray-200 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
            </svg>
            <p class="text-sm">暂无通知记录</p>
          </div>

          <!-- Notification List -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-2">
            <button
              v-for="notification in notifications"
              :key="notification.id"
              @click="handleMarkNotificationRead(notification)"
              class="w-full text-left p-4 rounded-xl transition flex items-start gap-3"
              :class="notification.isRead ? 'bg-gray-50 hover:bg-gray-100' : 'bg-blue-50 hover:bg-blue-100 border border-blue-100'"
            >
              <div class="mt-0.5 flex-shrink-0">
                <span
                  class="block w-2 h-2 rounded-full"
                  :class="notification.isRead ? 'bg-gray-300' : 'bg-blue-500'"
                />
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 mb-1">
                  <span class="font-medium text-gray-900 text-sm">{{ notification.stockName }}</span>
                  <span class="text-xs text-gray-400">{{ notification.stockCode }}</span>
                </div>
                <div class="flex items-center gap-2 text-xs mb-1">
                  <span
                    class="px-1.5 py-0.5 rounded font-medium"
                    :class="notification.alertType === 'PRICE_ABOVE' ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'"
                  >
                    {{ alertTypeLabel(notification.alertType) }}
                  </span>
                  <span class="text-gray-500">
                    ¥{{ notification.targetPrice.toFixed(2) }} &rarr; ¥{{ notification.triggeredPrice.toFixed(2) }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <p class="text-xs text-gray-400">{{ formatTime(notification.createTime) }}</p>
                  <span
                    v-if="!notification.isRead"
                    class="px-1.5 py-0.5 bg-blue-100 text-blue-600 text-xs rounded font-medium"
                  >
                    未读
                  </span>
                </div>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
