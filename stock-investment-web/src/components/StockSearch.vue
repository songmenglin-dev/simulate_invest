<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { searchStocks } from '../services/api'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits(['update:modelValue', 'select'])

const keyword = ref('')
const results = ref<Array<{ stockCode: string; stockName: string }>>([])
const showDropdown = ref(false)

const doSearch = async () => {
  if (!keyword.value.trim()) {
    results.value = []
    return
  }
  try {
    results.value = await searchStocks(keyword.value)
    showDropdown.value = true
  } catch (err) {
    console.error(err)
  }
}

const selectStock = (item: { stockCode: string; stockName: string }) => {
  emit('update:modelValue', item.stockCode)
  emit('select', item)
  keyword.value = `${item.stockCode} - ${item.stockName}`
  showDropdown.value = false
}

let blurTimer: ReturnType<typeof setTimeout> | null = null
const handleBlur = () => {
  blurTimer = setTimeout(() => { showDropdown.value = false }, 200)
}
const handleFocus = () => {
  if (blurTimer) clearTimeout(blurTimer)
  showDropdown.value = results.value.length > 0
}

watch(keyword, () => {
  doSearch()
})

onMounted(() => {
  if (props.modelValue) {
    keyword.value = props.modelValue
  }
})
</script>

<template>
  <div class="relative">
    <input
      v-model="keyword"
      type="text"
      placeholder="输入股票代码或名称搜索..."
      class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition"
      @focus="handleFocus"
      @blur="handleBlur"
    />
    <div
      v-if="showDropdown && results.length > 0"
      class="absolute z-10 top-full mt-1 w-full bg-white border border-gray-200 rounded-xl shadow-lg max-h-60 overflow-y-auto"
    >
      <div
        v-for="item in results"
        :key="item.stockCode"
        class="px-4 py-3 hover:bg-blue-50 cursor-pointer flex justify-between items-center border-b border-gray-50 last:border-0"
        @click="selectStock(item)"
      >
        <span class="font-medium text-gray-900">{{ item.stockCode }}</span>
        <span class="text-gray-500 text-sm">{{ item.stockName }}</span>
      </div>
    </div>
  </div>
</template>
