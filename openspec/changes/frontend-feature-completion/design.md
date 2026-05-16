## Context

当前前端项目 (`stock-investment-web`) 是 Vue 3 + Vite + Tailwind CSS 构建的单页应用，仅有 `LoginView` 和 `DashboardView` 两个页面。后端 5 个微服务（用户、行情、交易、持仓、分析）均已实现且运行正常，但前端缺乏对应的 UI 页面。`api.ts` 中已定义了 14 个 API 函数，实际仅调用了 4-5 个。

## Goals / Non-Goals

**Goals:**
- 修复顶部导航栏，实现多页面路由系统（含导航守卫）
- 新增行情、交易、持仓、分析、用户共 5 个功能页面
- 集成 ECharts 渲染 K 线图和技术指标
- 所有后端 API 函数在前端有对应的调用和 UI 展示
- 可复用组件的提取（K 线图、财务表格、股票搜索等）

**Non-Goals:**
- 不引入 Pinia/Vuex 状态管理（当前规模不需要）
- 不改动后端任何代码
- 不引入 Element Plus 等 UI 组件库（保持 Tailwind CSS）
- 不处理 SSR/SEO
- 不实现资讯板块（后端暂不支持）

## Decisions

### 1. 路由结构：Shared Layout + Nested Routes

使用 Vue Router 嵌套路由，`AppLayout` 作为共享布局（顶部导航栏 + 内容区），子路由渲染各功能页面。

```
/ (AppLayout)
├── /dashboard       → DashboardView (default)
├── /market          → MarketView
├── /trading         → TradingView
├── /portfolio       → PortfolioView
├── /analysis        → AnalysisView
├── /profile         → ProfileView
/login                → LoginView (独立布局，无导航栏)
```

**选择理由**: 嵌套路由减少导航栏代码重复；`AppLayout` 统一处理认证状态和用户信息。

### 2. 图表库：ECharts

K 线图和营收趋势图使用 ECharts 5.x，通过 `npm install echarts` 引入。

**选择理由**: ECharts 原生支持 K 线图 (candlestick) 类型，中文文档丰富，与 Vue 3 集成成熟。替代方案 (Chart.js) 需额外插件支持 K 线。

### 3. 组件拆分策略

提取以下可复用组件到 `src/components/`：

| 组件 | 功能 | 被使用 |
|------|------|--------|
| `StockSearch.vue` | 股票代码/名称搜索下拉 | MarketView, TradingView, AnalysisView |
| `KLineChart.vue` | K 线图 + 技术指标叠加 | MarketView |
| `OrderForm.vue` | 买卖下单表单 | TradingView |
| `OrderList.vue` | 订单历史列表 | TradingView |
| `FinancialTable.vue` | 财务报表通用表格 | AnalysisView |
| `MetricCard.vue` | 估值指标卡片 | AnalysisView, DashboardView |

**选择理由**: 多个页面共享相同数据展示模式（如 Dashboard 和 Portfolio 都显示持仓信息），复用减少重复代码。

### 4. 导航守卫

```typescript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) {
    next('/login');
  } else if (to.path === '/login' && token) {
    next('/dashboard');
  } else {
    next();
  }
});
```

### 5. API 补全

已有 `api.ts` 中的 14 个函数均保留，补充调用链路：
- `getKLine()` → `KLineChart.vue` → `MarketView.vue`
- `getIndicators()` → `KLineChart.vue` → `MarketView.vue`
- `placeOrder()` → `OrderForm.vue` → `TradingView.vue`
- `getOrderHistory()` → `OrderList.vue` → `TradingView.vue`
- `getFinancialOverview()` → `MetricCard.vue` → `AnalysisView.vue`
- `getRevenueTrend()` → ECharts → `AnalysisView.vue`
- `getProfile()` → `ProfileView.vue`

### 6. 交易确认流程

由于后端 `confirmOrder` 接口需要确认步骤，前端下单后：
1. `placeOrder()` → 订单创建（状态 PENDING）
2. 弹出确认对话框（显示订单详情）
3. 用户确认 → 调用 `confirmOrder()` → 订单执行
4. 用户取消 → 调用 `cancelOrder()` → 订单撤销

## Risks / Trade-offs

- [Risk] ECharts 打包体积较大 (~800KB gzip ~250KB) → Mitigation: 按需引入 `echarts/core`，仅导入 candlestick、line、bar 三种图表类型
- [Risk] 没有状态管理库，跨组件共享数据依赖 props/events → Mitigation: 当前页面间数据独立（行情页不依赖交易页数据），局部状态足够；如后续需要可引入 Pinia
