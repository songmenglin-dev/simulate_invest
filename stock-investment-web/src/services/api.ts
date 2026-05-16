const API_BASE = '/api'

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('token')
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: { ...headers, ...options?.headers },
  })

  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  const json: ApiResponse<T> = await res.json()
  if (json.code !== 200) throw new Error(json.message)
  return json.data
}

// Auth
export const login = (data: { username: string; password: string }) =>
  request<{ token: string; userId: number; username: string; avatarUrl?: string }>('/user/login', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const register = (data: { username: string; password: string; email?: string; phone?: string }) =>
  request<number>('/user/register', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const logout = () =>
  request<void>('/user/logout', { method: 'POST' })

export const getProfile = () =>
  request<{ id: number; username: string; email?: string; phone?: string; avatarUrl?: string }>('/user/profile')

// Market
export const searchStocks = (keyword?: string) =>
  request<Array<{ id: number; stockCode: string; stockName: string }>>(`/market/search${keyword ? `?keyword=${keyword}` : ''}`)

export const getQuote = (stockCode: string) =>
  request<{
    stockCode: string
    stockName: string
    currentPrice: number
    change: number
    changePercent: number
    open: number
    high: number
    low: number
    close: number
    volume: number
  }>(`/market/quote/${stockCode}`)

export const getKLine = (stockCode: string, period = 'daily') =>
  request<{
    dates: string[]
    open: number[]
    high: number[]
    low: number[]
    close: number[]
    volume: number[]
  }>(`/market/kline/${stockCode}?period=${period}`)

export const getIndicators = (stockCode: string, period = 'daily') =>
  request<{
    ma5: number
    ma10: number
    ma20: number
    macd: number
    signal: number
    histogram: number
    k: number
    d: number
    j: number
  }>(`/market/indicators/${stockCode}?period=${period}`)

// Portfolio
export const getPortfolioOverview = (userId: number) =>
  request<{
    availableCash: number
    frozenCash: number
    totalMarketValue: number
    totalProfitLoss: number
    profitLossPercent: number
    totalAssets: number
  }>(`/portfolio/overview/${userId}`)

export const getPositions = (userId: number) =>
  request<Array<{
    positionId: number
    stockCode: string
    stockName: string
    totalQuantity: number
    availableQuantity: number
    frozenQuantity: number
    avgCost: number
    currentPrice: number
    marketValue: number
    profitLoss: number
    profitLossPercent: number
  }>>(`/portfolio/positions/${userId}`)

// Trading
export const placeOrder = (data: {
  userId: number
  fundAccountId: number
  stockCode: string
  stockName: string
  direction: 'BUY' | 'SELL'
  price: number
  quantity: number
  orderType: string
}) =>
  request<{ id: number; orderNo: string }>('/order/place', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const getOrderHistory = (userId: number) =>
  request<Array<{
    id: number
    orderNo: string
    stockCode: string
    stockName: string
    direction: string
    price: number
    quantity: number
    amount: number
    status: string
    createTime: string
  }>>(`/order/history/${userId}`)

// Analysis
export const getFinancialOverview = (stockCode: string) =>
  request<{
    stockCode: string
    stockName: string
    pe: number
    pb: number
    marketCap: number
    revenue: number
    netProfit: number
    roe: number
  }>(`/analysis/overview/${stockCode}`)

export const getRevenueTrend = (stockCode: string) =>
  request<{
    stockCode: string
    dates: string[]
    revenues: number[]
    growthRates: number[]
  }>(`/analysis/revenue-trend/${stockCode}`)