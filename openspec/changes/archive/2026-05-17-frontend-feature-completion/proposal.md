## Why

当前 Vue 前端仅为早期原型：仅有一个登录页和单一 Dashboard，顶部导航栏链接均为死链接（href="#"），大量后端 API 函数已在 `api.ts` 中定义但从未被调用。需要补全前端页面和交互，使用户能通过 UI 访问所有后端已实现的功能，以便验证项目整体功能完成情况。

## What Changes

- 修复顶部导航栏，使所有导航链接可点击并跳转到对应页面
- 新增股票行情页面：K 线图表、技术指标（MA/MACD/KDJ）可视化
- 新增交易页面：买卖下单表单、订单确认/取消、订单历史列表
- 新增持仓详情页面：持仓盈亏明细、资金账户信息
- 新增财务分析页面：营收趋势图、利润表/资产负债表/现金流量表
- 新增个人信息页面：用户资料展示、头像上传

## Capabilities

### New Capabilities

- `frontend-navigation`: 修复顶部导航栏，实现多页面路由跳转，包含导航守卫和激活状态
- `frontend-market`: 行情页面 — K 线图表渲染（ECharts）、技术指标切换显示、股票搜索
- `frontend-trading`: 交易页面 — 买卖下单、订单确认、订单撤单、订单历史
- `frontend-portfolio`: 持仓页面 — 持仓列表、盈亏计算展示、资金账户详情
- `frontend-analysis`: 分析页面 — 财务概览、营收趋势图、三大财务报表数据展示
- `frontend-user`: 用户页面 — 个人资料展示、头像上传

### Modified Capabilities

<!-- 无现有前端 specs 需要修改，均为新增 -->

## Impact

- 影响项目: `stock-investment-web/` (Vue 3 + Vite + Tailwind CSS 前端项目)
- 新增 views: `MarketView`, `TradingView`, `PortfolioView`, `AnalysisView`, `ProfileView`
- 修改 views: `DashboardView`, `LoginView`
- 新增 components: K线图表、股票搜索、下单表单、财务表格等可复用组件
- 修改 router: 新增 5+ 路由
- 依赖: ECharts（K 线图/趋势图）
