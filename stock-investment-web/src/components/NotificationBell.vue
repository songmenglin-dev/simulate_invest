<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getUnreadNotificationCount } from '../services/api'

const emit = defineEmits<{ click: [] }>()
const unreadCount = ref(0)
const userId = Number(localStorage.getItem('userId') || '0')
let timer: ReturnType<typeof setInterval> | null = null

const fetchCount = async () => {
  try {
    const count = await getUnreadNotificationCount(userId)
    unreadCount.value = typeof count === 'number' ? count : 0
  } catch {
    // silently fail, keep previous count
  }
}

onMounted(() => {
  fetchCount()
  timer = setInterval(fetchCount, 30000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<template>
  <button
    @click="emit('click')"
    class="relative p-2 text-gray-600 hover:text-blue-600 rounded-lg hover:bg-blue-50 transition"
    title="通知"
  >
    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
      />
    </svg>
    <span
      v-if="unreadCount > 0"
      class="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-bold animate-pulse"
    >
      {{ unreadCount > 99 ? '99+' : unreadCount }}
    </span>
  </button>
</template>
