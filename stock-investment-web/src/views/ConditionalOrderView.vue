<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  searchStocks,
  getSimulatedQuotes,
  getPositions,
  createConditionalOrder,
  getConditionalOrders,
  cancelConditionalOrder,
} from '../services/api'

const userId = ref<number>(Number(localStorage.getItem('userId') || '0'))

// ── Create form state ──
const stocks = ref<Array<{ id: number; stockCode: string; stockName: string }>>([])
const selectedStockCode = ref('')
const conditionType = ref<'TAKE_PROFIT' | 'STOP_LOSS'>('TAKE_PROFIT')
const triggerPrice = ref<number | null>(null)
const orderPrice = ref<number | null>(null)
const quantity = ref<number | null>(null)
const creating = ref(false)
const createFormError = ref('')
const createFormSuccess = ref('')
const currentPrice = ref<number | null>(null)
const priceLoading = ref(false)

// Position check
const positions = ref<any[]>([])
const hasPosition = computed(() =>
  positions.value.some((p) => p.stockCode === selectedStockCode.value && p.totalQuantity > 0)
)
const selectedPosition = computed(() =>
  positions.value.find((p) => p.stockCode === selectedStockCode.value)
)

// Load stocks and positions on mount
const loadStocksAndPositions = async () => {
  try {
    const [s, p] = await Promise.all([
      searchStocks(),
      getPositions(userId.value),
    ])
    stocks.value = s
    positions.value = p
  } catch (err) {
    createFormError.value = '加载数据失败，请刷新重试'
  }
}

// Load price when stock selected
const loadCurrentPrice = async () => {
  if (!selectedStockCode.value) {
    currentPrice.value = null
    return
  }
  priceLoading.value = true
  try {
    const quotes = await getSimulatedQuotes([selectedStockCode.value])
    if (quotes.length > 0) {
      currentPrice.value = quotes[0].currentPrice
      if (orderPrice.value === null) {
        orderPrice.value = quotes[0].currentPrice
      }
    }
  } catch (err) {
    currentPrice.value = null
  } finally {
    priceLoading.value = false
  }
}

// When trigger price changes, auto-fill order price if empty
const onTriggerPriceChange = () => {
  if (triggerPrice.value !== null && orderPrice.value === null) {
    orderPrice.value = triggerPrice.value
  }
}

// Submit create form
const handleCreate = async () => {
  createFormError.value = ''
  createFormSuccess.value = ''

  if (!selectedStockCode.value) {
    createFormError.value = '请选择股票'
    return
  }
  if (!conditionType.value) {
    createFormError.value = '请选择条件类型'
    return
  }
  if (triggerPrice.value === null || triggerPrice.value <= 0) {
    createFormError.value = '请输入有效的触发价格'
    return
  }
  if (orderPrice.value === null || orderPrice.value <= 0) {
    createFormError.value = '请输入有效的委托价格'
    return
  }
  if (quantity.value === null || quantity.value <= 0 || !Number.isInteger(quantity.value)) {
    createFormError.value = '请输入有效的委托数量'
    return
  }
  if (quantity.value % 100 !== 0) {
    createFormError.value = '数量必须是100的整数倍（整手交易）'
    return
  }
  if (!hasPosition.value) {
    createFormError.value = '您未持有该股票，无法创建条件单'
    return
  }

  const selectedStock = stocks.value.find((s) => s.stockCode === selectedStockCode.value)
  if (!selectedStock) {
    createFormError.value = '未找到所选股票'
    return
  }

  // Direction: TAKE_PROFIT = sell (2), STOP_LOSS = sell (2)
  // Both take-profit and stop-loss are sell direction actions
  const direction = 2

  creating.value = true
  try {
    await createConditionalOrder({
      userId: userId.value,
      fundAccountId: 1,
      stockCode: selectedStockCode.value,
      stockName: selectedStock.stockName,
      conditionType: conditionType.value,
      triggerPrice: triggerPrice.value!,
      orderPrice: orderPrice.value!,
      quantity: quantity.value!,
      direction,
    })
    createFormSuccess.value = '条件单创建成功'
    triggerPrice.value = null
    orderPrice.value = null
    quantity.value = null
    loadConditionalOrders()
  } catch (err: unknown) {
    createFormError.value = err instanceof Error ? err.message : '创建失败，请重试'
  } finally {
    creating.value = false
  }
}

// ── Conditional orders list ──
const orders = ref<any[]>([])
const ordersLoading = ref(true)
const ordersError = ref('')
const activeFilter = ref('全部')
const filters = ['全部', '活跃中', '已触发', '已取消', '已过期']
const cancellingId = ref<number | null>(null)
const showCancelConfirm = ref(false)
const cancelTargetId = ref<number | null>(null)

const filteredOrders = computed(() => {
  if (activeFilter.value === '全部') return orders.value
  const statusMap: Record<string, string> = {
    '活跃中': 'ACTIVE',
    '已触发': 'TRIGGERED',
    '已取消': 'CANCELLED',
    '已过期': 'EXPIRED',
  }
  return orders.value.filter((o) => o.status === statusMap[activeFilter.value])
})

const loadConditionalOrders = async () => {
  ordersError.value = ''
  try {
    orders.value = await getConditionalOrders(userId.value)
  } catch (err) {
    ordersError.value = err instanceof Error ? err.message : '加载条件单列表失败'
  } finally {
    ordersLoading.value = false
  }
}

const handleCancel = async (id: number) => {
  cancellingId.value = id
  try {
    await cancelConditionalOrder(id, userId.value)
    loadConditionalOrders()
  } catch (err) {
    ordersError.value = err instanceof Error ? err.message : '取消失败，请重试'
  } finally {
    cancellingId.value = null
    showCancelConfirm.value = false
    cancelTargetId.value = null
  }
}

const openCancelDialog = (id: number) => {
  cancelTargetId.value = id
  showCancelConfirm.value = true
}

const closeCancelDialog = () => {
  showCancelConfirm.value = false
  cancelTargetId.value = null
}

// Status display mapping
const statusLabel = (status: string): string => {
  const map: Record<string, string> = {
    ACTIVE: '活跃中',
    TRIGGERED: '已触发',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
  }
  return map[status] || status
}

const statusClass = (status: string): string => {
  const map: Record<string, string> = {
    ACTIVE: 'bg-blue-100 text-blue-700',
    TRIGGERED: 'bg-purple-100 text-purple-700',
    CANCELLED: 'bg-gray-100 text-gray-500',
    EXPIRED: 'bg-orange-100 text-orange-700',
  }
  return map[status] || 'bg-gray-100 text-gray-500'
}

const conditionLabel = (type: string): string => {
  return type === 'TAKE_PROFIT' ? '止盈' : '止损'
}

const conditionTagClass = (type: string): string => {
  return type === 'TAKE_PROFIT'
    ? 'bg-red-100 text-red-700'
    : 'bg-green-100 text-green-700'
}

const formatTime = (timeStr: string): string => {
  if (!timeStr) return '-'
  try {
    const d = new Date(timeStr)
    const pad = (n: number) => String(n).padStart(2, '0')
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
  } catch {
    return timeStr
  }
}

// ── Lifecycle ──
let refreshTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  loadStocksAndPositions()
  loadConditionalOrders()
  refreshTimer = setInterval(loadConditionalOrders, 10_000)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-orange-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">条件单</h1>
    </div>

    <!-- Create Form Section -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">创建条件单</h2>

      <!-- Success message -->
      <div v-if="createFormSuccess" class="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-xl text-green-700 text-sm flex items-center gap-2">
        <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ createFormSuccess }}
      </div>

      <!-- Error message -->
      <div v-if="createFormError" class="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm flex items-center gap-2">
        <svg class="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        {{ createFormError }}
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <!-- Stock selector -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">选择股票</label>
          <select
            v-model="selectedStockCode"
            @change="loadCurrentPrice"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
          >
            <option value="">请选择股票</option>
            <option v-for="s in stocks" :key="s.stockCode" :value="s.stockCode">
              {{ s.stockCode }} - {{ s.stockName }}
            </option>
          </select>
        </div>

        <!-- Current price display -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">当前价格</label>
          <div class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl text-gray-900">
            <span v-if="priceLoading" class="text-gray-400">加载中...</span>
            <span v-else-if="currentPrice !== null" :class="currentPrice >= 0 ? 'text-red-600 font-semibold' : 'text-green-600 font-semibold'">
              {{ currentPrice.toFixed(2) }}
            </span>
            <span v-else class="text-gray-400">请先选择股票</span>
          </div>
        </div>

        <!-- Condition type -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">条件类型</label>
          <div class="flex gap-2">
            <button
              @click="conditionType = 'TAKE_PROFIT'"
              :class="[
                'flex-1 py-3 px-4 rounded-xl font-medium text-sm transition-all',
                conditionType === 'TAKE_PROFIT'
                  ? 'bg-red-600 text-white shadow-lg shadow-red-200'
                  : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
              ]"
            >
              止盈
            </button>
            <button
              @click="conditionType = 'STOP_LOSS'"
              :class="[
                'flex-1 py-3 px-4 rounded-xl font-medium text-sm transition-all',
                conditionType === 'STOP_LOSS'
                  ? 'bg-green-600 text-white shadow-lg shadow-green-200'
                  : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
              ]"
            >
              止损
            </button>
          </div>
        </div>

        <!-- Trigger price -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            触发价格
            <span class="text-gray-400 font-normal ml-1">（达到此价格时触发）</span>
          </label>
          <input
            v-model.number="triggerPrice"
            @input="onTriggerPriceChange"
            type="number"
            step="0.01"
            min="0"
            placeholder="请输入触发价格"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900 placeholder-gray-400"
          />
        </div>

        <!-- Order price -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            委托价格
            <span class="text-gray-400 font-normal ml-1">（触发后下单价格）</span>
          </label>
          <input
            v-model.number="orderPrice"
            type="number"
            step="0.01"
            min="0"
            placeholder="默认为触发价格"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900 placeholder-gray-400"
          />
        </div>

        <!-- Quantity -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            委托数量
            <span class="text-gray-400 font-normal ml-1">（100的整数倍）</span>
          </label>
          <input
            v-model.number="quantity"
            type="number"
            step="100"
            min="100"
            placeholder="请输入委托数量"
            class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900 placeholder-gray-400"
          />
          <p v-if="selectedPosition" class="text-xs text-gray-400 mt-1">
            持仓 {{ selectedPosition.totalQuantity }} 股，可用 {{ selectedPosition.availableQuantity }} 股
          </p>
        </div>
      </div>

      <!-- Submit button -->
      <div class="mt-6">
        <button
          @click="handleCreate"
          :disabled="creating"
          :class="[
            'px-8 py-3 bg-blue-600 text-white font-medium rounded-xl transition-all',
            creating
              ? 'opacity-60 cursor-not-allowed'
              : 'hover:bg-blue-700 shadow-lg shadow-blue-200'
          ]"
        >
          <span v-if="creating" class="flex items-center gap-2">
            <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
            </svg>
            创建中...
          </span>
          <span v-else>创建条件单</span>
        </button>
      </div>
    </div>

    <!-- Conditional Orders List Section -->
    <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-lg font-semibold text-gray-900">条件单列表</h2>
        <span class="text-xs text-gray-400 flex items-center gap-1">
          <span class="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
          每10秒自动刷新
        </span>
      </div>

      <!-- Filter tabs -->
      <div class="flex gap-2 mb-4 flex-wrap">
        <button
          v-for="f in filters"
          :key="f"
          @click="activeFilter = f"
          :class="[
            'px-4 py-2 rounded-xl text-sm font-medium transition-all',
            activeFilter === f
              ? 'bg-blue-600 text-white shadow-md shadow-blue-200'
              : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
          ]"
        >
          {{ f }}
        </button>
      </div>

      <!-- Error -->
      <div v-if="ordersError" class="text-center py-12">
        <div class="inline-flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          {{ ordersError }}
        </div>
      </div>

      <!-- Loading -->
      <div v-else-if="ordersLoading" class="text-center py-12 text-gray-400">
        <svg class="animate-spin w-8 h-8 mx-auto mb-3 text-gray-300" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
        </svg>
        <p>加载中...</p>
      </div>

      <!-- Empty -->
      <div v-else-if="filteredOrders.length === 0" class="text-center py-12 text-gray-400 bg-gray-50 rounded-xl">
        <svg class="w-12 h-12 mx-auto text-gray-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
        </svg>
        <p v-if="activeFilter === '全部'">暂无条件单</p>
        <p v-else>暂无{{ activeFilter }}的条件单</p>
      </div>

      <!-- Table -->
      <div v-else class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="text-gray-500 text-sm border-b border-gray-100">
              <th class="text-left py-3 px-4 font-medium">编号</th>
              <th class="text-left py-3 px-4 font-medium">股票</th>
              <th class="text-center py-3 px-4 font-medium">类型</th>
              <th class="text-right py-3 px-4 font-medium">触发价</th>
              <th class="text-right py-3 px-4 font-medium">下单价</th>
              <th class="text-right py-3 px-4 font-medium">数量</th>
              <th class="text-center py-3 px-4 font-medium">状态</th>
              <th class="text-left py-3 px-4 font-medium">创建时间</th>
              <th class="text-center py-3 px-4 font-medium">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="o in filteredOrders"
              :key="o.id"
              class="border-b border-gray-50 hover:bg-gray-50 transition"
            >
              <td class="py-4 px-4 font-mono text-sm text-gray-600">{{ o.orderNo }}</td>
              <td class="py-4 px-4">
                <div class="font-medium text-gray-900">{{ o.stockName }}</div>
                <div class="text-gray-400 text-xs">{{ o.stockCode }}</div>
              </td>
              <td class="py-4 px-4 text-center">
                <span :class="['inline-block px-2 py-0.5 rounded-full text-xs font-medium', conditionTagClass(o.conditionType)]">
                  {{ conditionLabel(o.conditionType) }}
                </span>
              </td>
              <td class="py-4 px-4 text-right text-gray-900 font-mono">{{ o.triggerPrice?.toFixed(2) }}</td>
              <td class="py-4 px-4 text-right text-gray-900 font-mono">{{ o.orderPrice?.toFixed(2) }}</td>
              <td class="py-4 px-4 text-right text-gray-900">{{ o.quantity }}</td>
              <td class="py-4 px-4 text-center">
                <span :class="['inline-block px-2 py-0.5 rounded-full text-xs font-medium', statusClass(o.status)]">
                  {{ statusLabel(o.status) }}
                </span>
              </td>
              <td class="py-4 px-4 text-sm text-gray-500">{{ formatTime(o.createTime) }}</td>
              <td class="py-4 px-4 text-center">
                <button
                  v-if="o.status === 'ACTIVE'"
                  @click="openCancelDialog(o.id)"
                  :disabled="cancellingId === o.id"
                  :class="[
                    'px-3 py-1.5 rounded-lg text-xs font-medium transition-all',
                    cancellingId === o.id
                      ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                      : 'bg-red-50 text-red-600 hover:bg-red-100'
                  ]"
                >
                  {{ cancellingId === o.id ? '取消中...' : '取消' }}
                </button>
                <span v-else class="text-xs text-gray-400">-</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Cancel Confirmation Dialog -->
    <Transition name="fade">
      <div
        v-if="showCancelConfirm"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
        @click.self="closeCancelDialog"
      >
        <div class="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm mx-4">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">确认取消</h3>
          <p class="text-gray-500 text-sm mb-6">确定要取消该条件单吗？取消后不可恢复。</p>
          <div class="flex gap-3">
            <button
              @click="closeCancelDialog"
              class="flex-1 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 transition font-medium text-sm"
            >
              暂不取消
            </button>
            <button
              @click="cancelTargetId !== null && handleCancel(cancelTargetId)"
              class="flex-1 py-3 bg-red-600 text-white rounded-xl hover:bg-red-700 transition font-medium text-sm"
            >
              确定取消
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
