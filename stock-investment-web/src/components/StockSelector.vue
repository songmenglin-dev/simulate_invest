<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { searchStocks, getQuote } from '../services/api'

const emit = defineEmits<{
  select: [item: { stockCode: string; stockName: string; currentPrice: number }]
}>()

interface StockInfo {
  id: number
  stockCode: string
  stockName: string
}

const stocks = ref<StockInfo[]>([])
const selectedCode = ref('')

const hotStocks = [
  { stockCode: '600519', stockName: '贵州茅台' },
  { stockCode: '000858', stockName: '五粮液' },
  { stockCode: '600036', stockName: '招商银行' },
  { stockCode: '601318', stockName: '中国平安' },
  { stockCode: '002594', stockName: '比亚迪' },
  { stockCode: '000333', stockName: '美的集团' },
  { stockCode: '600887', stockName: '伊利股份' },
  { stockCode: '000001', stockName: '平安银行' },
]

const handleSelect = async (stockCode: string, stockName: string) => {
  selectedCode.value = stockCode
  try {
    const quote = await getQuote(stockCode)
    emit('select', {
      stockCode,
      stockName,
      currentPrice: quote.currentPrice,
    })
  } catch (err) {
    console.error('Failed to fetch quote for', stockCode, err)
  }
}

const handleDropdownChange = () => {
  if (!selectedCode.value) return
  const stock = stocks.value.find((s) => s.stockCode === selectedCode.value)
  if (stock) {
    handleSelect(stock.stockCode, stock.stockName)
  }
}

const handleHotClick = (stockCode: string, stockName: string) => {
  selectedCode.value = stockCode
  handleSelect(stockCode, stockName)
}

onMounted(async () => {
  try {
    stocks.value = await searchStocks('')
  } catch (err) {
    console.error('Failed to load stocks', err)
  }
})
</script>

<template>
  <div class="space-y-3">
    <!-- Dropdown -->
    <select
      v-model="selectedCode"
      @change="handleDropdownChange"
      class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition text-gray-900"
    >
      <option value="" disabled>请选择股票...</option>
      <option
        v-for="stock in stocks"
        :key="stock.stockCode"
        :value="stock.stockCode"
      >
        {{ stock.stockCode }} - {{ stock.stockName }}
      </option>
    </select>

    <!-- Hot stocks quick-select -->
    <div>
      <p class="text-xs text-gray-500 mb-2">热门股票快速选择</p>
      <div class="flex flex-wrap gap-2">
        <button
          v-for="hs in hotStocks"
          :key="hs.stockCode"
          type="button"
          @click="handleHotClick(hs.stockCode, hs.stockName)"
          class="px-3 py-1.5 text-xs font-medium bg-gray-50 border border-gray-200 rounded-lg hover:bg-blue-50 hover:border-blue-300 hover:text-blue-700 transition"
        >
          {{ hs.stockName }}
        </button>
      </div>
    </div>
  </div>
</template>
