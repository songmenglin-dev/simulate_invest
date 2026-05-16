<script setup lang="ts">
import { ref } from 'vue'
import { placeOrder } from '../services/api'
import StockSearch from './StockSearch.vue'

const emit = defineEmits(['ordered'])

const userId = Number(localStorage.getItem('userId')) || 1
const form = ref({
  fundAccountId: 1,
  stockCode: '',
  stockName: '',
  direction: 1 as number,
  price: null as number | null,
  quantity: null as number | null,
})
const loading = ref(false)
const error = ref('')
const success = ref('')
const showConfirm = ref(false)
const lastOrder = ref<any>(null)

const selectedStockName = ref('')

const onStockSelect = (item: { stockCode: string; stockName: string }) => {
  form.value.stockCode = item.stockCode
  form.value.stockName = item.stockName
  selectedStockName.value = item.stockName
}

const handleSubmit = async () => {
  error.value = ''
  success.value = ''
  if (!form.value.stockCode || !form.value.price || !form.value.quantity) {
    error.value = '请填写完整信息'
    return
  }
  loading.value = true
  try {
    const order = await placeOrder({
      userId,
      fundAccountId: form.value.fundAccountId,
      stockCode: form.value.stockCode,
      stockName: form.value.stockName,
      direction: form.value.direction === 1 ? 'BUY' as any : 'SELL' as any,
      price: form.value.price!,
      quantity: form.value.quantity!,
      orderType: '1',
    }) as any
    lastOrder.value = { ...order, price: form.value.price, quantity: form.value.quantity, stockName: form.value.stockName, direction: form.value.direction }
    showConfirm.value = true
    success.value = '下单成功！请确认或取消订单'
  } catch (err: any) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

const confirmOrder = async () => {
  if (!lastOrder.value) return
  try {
    const { confirmOrder: confirmApi } = await import('../services/api')
    await confirmApi(lastOrder.value.id, userId)
    showConfirm.value = false
    success.value = '订单已确认，交易成功！'
    emit('ordered')
  } catch (err: any) {
    error.value = err.message
  }
}

const cancelOrder = async () => {
  if (!lastOrder.value) return
  try {
    const { cancelOrder: cancelApi } = await import('../services/api')
    await cancelApi(lastOrder.value.id, userId)
    showConfirm.value = false
    success.value = '订单已取消'
    emit('ordered')
  } catch (err: any) {
    error.value = err.message
  }
}
</script>

<template>
  <div>
    <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-xl mb-4 text-sm">{{ error }}</div>
    <div v-if="success" class="bg-green-50 text-green-600 p-4 rounded-xl mb-4 text-sm">{{ success }}</div>

    <!-- Confirm Dialog -->
    <div v-if="showConfirm && lastOrder" class="bg-blue-50 border border-blue-200 rounded-xl p-6 mb-4">
      <h3 class="font-semibold text-blue-900 mb-3">确认订单</h3>
      <div class="space-y-2 text-sm text-blue-800">
        <p>股票: {{ lastOrder.stockName }} ({{ lastOrder.stockCode }})</p>
        <p>方向: {{ lastOrder.direction === 1 ? '买入' : '卖出' }}</p>
        <p>价格: ¥{{ lastOrder.price }}</p>
        <p>数量: {{ lastOrder.quantity }} 股</p>
        <p class="font-semibold">金额: ¥{{ (lastOrder.price * lastOrder.quantity).toFixed(2) }}</p>
      </div>
      <div class="flex gap-3 mt-4">
        <button @click="confirmOrder" class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg text-sm font-medium transition">确认</button>
        <button @click="cancelOrder" class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-6 py-2 rounded-lg text-sm font-medium transition">取消</button>
      </div>
    </div>

    <form @submit.prevent="handleSubmit" class="space-y-4">
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-2">股票</label>
        <StockSearch :modelValue="form.stockCode" @select="onStockSelect" />
      </div>
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-2">买卖方向</label>
        <div class="flex gap-2">
          <button type="button" :class="['flex-1 py-3 rounded-xl font-medium transition', form.direction === 1 ? 'bg-red-500 text-white' : 'bg-gray-100 text-gray-500']" @click="form.direction = 1">买入</button>
          <button type="button" :class="['flex-1 py-3 rounded-xl font-medium transition', form.direction === 2 ? 'bg-green-500 text-white' : 'bg-gray-100 text-gray-500']" @click="form.direction = 2">卖出</button>
        </div>
      </div>
      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="text-sm font-medium text-gray-700 block mb-2">价格</label>
          <input v-model.number="form.price" type="number" step="0.01" class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition" placeholder="0.00" />
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700 block mb-2">数量(股)</label>
          <input v-model.number="form.quantity" type="number" step="100" class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition" placeholder="100" />
        </div>
      </div>
      <button type="submit" :disabled="loading" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 rounded-xl transition disabled:opacity-50">
        {{ loading ? '提交中...' : '下单' }}
      </button>
    </form>
  </div>
</template>
