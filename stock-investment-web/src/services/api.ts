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

export const fetchAllStocks = () =>
  request<Array<{ id: number; stockCode: string; stockName: string }>>('/market/search')

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

export const getSimulatedQuotes = (stockCodes: string[]) =>
  request<Array<{
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
    turnover: number
  }>>('/market/simulated-quotes', {
    method: 'POST',
    body: JSON.stringify(stockCodes),
  })

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
  direction: number
  price: number
  quantity: number
  orderType: number
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
    direction: number
    price: number
    quantity: number
    amount: number
    status: number
    createTime: string
  }>>(`/order/history/${userId}`)

export const confirmOrder = (orderId: number, userId: number) =>
  request<any>(`/order/confirm/${orderId}?userId=${userId}`, { method: 'POST' })

export const cancelOrder = (orderId: number, userId: number) =>
  request<any>(`/order/cancel/${orderId}?userId=${userId}`, { method: 'POST' })

// Analysis
export const getFinancialOverview = (stockCode: string) =>
  request<{
    stockCode: string
    stockName: string
    revenue: number
    netProfit: number
    roe: number
    eps: number
    peRatio: number
    pbRatio: number
    dividendYield: number
    totalAssets: number
    totalLiabilities: number
    shareholdersEquity: number
  }>(`/analysis/overview/${stockCode}`)

export const getIncomeStatement = (stockCode: string) =>
  request<{ revenue: number; netProfit: number; eps: number; reportDate: string }>(`/analysis/income/${stockCode}`)

export const getBalanceSheet = (stockCode: string) =>
  request<{ totalAssets: number; totalLiabilities: number; shareholdersEquity: number; reportDate: string }>(`/analysis/balance/${stockCode}`)

export const getCashFlowStatement = (stockCode: string) =>
  request<{ operatingCashFlow: number; investingCashFlow: number; financingCashFlow: number; netCashFlow: number; reportDate: string }>(`/analysis/cashflow/${stockCode}`)

export const getRevenueTrend = (stockCode: string) =>
  request<{
    stockCode: string
    dates: string[]
    revenues: number[]
    growthRates: number[]
  }>(`/analysis/revenue-trend/${stockCode}`)

// Portfolio Cash
export const getCashBalance = (userId: number) =>
  request<{ availableCash: number; frozenCash: number; totalCash: number; accountNo: string; status: number }>(`/portfolio/cash/${userId}`)

export const deposit = (userId: number, amount: number) =>
  request<{ balance: number; amount: number }>('/portfolio/deposit', {
    method: 'POST',
    body: JSON.stringify({ userId, amount }),
  })

export const withdraw = (userId: number, amount: number) =>
  request<{ balance: number; amount: number }>('/portfolio/withdraw', {
    method: 'POST',
    body: JSON.stringify({ userId, amount }),
  })

// Conditional Orders (条件单)
export const createConditionalOrder = (data: {
  userId: number
  fundAccountId: number
  stockCode: string
  stockName: string
  conditionType: string
  triggerPrice: number
  orderPrice: number
  quantity: number
  direction: number
}) =>
  request<{ id: number; orderNo: string }>('/conditional-order/create', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const getConditionalOrders = (userId: number) =>
  request<Array<{
    id: number
    orderNo: string
    stockCode: string
    stockName: string
    conditionType: string
    triggerPrice: number
    orderPrice: number
    quantity: number
    direction: number
    status: string
    triggeredOrderId: number | null
    failReason: string | null
    createTime: string
    updateTime: string
  }>>(`/conditional-order/list/${userId}`)

export const getConditionalOrderDetail = (id: number) =>
  request<{
    id: number
    orderNo: string
    stockCode: string
    stockName: string
    conditionType: string
    triggerPrice: number
    orderPrice: number
    quantity: number
    direction: number
    status: string
    triggeredOrderId: number | null
    failReason: string | null
    createTime: string
    updateTime: string
  }>(`/conditional-order/${id}`)

export const cancelConditionalOrder = (id: number, userId: number) =>
  request<any>(`/conditional-order/cancel/${id}?userId=${userId}`, { method: 'POST' })

// Watchlist (自选股)
export const addToWatchlist = (data: { userId: number; stockCode: string; stockName: string }) =>
  request<any>('/watchlist/add', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const removeFromWatchlist = (userId: number, stockCode: string) =>
  request<any>(`/watchlist/remove/${userId}/${stockCode}`, { method: 'DELETE' })

export const getWatchlist = (userId: number) =>
  request<Array<{ id: number; stockCode: string; stockName: string; createTime: string }>>(`/watchlist/${userId}`)

export const getWatchlistQuotes = (userId: number) =>
  request<Array<{
    id: number
    stockCode: string
    stockName: string
    currentPrice: number
    change: number
    changePercent: number
    createTime: string
  }>>(`/watchlist/quotes/${userId}`)

// Price Alerts (价格预警)
export const createPriceAlert = (data: {
  userId: number
  stockCode: string
  stockName: string
  alertType: string
  targetPrice: number
}) =>
  request<{ id: number; alertNo: string }>('/alert/create', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const getPriceAlerts = (userId: number, status?: string) =>
  request<Array<{
    id: number
    alertNo: string
    stockCode: string
    stockName: string
    alertType: string
    targetPrice: number
    status: string
    createTime: string
    updateTime: string
  }>>(`/alert/list/${userId}${status ? `?status=${status}` : ''}`)

export const cancelPriceAlert = (id: number, userId: number) =>
  request<any>(`/alert/cancel/${id}?userId=${userId}`, { method: 'POST' })

export const getAlertNotifications = (userId: number) =>
  request<Array<{
    id: number
    alertId: number
    stockCode: string
    stockName: string
    alertType: string
    targetPrice: number
    triggeredPrice: number
    isRead: number
    createTime: string
  }>>(`/alert/notifications/${userId}`)

export const markNotificationRead = (id: number) =>
  request<any>(`/alert/notifications/read/${id}`, { method: 'POST' })

export const markAllNotificationsRead = (userId: number) =>
  request<any>(`/alert/notifications/read-all/${userId}`, { method: 'POST' })

export const getUnreadNotificationCount = (userId: number) =>
  request<number>(`/alert/notifications/unread-count/${userId}`)

// Strategy Backtesting (策略回测)
export const runBacktest = (data: {
  userId: number
  strategyType: string
  stockCode: string
  stockName: string
  startDate: string
  endDate: string
  initialCapital: number
  parameters: Record<string, any>
}) =>
  request<{
    id: number
    resultNo: string
    strategyType: string
    stockCode: string
    stockName: string
    startDate: string
    endDate: string
    initialCapital: number
    finalCapital: number
    totalReturn: number
    annualReturn: number
    maxDrawdown: number
    winRate: number
    totalTrades: number
    winningTrades: number
    sharpeRatio: number
    equityCurveJson: string
    tradesJson: string
  }>('/backtest/run', {
    method: 'POST',
    body: JSON.stringify(data),
  })

export const getBacktestResult = (id: number) =>
  request<{
    id: number
    resultNo: string
    strategyType: string
    stockCode: string
    stockName: string
    startDate: string
    endDate: string
    initialCapital: number
    finalCapital: number
    totalReturn: number
    annualReturn: number
    maxDrawdown: number
    winRate: number
    totalTrades: number
    winningTrades: number
    sharpeRatio: number
    equityCurveJson: string
    tradesJson: string
  }>(`/backtest/result/${id}`)

export const getBacktestHistory = (userId: number) =>
  request<Array<{
    id: number
    resultNo: string
    strategyType: string
    stockCode: string
    stockName: string
    startDate: string
    endDate: string
    totalReturn: number
    totalTrades: number
    createTime: string
  }>>(`/backtest/history/${userId}`)

export const getStrategyTemplates = () =>
  request<Array<{
    type: string
    name: string
    description: string
    parameters: Array<{ name: string; label: string; type: string; defaultValue: any }>
  }>>('/backtest/strategies/templates')