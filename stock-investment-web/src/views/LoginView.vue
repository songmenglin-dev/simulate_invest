<script setup lang="ts">
import { ref } from 'vue'
import { login, register } from '../services/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLogin = ref(true)
const form = ref({ username: '', password: '', email: '', phone: '' })
const loading = ref(false)
const error = ref('')

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  try {
    const data = isLogin.value
      ? await login({ username: form.value.username, password: form.value.password })
      : await register({ username: form.value.username, password: form.value.password, email: form.value.email, phone: form.value.phone })
    localStorage.setItem('token', (data as any).token || '')
    localStorage.setItem('userId', String((data as any).userId || ''))
    router.push('/')
  } catch (err: any) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center px-4">
    <div class="w-full max-w-md">
      <!-- Logo & Title -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-blue-600 rounded-2xl mb-4 shadow-lg shadow-blue-200">
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
          </svg>
        </div>
        <h1 class="text-2xl font-bold text-gray-900">股票投资平台</h1>
        <p class="text-gray-500 mt-1">专业投资理财服务</p>
      </div>

      <!-- Login Card -->
      <div class="bg-white rounded-2xl shadow-xl shadow-blue-100/50 p-8">
        <h2 class="text-xl font-semibold text-gray-800 mb-6">
          {{ isLogin ? '欢迎登录' : '创建账户' }}
        </h2>

        <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-xl mb-6 text-sm flex items-center gap-2">
          <svg class="w-5 h-5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
          {{ error }}
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-5">
          <div>
            <label class="text-sm font-medium text-gray-700 block mb-2">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="请输入用户名"
              required
            />
          </div>

          <div>
            <label class="text-sm font-medium text-gray-700 block mb-2">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
              placeholder="请输入密码"
              required
            />
          </div>

          <template v-if="!isLogin">
            <div>
              <label class="text-sm font-medium text-gray-700 block mb-2">邮箱</label>
              <input
                v-model="form.email"
                type="email"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                placeholder="请输入邮箱"
              />
            </div>

            <div>
              <label class="text-sm font-medium text-gray-700 block mb-2">手机号</label>
              <input
                v-model="form.phone"
                type="tel"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                placeholder="请输入手机号"
              />
            </div>
          </template>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 rounded-xl transition shadow-lg shadow-blue-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="loading" class="flex items-center justify-center gap-2">
              <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"/>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/>
              </svg>
              处理中...
            </span>
            <span v-else>{{ isLogin ? '登 录' : '注 册' }}</span>
          </button>
        </form>

        <p class="text-center text-gray-500 text-sm mt-6">
          {{ isLogin ? '还没有账户？' : '已有账户？' }}
          <button
            type="button"
            @click="isLogin = !isLogin; error = ''"
            class="text-blue-600 hover:text-blue-700 font-medium ml-1"
          >
            {{ isLogin ? '立即注册' : '去登录' }}
          </button>
        </p>
      </div>

      <!-- Footer -->
      <p class="text-center text-gray-400 text-sm mt-6">
        登录即表示同意
        <a href="#" class="text-blue-600 hover:underline">服务条款</a>
        和
        <a href="#" class="text-blue-600 hover:underline">隐私政策</a>
      </p>
    </div>
  </div>
</template>