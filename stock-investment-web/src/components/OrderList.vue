<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getOrderHistory } from '../services/api'

const props = defineProps<{ userId: number; refreshKey?: number }>()

const orders = ref<any[]>([])
const loading = ref(true)

const statusLabels: Record<number, string> = { 1: '待确认', 2: '已成交', 3: '已取消' }
const statusColors: Record<number, string> = { 1: 'text-yellow-600 bg-yellow-50', 2: 'text-green-600 bg-green-50', 3: 'text-gray-400 bg-gray-100' }
const directionLabels: Record<number, string> = { 1: '买入', 2: '卖出' }
const directionColors: Record<number, string> = { 1: 'text-red-600', 2: 'text-green-600' }

const loadOrders = async () => {
  loading.value = true
  try {
    orders.value = await getOrderHistory(props.userId) as any[]
    orders.value.sort((a, b) => (b.id || 0) - (a.id || 0))
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
watch(() => props.refreshKey, loadOrders)
</script>

<template>
  <div>
    <div v-if="loading" class="text-center py-8 text-gray-400">加载中...</div>
    <div v-else-if="orders.length === 0" class="text-gray-400 text-center py-8 bg-gray-50 rounded-xl">暂无订单记录</div>
    <div v-else class="overflow-x-auto">
      <table class="w-full">
        <thead>
          <tr class="text-gray-500 text-sm border-b border-gray-100">
            <th class="text-left py-3 px-4 font-medium">订单号</th>
            <th class="text-left py-3 px-4 font-medium">股票</th>
            <th class="text-left py-3 px-4 font-medium">方向</th>
            <th class="text-right py-3 px-4 font-medium">价格</th>
            <th class="text-right py-3 px-4 font-medium">数量</th>
            <th class="text-right py-3 px-4 font-medium">金额</th>
            <th class="text-center py-3 px-4 font-medium">状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id" class="border-b border-gray-50 hover:bg-gray-50 transition">
            <td class="py-3 px-4 text-sm text-gray-500 font-mono">{{ o.orderNo?.slice(-8) }}</td>
            <td class="py-3 px-4">
              <div class="font-medium text-gray-900 text-sm">{{ o.stockName }}</div>
              <div class="text-gray-400 text-xs">{{ o.stockCode }}</div>
            </td>
            <td class="py-3 px-4">
              <span :class="['text-sm font-medium', directionColors[o.direction] || 'text-gray-600']">{{ directionLabels[o.direction] || o.direction }}</span>
            </td>
            <td class="py-3 px-4 text-right text-sm text-gray-700">¥{{ o.price?.toFixed(2) }}</td>
            <td class="py-3 px-4 text-right text-sm text-gray-700">{{ o.quantity }}</td>
            <td class="py-3 px-4 text-right text-sm font-medium text-gray-900">¥{{ o.amount?.toFixed(2) }}</td>
            <td class="py-3 px-4 text-center">
              <span :class="['px-2 py-1 rounded-lg text-xs font-medium', statusColors[o.status] || 'text-gray-500 bg-gray-50']">{{ statusLabels[o.status] || o.status }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
