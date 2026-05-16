## Context

当前系统采用 Spring Cloud 微服务架构（market-service、trading-service 等），前端为 Vue 3 + TypeScript + Tailwind CSS。行情数据通过 market-service 从 East Money API 获取，缓存到 Redis，并存入 `stock_quotes` 表。交易页面使用 StockSearch 组件进行文本搜索选股。当前配色方案为西方式绿涨红跌，需统一改为中国股市的红涨绿跌规范。

## Goals / Non-Goals

**Goals:**
- 交易页面支持下拉列表和热门股票按钮选股，替代文本输入
- 概览看板展示多只股票的实时价格变化，自动刷新
- 价格波动基于真实数据叠加随机增减（真实数据不可用时纯模拟）
- 全局统一红涨绿跌的中国股市配色

**Non-Goals:**
- 不实现 WebSocket 实时推送（使用轮询替代，后续可升级）
- 不修改交易核心逻辑（下单、确认、取消流程不变）
- 不新增外部依赖

## Decisions

### 1. 价格模拟策略

在 market-service 新增 `/market/simulated-quotes` 端点，接受股票代码列表，返回带随机波动的行情数据。

- 若 Redis 缓存或 `stock_quotes` 表中有最近价格，则以该价格为基准，随机浮动 ±0.3% ~ ±1.5%
- 若没有任何基准价格，则使用数据库中股票的默认价格（或 10.00 兜底），在此基础上随机游走
- 每次请求生成新的随机波动值，模拟真实市场变化

**替代方案**: 前端纯随机生成 → 不可取，后端统一模拟可保证一致性，且方便后续接入真实行情

### 2. 前端选股组件设计

创建 `StockSelector.vue` 组件替代 `StockSearch.vue`：
- 下拉选择器：调用 `/market/search` 获取全量股票列表，支持模糊筛选
- 热门股票快捷按钮：预设 6-8 只热门 A 股（贵州茅台、五粮液、招商银行等），一键选中
- 选中后自动回填当前价格（调用 `/market/quote`）

**替代方案**: 保留下拉但保留搜索 → 选用，下拉列表同时支持输入筛选，兼顾效率与便捷

### 3. 实时看板架构

在 DashboardView 新增"市场看板"区域：
- 使用 `setInterval` 每 3 秒轮询 `/market/simulated-quotes`
- 展示 6-8 只热门股票的卡片：股票名称、当前价格、涨跌幅、涨跌额
- 红涨绿跌配色，价格变动时添加 CSS transition 动画
- 使用 Vue 3 `watchEffect` 或 `useIntervalFn`（VueUse 已有则用，无则手写）

不引入 WebSocket：
- 轮询在 3-5 秒间隔下服务器压力可控（单次请求批量返回多只股票）
- 后续可平滑升级为 SSE/WebSocket

### 4. 配色方案翻转

全局替换涨跌颜色映射：
- **红色系** (`#EF4444`, `text-red-600`, `bg-red-*`): 上涨（涨）
- **绿色系** (`#10B981`, `text-green-600`, `bg-green-*`): 下跌（跌）
- 涉及文件：DashboardView.vue、OrderForm.vue、OrderList.vue、MarketView.vue、KLineChart.vue、PortfolioView.vue、AppLayout.vue
- 配置化处理：在 Tailwind 或 CSS 变量中定义 `--color-up` / `--color-down`，统一引用

**替代方案**: 仅改新组件 → 不可取，新旧配色混用会造成用户困惑

## Risks / Trade-offs

- **[Risk] 配色翻转是 breaking change** → 这是一次性全局替换，所有组件同步修改，不留旧配色残余
- **[Risk] 3 秒轮询可能对服务器造成压力** → 单次批量返回多只股票，market-service 已有 Redis 缓存 + 限流，且模拟端点计算量小
- **[Risk] 随机波动可能与真实市场走势不符** → 标注"模拟数据"提示，避免用户误以为真实行情
- **[Trade-off] 轮询 vs WebSocket** → 轮询实现简单、调试方便，本次先采用轮询，预留升级空间
