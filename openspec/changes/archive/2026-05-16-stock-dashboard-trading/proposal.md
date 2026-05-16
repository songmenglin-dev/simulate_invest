## Why

当前交易页面需要用户手动输入股票代码才能交易，操作繁琐且容易出错。同时，平台缺少一个能够实时展示股票价格变化的数据看板，用户无法直观地感知市场波动。需要将交易选股方式改为下拉/按钮选择，并新增实时价格看板，提升交易效率和用户体验。

## What Changes

- **交易页面选股方式**：从文本输入搜索改为下拉列表 + 热门股票快捷按钮选择，用户无需记忆股票代码即可快速下单
- **新增实时数据看板**：在概览页面或独立看板中展示多只股票的实时价格变化，价格以随机模拟波动的方式动态更新
- **价格模拟机制**：若行情数据来源于真实数据库（East Money API），则在真实价格基础上叠加随机增减；若真实数据不可用，则使用纯随机模拟
- **中国股市配色规范**：红色代表上涨，绿色代表下跌（与当前 DashboardView 中的 Western 配色相反）
- **自动刷新**：看板数据定时自动刷新，无需手动操作

## Capabilities

### New Capabilities

- `stock-dashboard`: 实时股票数据看板，展示多只股票的价格变化，支持自动刷新和随机价格波动模拟，采用红涨绿跌的中国股市配色
- `trading-ui-enhancement`: 交易页面选股方式改造，用下拉列表和热门股票快捷按钮替代文本输入搜索

### Modified Capabilities

- `market-data`: 新增实时价格模拟端点，支持在真实行情基础上叠加随机波动；更新股价展示配色为红涨绿跌
- `stock-trading`: 选股交互方式从文本搜索改为下拉选择 + 快捷按钮

## Impact

- **前端**：DashboardView.vue（新增实时看板区域）、TradingView.vue / OrderForm.vue（选股方式改造）、StockSearch.vue（可能废弃或用下拉组件替代）
- **后端 market-service**：MarketController 新增模拟价格端点，MarketService 新增随机波动计算逻辑
- **配色全局影响**：所有展示涨跌的组件（KLineChart、DashboardView、OrderList、Portfolio 相关组件等）需要统一改为红涨绿跌
- **新增依赖**：无，使用现有 ECharts、Vue 3 响应式能力实现
