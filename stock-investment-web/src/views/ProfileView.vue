<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProfile } from '../services/api'

const user = ref<any>(null)
const loading = ref(true)
const avatarUrl = ref('')
const uploadMessage = ref('')

onMounted(async () => {
  try {
    user.value = await getProfile()
    avatarUrl.value = user.value?.avatarUrl || ''
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
    </template>

    <div v-else class="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center text-gray-400">
      <p>无法加载用户信息</p>
    </div>
  </div>
</template>
