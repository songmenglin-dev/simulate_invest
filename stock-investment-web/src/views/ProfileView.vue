<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProfile, getPortfolioOverview, getPositions } from '../services/api'

const user = ref<any>(null)
const loading = ref(true)
const avatarUrl = ref('')
const uploadMessage = ref('')

const userId = ref<number>(Number(localStorage.getItem('userId')) || 1)
const overview = ref<any>(null)
const positions = ref<any[]>([])

const formatMoney = (v: number) => `¥${v.toFixed(2)}`
const formatPercent = (v: number) => `${v >= 0 ? '+' : ''}${v.toFixed(2)}%`

onMounted(async () => {
  try {
    const [userData, overviewData, positionsData] = await Promise.all([
      getProfile(),
      getPortfolioOverview(userId.value),
      getPositions(userId.value),
    ])
    user.value = userData
    avatarUrl.value = userData?.avatarUrl || ''
    overview.value = overviewData
    positions.value = positionsData
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
})

const onFileSelected = async (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  const token = localStorage.getItem('token')
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileName', file.name)
    const res = await fetch('/api/user/avatar', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData,
    })
    const json = await res.json()
    if (json.code === 200) {
      avatarUrl.value = json.data.avatarUrl
      uploadMessage.value = '头像上传成功'
    } else {
      uploadMessage.value = json.message || '上传失败'
    }
  } catch (err: any) {
    uploadMessage.value = err.message
  }
}
</script>

<template>
  <div>
    <div class="flex items-center gap-2 mb-6">
      <div class="w-10 h-10 bg-cyan-100 rounded-xl flex items-center justify-center">
        <svg class="w-5 h-5 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
        </svg>
      </div>
      <h1 class="text-2xl font-bold text-gray-900">个人信息</h1>
    </div>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <template v-else-if="user">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Profile Info -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">基本资料</h2>
          <div class="space-y-4">
            <div>
              <label class="text-sm text-gray-400">用户名</label>
              <p class="text-lg font-medium text-gray-900">{{ user.username }}</p>
            </div>
            <div>
              <label class="text-sm text-gray-400">邮箱</label>
              <p class="text-lg font-medium text-gray-900">{{ user.email || '未设置' }}</p>
            </div>
            <div>
              <label class="text-sm text-gray-400">手机号</label>
              <p class="text-lg font-medium text-gray-900">{{ user.phone || '未设置' }}</p>
            </div>
          </div>
        </div>

        <!-- Avatar Upload -->
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">头像设置</h2>
          <div class="flex flex-col items-center gap-4">
            <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center overflow-hidden border-4 border-gray-200">
              <img v-if="avatarUrl" :src="avatarUrl" class="w-full h-full object-cover" />
              <svg v-else class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
              </svg>
            </div>
            <label class="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg text-sm font-medium cursor-pointer transition">
              选择图片
              <input type="file" accept="image/*" class="hidden" @change="onFileSelected" />
            </label>
            <p v-if="uploadMessage" :class="['text-sm', uploadMessage.includes('成功') ? 'text-green-600' : 'text-red-600']">{{ uploadMessage }}</p>
          </div>
        </div>
      </div>

      <!-- Account Overview -->
      <div v-if="overview" class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center gap-2 mb-6">
          <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">账户概览</h2>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="p-4 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl border border-blue-100">
            <p class="text-sm text-gray-500 mb-1">总资产</p>
            <p class="text-3xl font-bold text-blue-600">{{ formatMoney(overview.totalAssets) }}</p>
          </div>
          <div class="p-4 bg-gray-50 rounded-xl border border-gray-200">
            <div class="flex justify-between items-center">
              <span class="text-sm text-gray-500">总盈亏</span>
              <span :class="['text-lg font-bold', overview.totalProfitLoss >= 0 ? 'text-red-600' : 'text-green-600']">
                {{ formatMoney(overview.totalProfitLoss) }}
              </span>
            </div>
            <p :class="['text-sm mt-1', overview.totalProfitLoss >= 0 ? 'text-red-500' : 'text-green-500']">
              {{ formatPercent(overview.profitLossPercent) }}
            </p>
          </div>
          <div class="p-3 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-500 mb-1">可用资金</p>
            <p class="text-lg font-semibold text-gray-900">{{ formatMoney(overview.availableCash) }}</p>
          </div>
          <div class="p-3 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-500 mb-1">冻结资金</p>
            <p class="text-lg font-semibold text-yellow-600">{{ formatMoney(overview.frozenCash) }}</p>
          </div>
          <div class="p-3 bg-gray-50 rounded-xl">
            <p class="text-xs text-gray-500 mb-1">持仓市值</p>
            <p class="text-lg font-semibold text-gray-900">{{ formatMoney(overview.totalMarketValue) }}</p>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center gap-2 mb-6">
          <div class="w-10 h-10 bg-purple-100 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">快捷操作</h2>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <router-link to="/trading" class="bg-blue-600 hover:bg-blue-700 text-white py-4 rounded-xl transition shadow-lg shadow-blue-200 font-medium text-center">
            买入
          </router-link>
          <router-link to="/trading" class="bg-orange-500 hover:bg-orange-600 text-white py-4 rounded-xl transition shadow-lg shadow-orange-200 font-medium text-center">
            卖出
          </router-link>
          <router-link to="/portfolio" class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium text-center">
            持仓
          </router-link>
          <router-link to="/trading" class="bg-gray-100 hover:bg-gray-200 text-gray-700 py-4 rounded-xl transition font-medium text-center">
            订单
          </router-link>
        </div>

        <div class="mt-6 p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl">
          <p class="text-sm text-gray-500 mb-2">市场动态</p>
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
            <span class="text-sm text-gray-600">市场交易中</span>
          </div>
        </div>
      </div>

      <!-- Position Details -->
      <div class="mt-6 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
        <div class="flex items-center gap-2 mb-6">
          <div class="w-10 h-10 bg-indigo-100 rounded-xl flex items-center justify-center">
            <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <h2 class="text-lg font-semibold text-gray-900">持仓明细</h2>
        </div>

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
                <th class="text-right py-3 px-4 font-medium">持仓数量</th>
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
                <td class="text-right text-gray-600">{{ p.totalQuantity }}</td>
                <td class="text-right text-gray-600">¥{{ p.avgCost.toFixed(2) }}</td>
                <td class="text-right text-gray-600">¥{{ p.currentPrice.toFixed(2) }}</td>
                <td class="text-right font-medium text-gray-900">{{ formatMoney(p.marketValue) }}</td>
                <td :class="['text-right font-medium', p.profitLoss >= 0 ? 'text-red-600' : 'text-green-600']">
                  {{ formatMoney(p.profitLoss) }}
                  <div class="text-xs">{{ formatPercent(p.profitLossPercent) }}</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </template>

    <div v-else class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center text-gray-400">
      <p>无法加载用户信息</p>
    </div>
  </div>
</template>
