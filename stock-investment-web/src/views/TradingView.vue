<script setup lang="ts">
import { ref } from 'vue'
import OrderForm from '../components/OrderForm.vue'
import OrderList from '../components/OrderList.vue'

const userId = Number(localStorage.getItem('userId')) || 1
const refreshKey = ref(0)

const onOrdered = () => {
  refreshKey.value++
}
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">交易</h1>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">下单</h2>
        <OrderForm @ordered="onOrdered" />
      </div>
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">订单历史</h2>
        <OrderList :userId="userId" :refreshKey="refreshKey" />
      </div>
    </div>
  </div>
</template>
