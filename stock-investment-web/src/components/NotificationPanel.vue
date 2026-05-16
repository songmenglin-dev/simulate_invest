<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { getAlertNotifications, markNotificationRead, markAllNotificationsRead } from '../services/api'

const props = defineProps<{ visible: boolean }>()
const emit = defineEmits<{ close: [] }>()

interface Notification {
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

const userId = Number(localStorage.getItem('userId') || '0')
const notifications = ref<Notification[]>([])
const loading = ref(false)

const alertTypeLabel = (type: string) => {
  return type === 'PRICE_ABOVE' ? '上涨' : '下跌'
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

const fetchNotifications = async () => {
  loading.value = true
  try {
    const data = await getAlertNotifications(userId)
    notifications.value = (data || []).slice(0, 20)
  } catch {
    notifications.value = []
  } finally {
    loading.value = false
  }
}

const handleMarkRead = async (notification: Notification) => {
  if (notification.isRead) {
    emit('close')
    return
  }
  try {
    await markNotificationRead(notification.id)
    notification.isRead = 1
    emit('close')
  } catch {
    // silently fail
  }
}

const handleMarkAllRead = async () => {
  const unreadIds = notifications.value.filter(n => !n.isRead).map(n => n.id)
  if (unreadIds.length === 0) return
  try {
    await markAllNotificationsRead(userId)
    notifications.value.forEach(n => { n.isRead = 1 })
  } catch {
    // silently fail
  }
}

const unreadCount = () => notifications.value.filter(n => !n.isRead).length

watch(() => props.visible, (val) => {
  if (val) {
    fetchNotifications()
  }
})

onMounted(() => {
  if (props.visible) {
    fetchNotifications()
  }
})
</script>

<template>
  <div v-if="visible" class="relative">
    <!-- Overlay -->
    <div
      class="fixed inset-0 z-40"
      @click="emit('close')"
    />

    <!-- Panel -->
    <div class="absolute right-0 top-10 z-50 w-96 bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden">
      <!-- Header -->
      <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
        <div class="flex items-center gap-2">
          <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
          </svg>
          <h3 class="font-semibold text-gray-900">通知记录</h3>
          <span v-if="unreadCount() > 0" class="px-2 py-0.5 bg-red-100 text-red-600 text-xs rounded-full font-medium">
            {{ unreadCount() }}条未读
          </span>
        </div>
        <button
          @click="handleMarkAllRead"
          :disabled="unreadCount() === 0"
          class="text-xs text-blue-600 hover:text-blue-700 disabled:text-gray-300 disabled:cursor-not-allowed font-medium transition"
        >
          全部已读
        </button>
      </div>

      <!-- Content -->
      <div class="max-h-96 overflow-y-auto">
        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-12">
          <div class="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin" />
        </div>

        <!-- Empty -->
        <div v-else-if="notifications.length === 0" class="flex flex-col items-center justify-center py-12 text-gray-400">
          <svg class="w-12 h-12 text-gray-200 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
          </svg>
          <p class="text-sm">暂无通知记录</p>
        </div>

        <!-- Notifications List -->
        <div v-else>
          <button
            v-for="notification in notifications"
            :key="notification.id"
            @click="handleMarkRead(notification)"
            class="w-full text-left px-5 py-4 border-b border-gray-50 hover:bg-gray-50 transition flex items-start gap-3"
            :class="{ 'bg-blue-50/50': !notification.isRead }"
          >
            <!-- Unread dot -->
            <div class="mt-1.5 flex-shrink-0">
              <span
                class="block w-2 h-2 rounded-full"
                :class="notification.isRead ? 'bg-gray-300' : 'bg-blue-500'"
              />
            </div>

            <!-- Content -->
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
                  目标价 {{ notification.targetPrice.toFixed(2) }} &rarr; 触发价 {{ notification.triggeredPrice.toFixed(2) }}
                </span>
              </div>
              <p class="text-xs text-gray-400">
                {{ formatTime(notification.createTime) }}
              </p>
            </div>

            <!-- Unread badge -->
            <span
              v-if="!notification.isRead"
              class="flex-shrink-0 px-1.5 py-0.5 bg-blue-100 text-blue-600 text-xs rounded font-medium"
            >
              未读
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
